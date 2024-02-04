package cn.shmedo.monitor.monibotbaseapi.service;


import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDeviceWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryDeviceWarnPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDeviceWarnParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.DeviceWarnHistoryInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DeviceWarnPageInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbDeviceWarnLogService extends IService<TbDeviceWarnLog> {


    void saveDeviceWarnLog(SaveDeviceWarnParam param);

    PageUtil.PageWithMap<DeviceWarnPageInfo> queryDeviceWarnPage(QueryDeviceWarnPageParam param);

    DeviceWarnHistoryInfo queryDeviceWarnHistory(TbDeviceWarnLog tbDeviceWarnLog);

    DeviceWarnPageInfo queryDeviceWarnDetail(QueryDeviceWarnDetailParam param);
}
