package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.AddCustomizedMonitorTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorType4web;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-20 16:53
 **/
public interface MonitorTypeService extends IService<TbMonitorType> {
    PageUtil.Page<TbMonitorType4web> queryMonitorTypePage(QueryMonitorTypePageParam request);

    void addCustomizedMonitorType(AddCustomizedMonitorTypeParam pa, Integer userID);

    MonitorTypeDetail queryMonitorTypeDetail(Integer monitorType);
}
