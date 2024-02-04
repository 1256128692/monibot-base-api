package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.video.BatchUpdateVideoDeviceStatusParam;

public interface IDeviceService {
    Boolean batchHandlerIotDeviceStatusChange(BatchUpdateVideoDeviceStatusParam pa);
}
