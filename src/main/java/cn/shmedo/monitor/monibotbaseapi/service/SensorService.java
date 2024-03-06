package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.DataSourceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.IdRecord;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface SensorService extends IService<TbSensor> {

    /**
     * 传感器分页，仅返回部分字段
     *
     * @param request {@link SensorPageRequest}
     * @return {@link PageUtil.Page< SensorListResponse >}
     */
    PageUtil.Page<SensorListResponse> sensorPage(SensorPageRequest request);

    /**
     * 数据源级联
     *
     * @param request {@link DataSourceCatalogRequest}
     * @return {@link DataSourceCatalogResponse}
     */
    List<DataSourceCatalogResponse> dataSourceCatalog(DataSourceCatalogRequest request);

    /**
     * 监测类型（下拉）目录
     *
     * @param request {@link MonitorTypeCatalogRequest}
     * @return {@link MonitorTypeCatalogResponse}
     */
    List<MonitorTypeCatalogResponse> monitorTypeCatalog(MonitorTypeCatalogRequest request);

    /**
     * 添加传感器
     *
     * @param request {@link SaveSensorRequest}
     * @return {@link IdRecord}
     */
    IdRecord addSensor(SaveSensorRequest request);

    /**
     * 传感器详情
     *
     * @param request {@link SensorPageRequest}
     * @return {@link SensorInfoResponse}
     */
    SensorInfoResponse sensorInfo(SensorInfoRequest request);

    /**
     * 删除传感器
     *
     * @param request {@link DeleteSensorRequest}
     */
    void deleteSensor(DeleteSensorRequest request, String accessToken, CurrentSubject currentSubject);

    /**
     * 更新传感器
     *
     * @param request {@link UpdateSensorRequest}
     * @return {@link IdRecord}
     */
    IdRecord updateSensor(UpdateSensorRequest request);

    /**
     * 获取试运行参数
     *
     * @param request {@link QueryTryingParamRequest}
     * @return {@link TryingParamResponse}
     */
    TryingParamResponse getTryingParam(QueryTryingParamRequest request);

    /**
     * 传感器试运行
     *
     * @param request {@link TryingRequest}
     * @return
     */
    Object trying(TryingRequest request);

    /**
     * 传感器基础配置
     *
     * @param request {@link BaseConfigRequest}
     * @return {@link BaseConfigResponse}
     */
    BaseConfigResponse baseConfig(BaseConfigRequest request);

    /**
     * 传感器列表
     *
     * @param request {@link SensorListRequest}
     * @return {@link SensorListResponse}
     */
    List<SensorListResponse> sensorList(SensorListRequest request);

    /**
     * 查询传感器和其数据源信息
     *
     * @param request {@link SourceWithSensorRequest}
     * @return {@link DataSourceWithSensor}
     */
    List<DataSourceWithSensor> querySensorDataSource(SourceWithSensorRequest request);

    /**
     * 更新传感器状态和监测开始时间
     *
     * @param request {@link UpdateSensorStatusRequest}
     */
    void updateSensorStatusAndMonitorBeginTime(UpdateSensorStatusRequest request);

    Map<Integer, List<Integer>> queryAllSensorID();

    /**
     * 查询监测类型下的全部人工传感器
     */
    List<SensorNameResponse> queryManualSensorListByMonitor(QueryManualSensorListByMonitorParam param);

    List<SensorSimple> querySimpleList(QuerySensorSimpleListRequest param);

    TryingResponse calculateField(CalculateFieldRequest request);
}
