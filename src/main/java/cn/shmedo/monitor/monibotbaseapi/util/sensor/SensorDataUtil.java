package cn.shmedo.monitor.monibotbaseapi.util.sensor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AvgDensityType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.RainDensityType;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorHistoryAvgDataResponse;

import java.time.LocalDateTime;
import java.util.*;

public class SensorDataUtil {

    /**
     * 根据密度去重新处理平均数据,如果以月进行处理,最终数据就是以传感器和月为维度,每个传感器每个月只展示一条数据
     *
     * @param dataList          传感器数据列表
     * @param density           密度(0:全部 1:日 2:月, 3:年)
     * @param monitorTypeFields 查询子类型
     * @return
     */
    public static List<SensorHistoryAvgDataResponse> handleDataList(List<Map<String, Object>> dataList, Integer density,
                                                                    List<TbMonitorTypeField> monitorTypeFields,
                                                                    List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList) {
        // 生成每个传感器每个月份的平均数据
        List<Map<String, Object>> averageDataList = new ArrayList<>();
        // 创建用于存储按传感器和月份统计结果的Map
        Map<Integer, Map<String, Map<String, Double>>> sensorDataStatistics = new HashMap<>();

        if (density == AvgDensityType.MONTHLY.getValue() || density == AvgDensityType.YEARLY.getValue()) {
            // 遍历数据列表进行统计
            for (Map<String, Object> data : dataList) {
                Integer sensorID = (Integer) data.get("sensorID");
                LocalDateTime time = LocalDateTimeUtil.parse((String) data.get("time"), "yyyy-MM-dd HH:mm:ss.SSS");
                String key = null;

                if (density == AvgDensityType.MONTHLY.getValue()) {
                    key = DateUtil.format(time, "yyyy-MM");
                } else if (density == AvgDensityType.YEARLY.getValue()) {
                    key = DateUtil.format(time, "yyyy");
                }

                // 初始化当前传感器和时间段的统计数据
                Map<String, Map<String, Double>> sensorData = sensorDataStatistics.computeIfAbsent(sensorID, k -> new HashMap<>());
                Map<String, Double> timeData = sensorData.computeIfAbsent(key, k -> new HashMap<>());

                // 累计对应传感器和月份的数据key
                monitorTypeFields.forEach(item -> {
                    timeData.merge(item.getFieldToken(), ((Number) data.get(item.getFieldToken())).doubleValue(), Double::sum);
                });
            }

            for (Map.Entry<Integer, Map<String, Map<String, Double>>> sensorEntry : sensorDataStatistics.entrySet()) {
                Integer sensorID = sensorEntry.getKey();
                Map<String, Map<String, Double>> sensorData = sensorEntry.getValue();

                for (Map.Entry<String, Map<String, Double>> monthEntry : sensorData.entrySet()) {
                    String timeKey = monthEntry.getKey();
                    Map<String, Double> monthData = monthEntry.getValue();
                    Map<String, Object> averageData = new HashMap<>();
                    int dataCount = (int) dataList.stream()
                            .filter(d -> sensorID.equals(d.get("sensorID")))
                            .filter(d -> {
                                LocalDateTime dateTime = LocalDateTimeUtil.parse((String) d.get("time"), "yyyy-MM-dd HH:mm:ss.SSS");
                                if (density == AvgDensityType.MONTHLY.getValue()) {
                                    return timeKey.equals(DateUtil.format(dateTime, "yyyy-MM"));
                                } else if (density == AvgDensityType.YEARLY.getValue()) {
                                    return timeKey.equals(DateUtil.format(dateTime, "yyyy"));
                                }
                                return false;
                            })
                            .count();


                    monitorTypeFields.forEach(item -> {
                        double totalValue = monthData.getOrDefault(item.getFieldToken(), 0.0);
                        double averageValue = totalValue / dataCount;
                        averageData.put(item.getFieldToken(), NumberUtil.round(averageValue, 2).doubleValue());
                    });


                    // 创建新的数据对象，并添加到平均数据列表
                    averageData.put("sensorID", sensorID);
                    if (density == AvgDensityType.MONTHLY.getValue()) {
                        averageData.put("time", timeKey + "-01 00:00:00.000");
                    } else if (density == AvgDensityType.YEARLY.getValue()) {
                        averageData.put("time", timeKey + "-01-01 00:00:00.000");
                    }
                    averageDataList.add(averageData);
                }
            }
        }

        List<SensorHistoryAvgDataResponse> responseList = new LinkedList<>();
        List<Map<String, Object>> selectedList = (density == AvgDensityType.ALL.getValue() ||
                density == AvgDensityType.DAILY.getValue()) ? dataList : averageDataList;

        selectedList.forEach(item -> {
            int sensorID = (int) item.get("sensorID");
            String dateString = (String) item.get("time");
            DateTime time = DateUtil.parse(dateString);

            monitorTypeFields.forEach(pojo -> {
                double value = (double) item.get(pojo.getFieldToken());
                item.put(pojo.getFieldToken(), NumberUtil.round(value, 2).doubleValue());
            });

            SensorHistoryAvgDataResponse response = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getSensorID() == sensorID)
                    .findFirst()
                    .orElse(null);

            assert response != null;
            SensorHistoryAvgDataResponse cloneVo = ObjectUtil.clone(response);
            cloneVo.setSensorData(item);
            cloneVo.setTime(time);
            responseList.add(cloneVo);
        });

        return responseList;
    }


    /**
     * 根据密度去重新处理雨量累加数据,如果以月进行处理,最终数据就是以传感器和月为维度,每个传感器每个月只展示一条数据
     * (雨量专属)
     * @param dataList          传感器数据列表
     * @param density           密度(0:全部 1:日 2:月, 3:年 [4,5,6,7]:小时)
     * @param monitorTypeFields 查询子类型
     * @return
     */
    public static List<SensorHistoryAvgDataResponse> handleRainDataList(List<Map<String, Object>> dataList, Integer density,
                                                                        List<TbMonitorTypeField> monitorTypeFields,
                                                                        List<SensorHistoryAvgDataResponse> sensorHistoryAvgDataResponseList) {
        // 生成每个传感器每个月份的平均数据
        List<Map<String, Object>> sumDataList = new ArrayList<>();
        // 创建用于存储按传感器和月份统计结果的Map
        Map<Integer, Map<String, Map<String, Double>>> sensorDataStatistics = new HashMap<>();


        if (density == RainDensityType.MONTHLY.getValue() || density == RainDensityType.YEARLY.getValue()) {
            TbMonitorTypeField dailyRainfall = monitorTypeFields.stream().filter(m -> m.getFieldToken().equals("dailyRainfall")).findFirst().orElse(null);

            // 遍历数据列表进行统计
            for (Map<String, Object> data : dataList) {
                Integer sensorID = (Integer) data.get("sensorID");
                LocalDateTime time = LocalDateTimeUtil.parse((String) data.get("time"), "yyyy-MM-dd HH:mm:ss.SSS");
                String key = null;

                if (density == RainDensityType.MONTHLY.getValue()) {
                    key = DateUtil.format(time, "yyyy-MM");
                } else if (density == RainDensityType.YEARLY.getValue()) {
                    key = DateUtil.format(time, "yyyy");
                }

                // 初始化当前传感器和时间段的统计数据
                Map<String, Map<String, Double>> sensorData = sensorDataStatistics.computeIfAbsent(sensorID, k -> new HashMap<>());
                Map<String, Double> timeData = sensorData.computeIfAbsent(key, k -> new HashMap<>());

                // 累计对应传感器和月份的数据key
                timeData.merge(dailyRainfall.getFieldToken(), ((Number) data.get(dailyRainfall.getFieldToken())).doubleValue(), Double::sum);

            }

            for (Map.Entry<Integer, Map<String, Map<String, Double>>> sensorEntry : sensorDataStatistics.entrySet()) {
                Integer sensorID = sensorEntry.getKey();
                Map<String, Map<String, Double>> sensorData = sensorEntry.getValue();

                for (Map.Entry<String, Map<String, Double>> monthEntry : sensorData.entrySet()) {
                    String timeKey = monthEntry.getKey();
                    Map<String, Double> monthData = monthEntry.getValue();
                    Map<String, Object> sumData = new HashMap<>();

                    TbMonitorTypeField rainfall = monitorTypeFields.stream().filter(m -> m.getFieldToken().equals("dailyRainfall")).findFirst().orElse(null);

                    double totalValue = monthData.getOrDefault(rainfall.getFieldToken(), 0.0);
                    sumData.put(rainfall.getFieldToken(), NumberUtil.round(totalValue, 2).doubleValue());
                    sumData.put("rainfall", NumberUtil.round(totalValue, 2).doubleValue());

                    // 创建新的数据对象，并添加到数据列表
                    sumData.put("sensorID", sensorID);
                    if (density == AvgDensityType.MONTHLY.getValue()) {
                        sumData.put("time", timeKey + "-01 00:00:00.000");
                    } else if (density == AvgDensityType.YEARLY.getValue()) {
                        sumData.put("time", timeKey + "-01-01 00:00:00.000");
                    }
                    sumDataList.add(sumData);
                }
            }
        }

        // 如果密度:(全部,日,小时)取dataList,反之取sumDataList
        List<SensorHistoryAvgDataResponse> responseList = new LinkedList<>();
        List<Map<String, Object>> selectedList = (density == RainDensityType.ALL.getValue() ||
                density == RainDensityType.DAILY.getValue() || density == RainDensityType.ONE_HOURS.getValue() ||
                density == RainDensityType.THREE_HOURS.getValue() || density == RainDensityType.SIX_HOURS.getValue() ||
                density == RainDensityType.TWELVE_HOURS.getValue()
        ) ? dataList : sumDataList;

        selectedList.forEach(item -> {
            int sensorID = (int) item.get("sensorID");
            String dateString = (String) item.get("time");
            DateTime time = DateUtil.parse(dateString);
            // 使用rainfall,来代表累计降雨量
            if (density == RainDensityType.DAILY.getValue() || density == RainDensityType.MONTHLY.getValue()
                    || density == RainDensityType.YEARLY.getValue()) {
                item.put("rainfall", item.get("dailyRainfall"));
            } else {
                item.put("rainfall", item.get("periodRainfall"));
            }

            double value = (double) item.get("rainfall");
            item.put("rainfall", NumberUtil.round(value, 2).doubleValue());

            SensorHistoryAvgDataResponse response = sensorHistoryAvgDataResponseList.stream()
                    .filter(pojo -> pojo.getSensorID() == sensorID)
                    .findFirst()
                    .orElse(null);

            assert response != null;
            SensorHistoryAvgDataResponse cloneVo = ObjectUtil.clone(response);
            cloneVo.setSensorData(item);
            cloneVo.setTime(time);
            responseList.add(cloneVo);
        });

        return responseList;
    }


}
