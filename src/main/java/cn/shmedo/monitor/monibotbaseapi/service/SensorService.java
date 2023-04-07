package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SensorService extends IService<TbSensor> {

    /**
     * 传感器分页，仅返回部分字段
     *
     * @param request {@link SensorPageRequest}
     * @return {@link PageUtil.Page<SensorPageResponse>}
     */
    PageUtil.Page<SensorPageResponse> sensorPage(SensorPageRequest request);

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
     * @return
     */
    Object addSensor(SaveSensorRequest request);

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
    void deleteSensor(DeleteSensorRequest request);

    /**
     * 更新传感器
     *
     * @param request {@link UpdateSensorRequest}
     * @return
     */
    Object updateSensor(UpdateSensorRequest request);

    /**
     * 获取试运行参数
     *
     * @param request {@link QueryTryingParamRequest}
     * @return
     */
    Object getTryingParam(QueryTryingParamRequest request);

    /**
     * 传感器试运行
     *
     * @param request {@link TryingRequest}
     * @return
     */
    Object trying(TryingRequest request);
}
