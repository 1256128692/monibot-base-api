package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointDescribeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.SensorNewDataInfo;

import java.util.List;

public interface ReservoirMonitorService {

    List<SensorNewDataInfo> queryMonitorPointList(QueryMonitorPointListParam pa);


    List<FieldSelectInfo> getFieldSelectInfoListFromModleTypeFieldList(List<TbMonitorTypeField> list);

    SensorNewDataInfo querySingleMonitorPointNewData(QueryMonitorPointDescribeParam pa);
}
