package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.IDNameAlias;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorItemWithPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPoint4Web;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 13:47
 **/
public interface MonitorPointService {
    void addMonitorPoint(AddMonitorPointParam pa, Integer userID);

    void updateMonitorPoint(UpdateMonitorPointParam pa, Integer userID);

    void deleteMonitorPoint(List<Integer> pointIDList);

    void configMonitorPointSensors(Integer pointID, List<Integer> sensorIDList, Integer userID);

    PageUtil.Page<MonitorPoint4Web> queryMonitorPointPageList(QueryMonitorPointPageListParam pa);

    List<IDNameAlias> queryMonitorPointSimpleList(QueryMonitorPointSimpleListParam pa);

    List<MonitorItemWithPoint> queryMonitorItemPointList(QueryMonitorItemPointListParam pa);
}
