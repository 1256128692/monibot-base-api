package cn.shmedo.monitor.monibotbaseapi.dao.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.util.FieldUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.util.MonitorTypeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.sensor.InfluxSensorDataUtil;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SensorDataDaoImpl implements SensorDataDao {
    private InfluxDB influxDB;
    private FileConfig fileConfig;

    public SensorDataDaoImpl(@Autowired @Qualifier("iotDb") InfluxDB influxDB, FileConfig fileConfig) {
        this.influxDB = influxDB;
        this.fileConfig = fileConfig;
    }

    @Override
    public List<Map<String, Object>> querySensorNewData(List<Integer> sensorIDList,
                                                        List<FieldSelectInfo> fieldSelectInfoList,
                                                        boolean raw, Integer monitorType) {
        return querySensorNewDataByCondition(sensorIDList, fieldSelectInfoList, raw, null, monitorType);
    }

    @Override
    public List<Map<String, Object>> querySensorNewDataBetween(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList, boolean raw, Timestamp begin, Timestamp end) {
        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, false);

        List<String> selectField = FieldUtil.getSelectField(fieldSelectInfoList, false);
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        String fieldString = String.join(",", selectField);
        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        StringBuilder sqlBuilder = new StringBuilder();
        sensorIDList.forEach(sid -> {
            String sidSql = " select  " + fieldString + " from  " + measurement
                    + " where sid='" + sid.toString() + "' and time>='" + beginString + "' and time<='" + endString
                    + "'  order by time desc limit 1 tz('Asia/Shanghai') ; ";
            sqlBuilder.append(sidSql);
        });
        String sql = sqlBuilder.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }

    @Override
    public List<Map<String, Object>> querySensorDayStatisticsData(List<Integer> sensorIDList, Timestamp begin,
                                                                  Timestamp end, List<FieldSelectInfo> fieldSelectInfoList,
                                                                  boolean raw) {
        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, false);
        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));
        List<String> selectField = FieldUtil.getSelectField(fieldSelectInfoList, true);

        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<'" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sidOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(1d) fill(none) ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        String sql = sqlBuilder.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, null);
    }

    @Override
    public List<Map<String, Object>> querySensorData(List<Integer> sensorIDList, Timestamp begin, Timestamp end,
                                                     String density, List<FieldSelectInfo> fieldSelectInfoList,
                                                     boolean raw, Integer monitorType) {
        Boolean flag = true;
        if (StringUtils.isNotBlank(density) && DbConstant.DENSITY_DAY.equals(density)) {
            flag = true;
        } else {
            flag = false;
        }
        // 截取最后一个字母,以此来判断查询密度,h:代表小时,d:代表天
        String endStr = density.substring(density.length() - 1, density.length());

        String measurement = MonitorTypeUtil.getMeasurement(monitorType, raw, flag);
        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));

        List<String> selectField = new LinkedList<>();
        fieldSelectInfoList.forEach(item -> {
            selectField.add(item.getFieldToken());
        });

        String sql = null;
        switch (endStr) {
            case "":
                sql = getAllSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
                break;
            case DbConstant.DENSITY_HOUR:
                sql = getHourSensorDataSql(sidOrString, beginString, endString, measurement, selectField, density, monitorType);
                break;
            case DbConstant.DENSITY_DAY:
                sql = getDaySensorDataSql(sidOrString, beginString, endString, measurement, selectField, density);
                break;
        }
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }

    @Override
    public List<Map<String, Object>> querySensorRainStatisticsData(List<Map<String, Object>> dataMaps, Timestamp begin, Timestamp end, List<FieldSelectInfo> fieldSelectInfoList, Integer monitorType) {

        String measurement = MonitorTypeUtil.getMeasurement(monitorType, false, false);
        List<String> selectField = new LinkedList<>();
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        // 当前雨量
        selectField.add(DbConstant.CURRENT_RAIN_FALL);

        StringBuilder sql = new StringBuilder();
        for (Map<String, Object> map : dataMaps) {
            StringBuilder mapSql = new StringBuilder();
            Date time = DateUtil.parse(map.get("time").toString(), "yyyy-MM-dd HH:mm:ss.SSS");
            Timestamp startTime, endTime;

            if (DateUtil.hour(time, true) >= 8) {
                // 如果时间在当天8点之后，endTime为该time，startTime为time当天的8点
                startTime = new Timestamp(DateUtil.offsetHour(DateUtil.beginOfDay(time), 8).getTime());
                endTime = new Timestamp(time.getTime());
            } else {
                DateTime currentDataDay8 = DateUtil.offsetHour(DateUtil.beginOfDay(time), 8);
                // 如果当前天的时间晚于当前天的8点,开始时间:当前天8点,结束时间:当前天的时间
                if (time.after(currentDataDay8)) {
                    // 当前天的8点
                    startTime = new Timestamp(currentDataDay8.getTime());
                    endTime = new Timestamp(time.getTime());
                } else {
                    // 如果当前天早于当前天的8点,开始时间:当前天的前一天的8点,结束时间:当前天的时间的点
                    // 当前天的前一天
                    DateTime currentDataYesterday = DateUtil.offsetDay(DateUtil.beginOfDay(time), -1);
                    // 当前天的前一天8点
                    DateTime currentDataTimeYesterday8 = DateUtil.offsetHour(currentDataYesterday, 8);
                    startTime = new Timestamp(currentDataTimeYesterday8.getTime());
                    endTime = new Timestamp(time.getTime());
                }

            }
            String beginString = TimeUtil.formatInfluxTimeString(startTime);
            String endString = TimeUtil.formatInfluxTimeString(endTime);

            mapSql.append(" select sum(v1) as currentRainfall from ").append(measurement);
            mapSql.append(" where sid ='").append(map.get(DbConstant.SENSOR_ID_FIELD_TOKEN)).append("'");
            mapSql.append(" and time>='").append(beginString).append("' ");
            mapSql.append(" and time<='").append(endString).append("' ");
            mapSql.append(" group by sid tz('Asia/Shanghai'); ");
            sql.append(mapSql);
        }
        String result = sql.toString();

        QueryResult queryResult = influxDB.query(new Query(result), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseCurrentRainResult(queryResult);
    }

    private String getAllSensorDataSql(String sensorIDOrString, String beginString,
                                       String endString, String measurement,
                                       List<String> selectField) {
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();
        selectField.forEach(item -> {
            querySql.append(item).append(",");
        });
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
//        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getHourSensorDataSql(String sensorIDOrString, String beginString,
                                        String endString, String measurement,
                                        List<String> selectField, String density, Integer monitorType) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();

        if (monitorType.equals(MonitorType.RAINFALL.getKey())) {
            selectField.forEach(item -> {
                querySql.append("sum(").append(item).append(") as ").append(item).append(",");
            });
        } else {
            selectField.forEach(item -> {
                querySql.append("first(").append(item).append(") as ").append(item).append(",");
            });
        }
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
//        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(").append(density).append(") fill(none) ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getDaySensorDataSql(String sensorIDOrString, String beginString,
                                       String endString, String measurement,
                                       List<String> selectField, String density) {
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();
        selectField.forEach(item -> {
            querySql.append("first(").append(item).append(") as ").append(item).append(",");
        });
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
//        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(").append(density).append(")  fill(none)  ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getWeekSensorDataSql(String sensorIDOrString, String beginString,
                                        String endString, String measurement,
                                        List<String> selectField) {
        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(1w)  fill(none)  ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getMonthSensorDataSql(String sensorIDOrString, String beginString,
                                         String endString, String measurement,
                                         List<String> selectField) {
        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(30d)  fill(none)  ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getYearSensorDataSql(String sensorIDOrString, String beginString,
                                        String endString, String measurement,
                                        List<String> selectField) {
        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(365d)  fill(none)  ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    @Override
    public void insertSensorData(List<Map<String, Object>> sensorDataList, boolean avg, boolean raw, List<FieldSelectInfo> fieldSelectInfoList) {
        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, avg);

        BatchPoints batchPoints = BatchPoints.database(fileConfig.getIotInfluxDatabase()).build();
        for (Map<String, Object> item : sensorDataList) {
            Point.Builder builder = Point.measurement(measurement);

            Map<String, Object> collect = fieldSelectInfoList.stream().collect(Collectors.toMap(
                    fieldSelectInfo -> fieldSelectInfo.getFieldType().getColumnPrefix() + fieldSelectInfo.getFieldTypeOrder().toString(),
                    fieldSelectInfo -> item.get(fieldSelectInfo.getFieldToken())));
            builder.fields(collect);

            builder.time(TimeUtil.dateTimeParse((String) item.get("time"), "yyyy-MM-dd HH:mm:ss.SSS").getTime(),
                    TimeUnit.MILLISECONDS);
            builder.tag("sid", item.get("sensorID").toString());
            batchPoints.point(builder.build());
        }
        influxDB.write(batchPoints);

    }

    @Override
    public void deleteSensorData(Integer sensorID, List<Date> timeList, List<FieldSelectInfo> fieldSelectInfos, boolean raw) {
        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfos);
        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, false);
        String format = "delete from " + measurement + " where sid = '" + sensorID + "' and time = '%s'";
        timeList.forEach(item -> {
            String sql = String.format(format, TimeUtil.formatInfluxTimeStringMilli(new Timestamp(item.getTime())));
            influxDB.query(new Query(sql));
        });
    }


    @Override
    public List<Map<String, Object>> querySensorNewDataByCondition(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList,
                                                                   boolean raw, Integer limitCount, Integer monitorType) {

        // 根据monitorType组建要查询传感器类型的表名,例如:流量流速:11 ,influxdb表名最终为:tb_11
        String measurement = MonitorTypeUtil.getMeasurement(monitorType, raw, false);

//        DateTime endTime = DateUtil.date();
//        DateTime startTime = DateUtil.offsetDay(endTime, -1);
        DateTime endTime = null;
        DateTime startTime = null;

        List<String> selectField = new LinkedList<>();
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        fieldSelectInfoList.forEach(item -> {
            selectField.add(item.getFieldToken());
        });
        String fieldString = String.join(",", selectField);
        StringBuilder sqlBuilder = new StringBuilder();
        sensorIDList.forEach(sid -> {
            String sidSql = " select  " + fieldString + " from  " + measurement + " where sid='" + sid.toString() + "' " +
                    (Objects.isNull(startTime) ? "" : "and time >= '" + TimeUtil.formatInfluxTimeString(new Timestamp(startTime.getTime())) + "'")
                    +
                    (Objects.isNull(endTime) ? "" : " and time <= '" + TimeUtil.formatInfluxTimeString(new Timestamp(endTime.getTime())) + "'")
                    + " order by time desc limit " +
                    (Objects.isNull(limitCount) ? "1" : limitCount)
                    + " tz('Asia/Shanghai') ; ";
            sqlBuilder.append(sidSql);
        });
        String sql = sqlBuilder.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }

    @Override
    public List<Map<String, Object>> querySensorDailyRainData(List<Integer> sensorIDList, Timestamp begin, Timestamp end) {

        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("select sum(v1) as dailyRainfall from tb_5 ");
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sidOrString).append(" ) group by sid tz('Asia/Shanghai') ; ");
        String sql = sqlBuilder.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);

        List<String> selectField = new LinkedList<>();
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        selectField.add(DbConstant.DAILY_RAINFALL);

        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }


    private List<Map<String, Object>> queryResult2map(QueryResult queryResult) {
        return queryResult2map(queryResult, null);
    }

    /**
     * 需要保证每一条数据的map的顺序和字段顺序一致
     *
     * @param queryResult
     * @param fieldSelectInfoList
     * @return
     */
    private List<Map<String, Object>> queryResult2map(QueryResult queryResult, List<FieldSelectInfo> fieldSelectInfoList) {
        InfluxColComparator influxColComparator;
        if (ObjectUtil.isEmpty(fieldSelectInfoList)) {
            influxColComparator = null;
        } else {
            Map<String, Integer> map = fieldSelectInfoList.stream().collect(Collectors.toMap(fi -> fi.getFieldType().getColumnPrefix() + fi.getFieldTypeOrder().toString(), FieldSelectInfo::getFieldOrder));
            influxColComparator = new InfluxColComparator(map);
        }
        List<QueryResult.Result> results = queryResult.getResults();
        List<Map<String, Object>> maps = new ArrayList<>(results.size());

        results.forEach(result -> {
            List<QueryResult.Series> series = result.getSeries();
            if (!CollectionUtil.isNullOrEmpty(series)) {
                series.forEach(item -> {
                    List<String> columns = item.getColumns();
                    List<List<Object>> values = item.getValues();
                    values.forEach(list -> {
                        Map<String, Object> temp = ObjectUtil.isNull(influxColComparator) ? new HashMap<>() : new TreeMap<>(influxColComparator);
                        for (int i = 0; i < columns.size(); i++) {
                            if (columns.get(i).equals(DbConstant.TIME_FIELD)) {
                                temp.put(columns.get(i), parseInfluxTime(list.get(i)));
                            } else {
                                temp.put(columns.get(i), list.get(i));
                            }

                        }
                        maps.add(temp);
                    });

                });
            }

        });
        return maps;
    }

    private String parseInfluxTime(Object time) {
        if (time instanceof Double) {
            Double timeDouble = (Double) time;
            long milli = timeDouble.longValue();
            Timestamp timestamp = new Timestamp(milli);
            return TimeUtil.getMilliDefaultFormatter().format(timestamp);
        } else {
            throw new RuntimeException("unknown time value");
        }
    }

    private static class InfluxColComparator implements Comparator<String> {
        Map<String, Integer> map;

        public InfluxColComparator(Map<String, Integer> map) {
            this.map = map;
        }

        @Override
        public int compare(String o1, String o2) {
            if (map.containsKey(o1) && map.containsKey(o2)) {
                return map.get(o1) - map.get(o2);
            } else if (!map.containsKey(o1) && map.containsKey(o2)) {
                return -1;
            } else if (map.containsKey(o1) && !map.containsKey(o2)) {
                return 1;
            } else {
                return o1.compareTo(o2);
            }
        }
    }
}
