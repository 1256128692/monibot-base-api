package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;

import java.text.DecimalFormat;
import java.util.*;

public class InfluxDBDataUtil {

    public static List<Map<String, Object>> calculateStatistics(List<Map<String, Object>> sensorData, Integer densityType, Integer statisticsType) {
        // 根据densityType进行分组
        Map<String, List<Map<String, Object>>> groupedData = groupByDensityType(sensorData, densityType);

        List<Map<String, Object>> result = new ArrayList<>();
        // 遍历分组后的数据进行处理
        groupedData.forEach((group, data) -> {
            Map<String, Object> statistics = new HashMap<>();
            if (CollUtil.isNotEmpty(data)) {
                // 根据statisticsType进行数据统计
                switch (statisticsType) {
                    case 1:
                        // 最新值，取最后一条数据
                        statistics = calculateLatestValue(data, densityType);
                        break;
                    case 2:
                        // 平均值
                        statistics = calculateAverage(data, densityType);
                        break;
                    case 3:
                    case 4:
                        // 求和
                        statistics = calculateSum(data, densityType);
                        break;
                    default:
                        break;
                }
            }
            if (MapUtil.isNotEmpty(statistics)) {
                result.add(statistics);
            }

        });

        // 按照时间倒序排序
        result.sort((o1, o2) -> {
            Date time1 = MapUtil.getDate(o1, "time");
            Date time2 = MapUtil.getDate(o2, "time");
            return time2.compareTo(time1);
        });

        return result;
    }

    /**
     * 计算最新值
     *
     * @param sensorData
     * @return
     */
    private static Map<String, Object> calculateLatestValue(List<Map<String, Object>> sensorData, Integer densityType) {
        Map<String, Object> result = new HashMap<>();

        if (CollUtil.isNotEmpty(sensorData)) {
            // 过滤掉为null的数据
            sensorData.removeIf(map -> {
                // 如果有任何业务字段为null，则过滤掉
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (!entry.getKey().equals("sensorID") && !entry.getKey().equals("time") && entry.getValue() == null) {
                        return true;
                    }
                }
                return false;
            });
            if (CollUtil.isNotEmpty(sensorData)) {
                // 根据时间最新进行筛选
                sensorData.sort(Comparator.comparing(m -> MapUtil.getStr(m, "time")));
                Collections.reverse(sensorData);

                // 获取最新一条数据
                Map<String, Object> latestData = sensorData.get(0);
                // 设置传感器ID
                latestData.put("sensorID", MapUtil.getInt(sensorData.get(0), "sensorID"));

                // 如果是周密度，设置时间为当周的第一天
                if (densityType == 4) {
                    Date weekStartDate = DateUtil.beginOfWeek(MapUtil.getDate(latestData, "time"));
                    latestData.put("time", weekStartDate);
                }
                if (densityType == 5) {
                    Date weekStartDate = DateUtil.beginOfMonth(MapUtil.getDate(latestData, "time"));
                    latestData.put("time", weekStartDate);
                }
                if (densityType == 6) {
                    latestData.put("time", DateUtil.beginOfYear(DateUtil.parse(MapUtil.getStr(latestData, "time"))));
                }
                result.putAll(latestData);
            }
        }

        return result;
    }

    /**
     * 计算平均值
     *
     * @param sensorData
     * @return
     */
    private static Map<String, Object> calculateAverage(List<Map<String, Object>> sensorData, Integer densityType) {
        Map<String, Object> result = new HashMap<>();

        if (CollUtil.isNotEmpty(sensorData)) {
            Map<String, List<Double>> valueMap = new HashMap<>();

            // 遍历数据，对每个字段进行平均值计算
            for (Map<String, Object> data : sensorData) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String field = entry.getKey();
                    if (!"time".equals(field) && !"sensorID".equals(field)) {
                        double value = MapUtil.getDouble(data, field, 0.0);
                        valueMap.computeIfAbsent(field, k -> new ArrayList<>()).add(value);
                    }
                }
            }

            // 计算每个字段的平均值，并设置到结果中
            valueMap.forEach((field, values) -> {
                double sum = values.stream().mapToDouble(Double::doubleValue).sum();
                double average = sum / values.size();
                // 保留3位小数
                result.put(field, new DecimalFormat("#.###").format(average));
            });
            // 设置传感器ID
            result.put("sensorID", MapUtil.getInt(sensorData.get(0), "sensorID"));
            // 如果是周密度，设置时间为当周的第一天
            if (densityType == 4) {
                Date weekStartDate = DateUtil.beginOfWeek(MapUtil.getDate(sensorData.get(0), "time"));
                result.put("time", weekStartDate);
            }
            if (densityType == 5) {
                Date weekStartDate = DateUtil.beginOfMonth(MapUtil.getDate(sensorData.get(0), "time"));
                result.put("time", weekStartDate);
            }
            if (densityType == 6) {
                result.put("time", DateUtil.beginOfYear(DateUtil.parse(MapUtil.getStr(sensorData.get(0), "time"))));
            }
        }

        return result;
    }

    /**
     * 计算累加值
     *
     * @param sensorData
     * @return
     */
    private static Map<String, Object> calculateSum(List<Map<String, Object>> sensorData, Integer densityType) {
        Map<String, Object> result = new HashMap<>();

        if (CollUtil.isNotEmpty(sensorData)) {
            // 过滤掉为null的数据
            sensorData.removeIf(map -> {
                // 如果有任何业务字段为null，则过滤掉
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    if (!entry.getKey().equals("sensorID") && !entry.getKey().equals("time") && entry.getValue() == null) {
                        return true;
                    }
                }
                return false;
            });

            if (CollUtil.isNotEmpty(sensorData)) {
                // 根据业务字段求和
                Map<String, Double> sumMap = new HashMap<>();
                for (Map<String, Object> data : sensorData) {
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        if (!entry.getKey().equals("sensorID") && !entry.getKey().equals("time")) {
                            Double value = MapUtil.getDouble(data, entry.getKey(), 0.0);
                            sumMap.put(entry.getKey(), sumMap.getOrDefault(entry.getKey(), 0.0) + value);
                        }
                    }
                }

                // 设置结果
                result.putAll(sumMap);

                // 如果是周密度，设置时间为当周的第一天
                if (densityType == 4) {
                    Date weekStartDate = DateUtil.beginOfWeek(MapUtil.getDate(sensorData.get(0), "time"));
                    result.put("time", weekStartDate);
                }
                if (densityType == 5) {
                    Date weekStartDate = DateUtil.beginOfMonth(MapUtil.getDate(sensorData.get(0), "time"));
                    result.put("time", weekStartDate);
                }
                if (densityType == 6) {
                    result.put("time", DateUtil.beginOfYear(DateUtil.parse(MapUtil.getStr(sensorData.get(0), "time"))));
                }
                // 设置传感器ID
                result.put("sensorID", MapUtil.getInt(sensorData.get(0), "sensorID"));
            }
        }

        // 保留三位小数
        result.forEach((key, value) -> {
            if (value instanceof Double) {
                result.put(key, NumberUtil.round((Double) value, 3).doubleValue());
            }
        });
        return result;
    }

    private static Map<String, List<Map<String, Object>>> groupByDensityType(List<Map<String, Object>> sensorData, int densityType) {
        Map<String, List<Map<String, Object>>> groupedData = new HashMap<>();

        for (Map<String, Object> data : sensorData) {
            String key;
            switch (densityType) {
                case 4:
                    // 周密度，按照周的起始日期分组
                    key = DateUtil.format(DateUtil.beginOfWeek(MapUtil.getDate(data, "time")), "yyyy-MM-dd");
                    break;
                case 5:
                    // 月密度，按照月份分组
                    key = DateUtil.format(DateUtil.beginOfMonth(MapUtil.getDate(data, "time")), "yyyy-MM");
                    break;
                case 6:
                    // 年密度，按照年份分组
                    key = DateUtil.format(DateUtil.beginOfYear(MapUtil.getDate(data, "time")), "yyyy");
                    break;
                default:
                    // 其他情况，按照日期分组
                    key = DateUtil.format(MapUtil.getDate(data, "time"), "yyyy-MM-dd");
                    break;
            }

            groupedData.computeIfAbsent(key, k -> new ArrayList<>()).add(data);
        }

        return groupedData;
    }


}
