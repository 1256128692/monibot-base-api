package cn.shmedo.monitor.monibotbaseapi.service;


import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDeviceWarnParam;

public interface ITbDeviceWarnLogService {


    void saveDeviceWarnLog(SaveDeviceWarnParam param);

}
