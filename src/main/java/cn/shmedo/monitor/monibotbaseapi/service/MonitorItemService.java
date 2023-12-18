package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.workorder.QueryWorkOrderStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtMonitorItemInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

public interface MonitorItemService {
    WtMonitorItemInfo queryWtMonitorItemList(QueryWtMonitorItemListParam request);

    void addMonitorItem(AddMonitorItemParam pa, Integer userID);

    void deleteMonitorItem(List<Integer> monitorItemIDList);

    void updateMonitorItem(UpdateMonitorItemParam pa, Integer userID);

    void addCompanyMonitorItem(AddCompanyMonitorItemParam pa, Integer userID);

    PageUtil.Page<MonitorItem4Web> queryMonitorItemPageList(QueryMonitorItemPageListParam pa);

    List<MonitorItemV1> queryMonitorItemList(QueryMonitorItemListParam pa);

    List<MonitorItemWithDefaultChecked> querySuperMonitorItemList(QuerySuperMonitorItemListParam pa);

    List<CompanyMonitorItemNameInfo> queryMonitorItemNameList(QueryWorkOrderStatisticsParam pa);

    List<MonitorItemSimple> listMonitorItemSimple(QueryMonitorItemSimpleListParam param);
}
