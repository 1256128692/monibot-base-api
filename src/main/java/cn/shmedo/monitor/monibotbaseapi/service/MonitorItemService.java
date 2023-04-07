package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.QueryWtMonitorItemListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtMonitorItemInfo;

public interface MonitorItemService {
    WtMonitorItemInfo queryWtMonitorItemList(QueryWtMonitorItemListParam request);
}
