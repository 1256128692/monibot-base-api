package cn.shmedo.monitor.monibotbaseapi.dao.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.base.QueryDensity;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.util.FieldUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dao.SensorDataDao;
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
       return querySensorNewDataByCondition(sensorIDList, fieldSelectInfoList, raw, null, null, monitorType);
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
        return InfluxSensorDataUtil.parseResult(queryResult, fieldSelectInfoList);
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
        return InfluxSensorDataUtil.parseResult(queryResult, fieldSelectInfoList);
    }

    @Override
    public List<Map<String, Object>> querySensorData(List<Integer> sensorIDList, Timestamp begin, Timestamp end,
                                                     String density, List<FieldSelectInfo> fieldSelectInfoList,
                                                     boolean raw, Integer monitorType) {
        Boolean flag = true;
        if (StringUtils.isNotBlank(density) && DbConstant.DENSITY_DAY.equals(density)){
            flag = true;
        }else {
            flag = false;
        }
        // 截取最后一个字母,以此来判断查询密度,h:代表小时,d:代表天
        String endStr = density.substring(density.length() - 1, density.length());

        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, flag);
        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));
        List<String> selectField = FieldUtil.getSelectField(fieldSelectInfoList,flag);
        String sql = null;
        switch (endStr) {
            case "":
                sql = getAllSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
                break;
            case DbConstant.DENSITY_HOUR:
                sql = getHourSensorDataSql(sidOrString, beginString, endString, measurement, selectField, density);
                break;
            case DbConstant.DENSITY_DAY:
                sql = getDaySensorDataSql(sidOrString, beginString, endString, measurement, selectField, density);
                break;
        }
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, fieldSelectInfoList);
    }

    private String getAllSensorDataSql(String sensorIDOrString, String beginString,
                                       String endString, String measurement,
                                       List<String> selectField) {
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getHourSensorDataSql(String sensorIDOrString, String beginString,
                                        String endString, String measurement,
                                        List<String> selectField,String density) {
        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(").append(density).append(") fill(none) ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getDaySensorDataSql(String sensorIDOrString, String beginString,
                                       String endString, String measurement,
                                       List<String> selectField, String density) {
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        StringBuilder sqlBuilder = new StringBuilder();
        String selectFieldString = String.join(",", selectField);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(").append(density).append(")  fill(none)  ");
        sqlBuilder.append(" order by time asc ");
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

//    @Override
//    public List<Map<String, Object>> querySensorOriginData(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList, Timestamp begin, Timestamp end, QueryDensity density, boolean raw, Boolean fieldSort) {
//        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
//        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, density.avgTable());
//        String beginString = TimeUtil.formatInfluxTimeString(begin);
//        String endString = TimeUtil.formatInfluxTimeString(end);
//        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
//                .collect(Collectors.joining(" or "));
//        List<String> selectField = FieldUtil.getSelectField(fieldSelectInfoList, density.avgField());
//        String sql = null;
//        switch (density) {
//            case ALL:
//                sql = getAllSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
//                break;
//            case HOUR:
//                sql = getHourSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
//                break;
//            case DAY:
//                sql = getDaySensorDataSql(sidOrString, beginString, endString, measurement, selectField);
//                break;
//            case WEEK:
//                sql = getWeekSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
//                break;
//            case MONTH:
//                sql = getMonthSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
//                break;
//            case YEAR:
//                sql = getYearSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
//                break;
//        }
//        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
//        if (ObjectUtil.isNotNull(fieldSort) && fieldSort) {
//            return queryResult2map(queryResult, fieldSelectInfoList);
//        } else {
//            return queryResult2map(queryResult);
//        }
//    }

    @Override
    public List<Map<String, Object>> querySensorNewDataByCondition(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList,
                                                                   boolean raw, Date maxTime, Integer limitCount, Integer monitorType) {
//        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
//        String measurement = FieldUtil.getMeasurement(fieldTypeCount, raw, false);

        // TODO:根据monitorType组建要查询传感器类型的表名,例如:流量流速:11 ,influxdb表名最终为:tb_11
        String measurement = MonitorTypeUtil.getMeasurement(monitorType, raw, false);

        List<String> selectField = FieldUtil.getSelectField(fieldSelectInfoList, false);
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        String fieldString = String.join(",", selectField);
        StringBuilder sqlBuilder = new StringBuilder();
        sensorIDList.forEach(sid -> {
            String sidSql = " select  " + fieldString + " from  " + measurement + " where sid='" + sid.toString()
                    + "' " +
                    (Objects.isNull(maxTime) ? "" : "and time <= '" + TimeUtil.formatInfluxTimeString(new Timestamp(maxTime.getTime())) + "'")
                    + " order by time desc limit " +
                    (Objects.isNull(limitCount) ? "1" : limitCount)
                    + " tz('Asia/Shanghai') ; ";
            sqlBuilder.append(sidSql);
        });
        String sql = sqlBuilder.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, fieldSelectInfoList);
    }


//    @Override
//    public List<Map<String, Object>> querySensorOriginData(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList, Timestamp begin, Timestamp end, QueryDensity density, boolean b) {
//        return querySensorOriginData(sensorIDList, fieldSelectInfoList, begin, end, density, b, null);
//    }

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
