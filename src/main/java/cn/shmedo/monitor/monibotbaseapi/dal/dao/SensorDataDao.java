package cn.shmedo.monitor.monibotbaseapi.dal.dao;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorStatisticsType;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SensorDataDao {
    /**
     * 查询传感器最新数据
     *
     * @param sensorIDList        传感器编号列表
     * @param fieldSelectInfoList 字段信息列表
     * @param raw                 是否为原始数据
     * @return 监测数据列表
     */
    List<Map<String, Object>> querySensorNewData(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList
            , boolean raw, Integer monitorType);

    /**
     * 查询传感器在某个时间点之前的最新数据
     *
     * @param sensorIDList        传感器编号列表
     * @param end                 时间点
     * @param fieldSelectInfoList 字段信息列表
     * @param raw                 是否为原始数据
     * @param monitorType         监测类型
     * @return 监测数据列表
     */
    List<Map<String, Object>> querySensorNewDataBefore(List<Integer> sensorIDList, Timestamp end,
                                                       List<FieldSelectInfo> fieldSelectInfoList, boolean raw, Integer monitorType);

    /**
     * 查询传感器监测数据列表
     *
     * @param sensorIDList        传感器编号列表
     * @param begin               开始时间
     * @param end                 结束时间
     * @param density             查询密度
     * @param fieldSelectInfoList 字段信息列表
     * @param raw                 是否为原始数据
     * @param queryType
     * @return 监测数据列表
     */
    List<Map<String, Object>> querySensorData(List<Integer> sensorIDList, Timestamp begin, Timestamp end,
                                              String density, List<FieldSelectInfo> fieldSelectInfoList,
                                              boolean raw, Integer monitorType, Integer queryType);

    /**
     * 从实时表中查询传感器(雨量)的当前统计数据。查询返回数据含有：sensorID,time,currentRainfall
     *
     * @param dataMaps        传感器数据列表
     * @param begin               开始时间(>=)
     * @param end                 结束时间(<)
     * @param fieldSelectInfoList 字段列表
     * @return 传感器天的统计数据
     */
    List<Map<String, Object>> querySensorRainStatisticsData(List<Map<String, Object>> dataMaps, Timestamp begin, Timestamp end,
                                                           List<FieldSelectInfo> fieldSelectInfoList, Integer monitorType);



    /**
     * 从实时表中查询传感器天的统计数据。查询返回数据含有：sensorID,time，以及物模型字段
     *
     * @param sensorIDList        传感器编号列表
     * @param begin               开始时间(>=)
     * @param end                 结束时间(<)
     * @param fieldSelectInfoList 字段列表
     * @param raw                 是否原始数据
     * @param monitorType
     * @return 传感器天的统计数据
     */
    List<Map<String, Object>> querySensorDayStatisticsData(List<Integer> sensorIDList, Timestamp begin, Timestamp end,
                                                           List<FieldSelectInfo> fieldSelectInfoList,
                                                           boolean raw, Integer monitorType, SensorStatisticsType sensorStatisticsType);

    /**
     * 获取特定时间范围内的传感器最新数据
     *
     * @param sensorIDList
     * @param fieldSelectInfoList
     * @param raw
     * @param begin
     * @param end
     * @return
     */
    List<Map<String, Object>> querySensorNewDataBetween(List<Integer> sensorIDList,
                                                        List<FieldSelectInfo> fieldSelectInfoList,
                                                        boolean raw, Timestamp begin, Timestamp end);

    /**
     * 插入传感器数据
     *
     * @param sensorDataList      传感器数据列表。包含sensorID和time字段
     * @param avg                 是否是AVG数据
     * @param raw                 是否时原始数据
     * @param fieldSelectInfoList 字段列表
     * @param monitorType
     */
    void insertSensorData(List<Map<String, Object>> sensorDataList, boolean avg, boolean raw,
                          List<FieldSelectInfo> fieldSelectInfoList, Integer monitorType, String tableSuffix);



    /**
     * 删除传感器在时间点上的数据
     *
     * @param sensorID
     * @param timeList         时间点列表
     * @param fieldSelectInfos 字段列表
     * @param b                是否时原始数据
     */
    void deleteSensorData(Integer sensorID, List<Date> timeList, List<FieldSelectInfo> fieldSelectInfos, boolean b);

    List<Map<String, Object>> querySensorNewDataByCondition(List<Integer> sensorIDList, List<FieldSelectInfo> fieldSelectInfoList,
                                                            boolean raw, Integer limitCount, Integer monitorType);

    List<Map<String, Object>> querySensorDailyRainData(List<Integer> sensorIDList, Timestamp begin, Timestamp endTime);

    /**
     * 查询历史时间段的历史平均数据
     *
     * @param sensorIDList 传感器ID列表
     * @param monitorTypeFields 字段列表
     * @param begin 开始时间
     * @param end 结束时间
     * @param density 密度
     * @return
     */
    List<Map<String, Object>> querySensorHistoryAvgData(List<Integer> sensorIDList, List<TbMonitorTypeField> monitorTypeFields,
                                                        Timestamp begin, Timestamp end, Integer density, Integer monitorType);



    /**
     * 查询历史时间段的历史累加数据(雨量专属)
     *
     * @param sensorIDList 传感器ID列表
     * @param monitorTypeFields 字段列表
     * @param begin 开始时间
     * @param end 结束时间
     * @param density 密度
     * @return
     */
    List<Map<String, Object>> queryRainSensorHistorySumData(List<Integer> sensorIDList, List<TbMonitorTypeField> monitorTypeFields,
                                                            Timestamp begin, Timestamp end, Integer density, Integer monitorType);


    /**
     * 查询有无数据的日期返回
     * @param sensorIDList
     * @param begin
     * @param end
     * @param monitorType
     * @return
     */
    List<Map<String, Object>> querySensorDayData(List<Integer> sensorIDList, Timestamp begin, Timestamp end, Integer monitorType);

    /**
     * 查询通用传感器数据列表
     *
     * @param sensorIDList
     * @param begin
     * @param end
     * @param densityType    查询密度,例如(1h,1d,1w)
     * @param statisticsType 统计方式,例如(avg,last)
     * @param fieldList
     * @param monitorType
     * @return
     */
    List<Map<String, Object>> queryCommonSensorDataList(List<Integer> sensorIDList, Date begin, Date end, Integer densityType, Integer statisticsType, List<FieldBaseInfo> fieldList, Integer monitorType);

    /**
     * 插入通用数据
     * @param sensorDataList
     * @param fieldInfoList
     * @param monitorType
     * @param tableSuffix
     */
    void insertSensorCommonData(List<Map<String, Object>> sensorDataList, List<FieldBaseInfo> fieldInfoList, Integer monitorType, String tableSuffix);
}
