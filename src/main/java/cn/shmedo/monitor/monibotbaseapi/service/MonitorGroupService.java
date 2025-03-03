package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.QueryManualSensorListByMonitorParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.Group4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorGroupParentBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.MonitorGroupPointBaseInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup.SimpleMonitorInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 16:53
 **/
public interface MonitorGroupService {
    void addMonitorGroup(AddMonitorGroupParam pa, Integer userID);

    void deleteMonitorGroup(List<Integer> groupIDList);

    void updateMonitorGroup(UpdateMonitorGroupParam pa, Integer userID);

    String uploadMonitorGroupImage(UploadMonitorGroupImageParam pa, Integer userID);

    void configMonitorPointImageLocation(ConfigMonitorPointImageLocationParam pa);

    PageUtil.Page<Group4Web> queryMonitorGroupPage(QueryMonitorGroupPageParam pa);

    Group4Web queryMonitorGroupDetail(TbMonitorGroup tbMonitorGroup);

    List<Group4Web> queryMonitorGroupList(QueryMonitorGroupListParam pa);

    List<SimpleMonitorInfo> queryMonitorGroupItemNameList(QueryMonitorGroupItemNameListParam pa);

    List<MonitorGroupParentBaseInfo> queryProjectGroupInfoList(QueryProjectGroupInfoParam param);

    List<MonitorGroupPointBaseInfo> queryMonitorTypeGroupPoint(QueryManualSensorListByMonitorParam pa);
}
