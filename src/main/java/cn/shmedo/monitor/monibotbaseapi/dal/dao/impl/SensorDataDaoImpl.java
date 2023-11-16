package cn.shmedo.monitor.monibotbaseapi.dal.dao.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.util.FieldUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.dao.SensorDataDao;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
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
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class SensorDataDaoImpl implements SensorDataDao {

    /**
     * 查询密度:(全部,小时)下的限制数量
     */
    private static final int LimitCount = 10000;
    private InfluxDB influxDB;
    private FileConfig fileConfig;

    public SensorDataDaoImpl(@Autowired InfluxDB influxDB, FileConfig fileConfig) {
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
                                                                  boolean raw, Integer monitorType) {
        String measurement = MonitorTypeUtil.getMeasurement(monitorType, raw, false);
        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));
        List<String> selectField = new LinkedList<>();
        List<String> resultField = new LinkedList<>();
        fieldSelectInfoList.forEach(item -> {
            selectField.add(String.format(" mean(%s) as %s", item.getFieldToken(), item.getFieldToken()));
            resultField.add(item.getFieldToken());
        });

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
        return InfluxSensorDataUtil.parseResult(queryResult, resultField);
    }

    @Override
    public List<Map<String, Object>> querySensorData(List<Integer> sensorIDList, Timestamp begin, Timestamp end,
                                                     String density, List<FieldSelectInfo> fieldSelectInfoList,
                                                     boolean raw, Integer monitorType, Integer queryType) {
        Boolean flag = true;
        String endStr = "";
        if (StringUtils.isNotBlank(density) && density.substring(density.length() - 1, density.length()).equals(DbConstant.DENSITY_DAY)) {
            flag = true;
        } else if (StringUtils.isNotBlank(density)) {
            // 截取最后一个字母,以此来判断查询密度,h:代表小时,d:代表天
            endStr = density.substring(density.length() - 1, density.length());
            flag = false;
        } else {
            flag = false;
        }
        // 时间根据密度往后推
        Timestamp beginTime = TimeUtil.calculateNewBeginTime(begin.toLocalDateTime(), density);

        String measurement = MonitorTypeUtil.getMeasurement(monitorType, raw, flag);
        String beginString = TimeUtil.formatInfluxTimeString(beginTime);
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
                sql = getHourSensorDataSql(sidOrString, beginString, endString, measurement, selectField, density, monitorType, queryType);
                break;
            case DbConstant.DENSITY_MINUTE:
                sql = getMinuteSensorDataSql(sidOrString, beginString, endString, measurement, selectField, density, monitorType, queryType);
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

            mapSql.append(" select sum(rainfall) as currentRainfall from ").append(measurement);
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
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" limit ");
        sqlBuilder.append(LimitCount);
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }

    private String getMinuteSensorDataSql(String sensorIDOrString, String beginString,
                                          String endString, String measurement,
                                          List<String> selectField, String density, Integer monitorType, Integer queryType) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();

        if (queryType != null) {
            selectField.forEach(item -> {
                querySql.append(QueryType.getValueByKey(queryType)).append("(").append(item).append(") as ").append(item).append(",");
            });
        } else {
            // 旧逻辑保持不动
            if (monitorType.equals(MonitorType.WT_RAINFALL.getKey())) {
                selectField.forEach(item -> {
                    querySql.append("sum(").append(item).append(") as ").append(item).append(",");
                });
            } else {
                selectField.forEach(item -> {
                    querySql.append("mean(").append(item).append(") as ").append(item).append(",");
                });
            }
        }
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(").append(density).append(") fill(none) ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" limit ");
        sqlBuilder.append(LimitCount);
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();

    }

    private String getHourSensorDataSql(String sensorIDOrString, String beginString,
                                        String endString, String measurement,
                                        List<String> selectField, String density, Integer monitorType, Integer queryType) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();

        if (queryType != null) {
            selectField.forEach(item -> {
                querySql.append(QueryType.getValueByKey(queryType)).append("(").append(item).append(") as ").append(item).append(",");
            });
        } else {
            // 旧逻辑保持不动
            if (monitorType.equals(MonitorType.WT_RAINFALL.getKey())) {
                selectField.forEach(item -> {
                    querySql.append("sum(").append(item).append(") as ").append(item).append(",");
                });
            } else {
                selectField.forEach(item -> {
                    querySql.append("mean(").append(item).append(") as ").append(item).append(",");
                });
            }
        }

        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(").append(density).append(") fill(none) ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" limit ");
        sqlBuilder.append(LimitCount);
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
            querySql.append("mean(").append(item).append(") as ").append(item).append(",");
        });
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
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


    @Override
    public void insertSensorData(List<Map<String, Object>> sensorDataList, boolean avg, boolean raw, List<FieldSelectInfo> fieldSelectInfoList, Integer monitorType) {
        List<Tuple<FieldType, Integer>> fieldTypeCount = FieldUtil.getFieldTypeCount(fieldSelectInfoList);
        String measurement = MonitorTypeUtil.getMeasurement(monitorType, false, avg);

        BatchPoints batchPoints = BatchPoints.database(fileConfig.getInfluxDatabase()).build();
        for (Map<String, Object> item : sensorDataList) {


            Map<String, Object> collect = fieldSelectInfoList.stream()
                    .filter(e -> item.containsKey(e.getFieldToken()) && ObjectUtil.isNotEmpty(item.get(e.getFieldToken())))
                    .collect(Collectors.toMap(
                            FieldSelectInfo::getFieldToken,
                            fieldSelectInfo -> item.get(fieldSelectInfo.getFieldToken())));
            if (collect.size() > 0) {
                Point.Builder builder = Point.measurement(measurement);
                builder.fields(collect);
                builder.time(TimeUtil.dateTimeParse((String) item.get("time"), "yyyy-MM-dd HH:mm:ss.SSS").getTime(),
                        TimeUnit.MILLISECONDS);
                builder.tag("sid", item.get("sensorID").toString());
                batchPoints.point(builder.build());
            }
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

        // 根据monitorType组建要查询传感器类型的表名,例如:流量流速:11 ,influxdb表名最终为:tb_11_data
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

    /**
     * @param sensorIDList
     * @param begin
     * @param end
     * @return
     */
    @Override
    public List<Map<String, Object>> querySensorDailyRainData(List<Integer> sensorIDList, Timestamp begin, Timestamp end) {

        List<Map<String, Timestamp>> dailyTimestampList = TimeUtil.getDailyTimestampList(begin, end);
        List<String> selectField = new LinkedList<>();
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        selectField.add(DbConstant.DAILY_RAINFALL);

        if (CollectionUtil.isNullOrEmpty(dailyTimestampList)) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        for (Map<String, Timestamp> map : dailyTimestampList) {
            Timestamp beginTimestamp = map.get("begin");
            Timestamp endTimestamp = map.get("end");

            String beginString = TimeUtil.formatInfluxTimeString(beginTimestamp);
            String endString = TimeUtil.formatInfluxTimeString(endTimestamp);
            String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                    .collect(Collectors.joining(" or "));

            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select dailyRainfall from tb_31_data ");
            sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
            sqlBuilder.append(" and ( ");
            sqlBuilder.append(sidOrString).append(" ) group by sid tz('Asia/Shanghai') ; ");
            result.append(sqlBuilder);

        }
        String sql = result.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }

    @Override
    public List<Map<String, Object>> querySensorHistoryAvgData(List<Integer> sensorIDList, List<TbMonitorTypeField> monitorTypeFields,
                                                               Timestamp begin, Timestamp end, Integer density, Integer monitorType) {

        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));

        List<String> selectField = new LinkedList<>();
        monitorTypeFields.forEach(item -> {
            selectField.add(item.getFieldToken());
        });

        String sql = null;
        if (density.equals(AvgDensityType.ALL.getValue())) {
            // 查询全部数据
            String measurement = MonitorTypeUtil.getMeasurement(monitorType, false, false);
            sql = getAllSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
        } else {
            // 查询每日的平均数据
            String measurement = MonitorTypeUtil.getMeasurement(monitorType, false, true);
            sql = getAvgSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
        }

        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);

    }

    @Override
    public List<Map<String, Object>> queryRainSensorHistorySumData(List<Integer> sensorIDList, List<TbMonitorTypeField> monitorTypeFields, Timestamp begin, Timestamp end, Integer density, Integer monitorType) {
        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));

        List<String> selectField = new LinkedList<>();
        monitorTypeFields.forEach(item -> {
            selectField.add(item.getFieldToken());
        });
        String measurement = MonitorTypeUtil.getMeasurement(monitorType, false, false);
        String sql = null;
        if (density.equals(AvgDensityType.ALL.getValue())) {
            // 查询全部数据
            sql = getAllSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
        } else if (density.equals(RainDensityType.DAILY.getValue()) || density.equals(RainDensityType.MONTHLY.getValue())
                || density.equals(RainDensityType.YEARLY.getValue())) {
            // 查询每日的日平均数据
            selectField.clear();
            selectField.add("dailyRainfall");
            sql = getAvgSensorDataSql(sidOrString, beginString, endString, measurement, selectField);
        } else if (density.equals(RainDensityType.ONE_HOURS.getValue()) || density.equals(RainDensityType.THREE_HOURS.getValue())
                || density.equals(RainDensityType.SIX_HOURS.getValue()) || density.equals(RainDensityType.TWELVE_HOURS.getValue())) {
            // 查询每小时的累加数据
            sql = getRainSensorSumDataSql(sidOrString, beginString, endString, measurement, selectField, density);
        }

        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }

    @Override
    public List<Map<String, Object>> querySensorDayData(List<Integer> sensorIDList, Timestamp begin, Timestamp end, Integer monitorType) {

        String beginString = TimeUtil.formatInfluxTimeString(begin);
        String endString = TimeUtil.formatInfluxTimeString(end);
        String measurement = MonitorTypeUtil.getMeasurement(monitorType, false, true);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" select * from ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sidOrString).append(" ) ");
        sqlBuilder.append(" order by time asc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        String sql = sqlBuilder.toString();
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        List<String> selectField = new LinkedList<>();
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }

    @Override
    public List<Map<String, Object>> queryCommonSensorDataList(List<Integer> sensorIDList,
                                                               Date begin, Date end, Integer densityType,
                                                               Integer statisticsType, List<FieldBaseInfo> fieldList,
                                                               Integer monitorType) {

        String beginString = TimeUtil.formatInfluxTimeString(new Timestamp(begin.getTime()));
        String endString = TimeUtil.formatInfluxTimeString(new Timestamp(end.getTime()));

        String measurement = MonitorTypeUtil.getMeasurementByStatisticsTypeAndDensityType(monitorType, statisticsType, densityType);
        List<String> selectField = new LinkedList<>();
        fieldList.forEach(item -> {
            selectField.add(item.getFieldToken());
        });

        String sql = null;
        switch (densityType) {
            case 1:
                sql = getAllSensorCommonDataSql(sensorIDList, beginString, endString, measurement, selectField);
                break;
            case 2:
            case 7:
            case 8:
            case 9:
            case 10:
                sql = getHourSensorCommonDataSql(sensorIDList, beginString, endString, measurement, selectField, statisticsType, densityType);
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                sql = getDaySensorCommonDataSql(sensorIDList, beginString, endString, measurement, selectField, statisticsType, densityType);
                break;
        }
        QueryResult queryResult = influxDB.query(new Query(sql), TimeUnit.MILLISECONDS);
        return InfluxSensorDataUtil.parseResult(queryResult, selectField);
    }


    /**
     * 查询全部传感器的最新值
     */
    private String getAllSensorCommonDataSql(List<Integer> sensorIDList, String beginString, String endString, String measurement, List<String> selectField) {
        selectField.add(DbConstant.TIME_FIELD);
        selectField.add(DbConstant.SENSOR_ID_TAG);
        String fieldString = String.join(",", selectField);
        StringBuilder sqlBuilder = new StringBuilder();
        sensorIDList.forEach(sid -> {
            String sidSql = " select  " + fieldString + " from  " + measurement + " where sid='" + sid.toString() + "' " +
                    "and time >= '" + beginString + "' and time <= '" + endString + "' order by time desc limit 1 tz('Asia/Shanghai') ; ";
            sqlBuilder.append(sidSql);
        });
        return sqlBuilder.toString();
    }

    /**
     * 查询全部传感器的根据小时密度的统计方式
     * 查询的是tb_data表,而不是后缀_avg,_diff等表
     */
    private String getHourSensorCommonDataSql(List<Integer> sensorIDList, String beginString,
                                              String endString, String measurement, List<String> selectField,
                                              Integer statisticsType, Integer densityType) {

        StringBuilder selectFieldBuilder = new StringBuilder();
        selectField.forEach(s -> {
            if (statisticsType == StatisticalMethods.CHANGE.getValue()) {
                selectFieldBuilder.append("last(").append(s).append(") - first(").append(s);
                selectFieldBuilder.append(") as ").append(s).append(",");
            } else {
                selectFieldBuilder.append(StatisticalMethods.fromValue(statisticsType).getName());
                selectFieldBuilder.append("(").append(s).append(") as ").append(s).append(",");
            }

        });
        selectFieldBuilder.append(DbConstant.TIME_FIELD);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));

        String sidSql = " select " + selectFieldBuilder.toString() + " from  " + measurement + " where ("
                + sidOrString + ") and time >= '" + beginString + "' and time <= '" + endString
                + "' GROUP BY sid, time("+DisplayDensity.fromValue(densityType).getName()+") fill(none) "
                + " order by time desc limit 50000 tz('Asia/Shanghai') ; ";
        return sidSql;
    }


    /**
     * 查询全部传感器的根据小时密度的统计方式
     * 查询的是tb_data_avg,或者_diff等等,带后缀的特殊表
     */
    private String getDaySensorCommonDataSql(List<Integer> sensorIDList, String beginString, String endString,
                                             String measurement, List<String> selectField, Integer statisticsType,
                                             Integer densityType) {
        StringBuilder selectFieldBuilder = new StringBuilder();
        selectField.forEach(s -> {
            selectFieldBuilder.append(StatisticalMethods.fromValue(statisticsType).getName());
            selectFieldBuilder.append("(").append(s).append(") as ").append(s).append(",");
        });
        selectFieldBuilder.append(DbConstant.TIME_FIELD);
//        selectFieldBuilder.append(DbConstant.SENSOR_ID_TAG);
        String sidOrString = sensorIDList.stream().map(sid -> DbConstant.SENSOR_ID_TAG + "='" + sid.toString() + "'")
                .collect(Collectors.joining(" or "));

        String sidSql = " select " + selectFieldBuilder.toString() + " from  " + measurement + " where ("
                + sidOrString + ") and time >= '" + beginString + "' and time <= '" + endString
                + "' GROUP BY sid, time("+DisplayDensity.fromValue(densityType).getName()+") fill(none) "
                + " order by time desc limit 50000 tz('Asia/Shanghai') ; ";
        return sidSql;
    }


    private String getAvgSensorDataSql(String sensorIDOrString, String beginString,
                                       String endString, String measurement,
                                       List<String> selectField) {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();
        selectField.forEach(item -> {
            querySql.append("mean(").append(item).append(") as ").append(item).append(",");
        });
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(1d)  fill(none)  ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }


    private String getRainSensorSumDataSql(String sensorIDOrString, String beginString,
                                           String endString, String measurement,
                                           List<String> selectField, Integer density) {
        String time = "";
        if (density.equals(RainDensityType.ONE_HOURS.getValue())) {
            time = RainDensityType.ONE_HOURS.getStr();
        } else if (density.equals(RainDensityType.THREE_HOURS.getValue())) {
            time = RainDensityType.THREE_HOURS.getStr();
        } else if (density.equals(RainDensityType.SIX_HOURS.getValue())) {
            time = RainDensityType.SIX_HOURS.getStr();
        } else if (density.equals(RainDensityType.TWELVE_HOURS.getValue())) {
            time = RainDensityType.TWELVE_HOURS.getStr();
        }

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder querySql = new StringBuilder();
        selectField.forEach(item -> {
            querySql.append("sum(").append(item).append(") as ").append(item).append(",");
        });
        String selectFieldString = querySql.toString().substring(0, querySql.toString().length() - 1);
        sqlBuilder.append(" select ");
        sqlBuilder.append(selectFieldString);
        sqlBuilder.append(" from  ").append(measurement);
        sqlBuilder.append(" where time>='" + beginString + "' and time<='" + endString + "' ");
        sqlBuilder.append(" and ( ");
        sqlBuilder.append(sensorIDOrString).append(" ) ");
        sqlBuilder.append(" group by ").append(DbConstant.SENSOR_ID_TAG).append(",time(");
        sqlBuilder.append(time);
        sqlBuilder.append(")  fill(none)  ");
        sqlBuilder.append(" order by time desc ");
        sqlBuilder.append(" tz('Asia/Shanghai') ");
        return sqlBuilder.toString();
    }


}
