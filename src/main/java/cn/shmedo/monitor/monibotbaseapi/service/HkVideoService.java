package cn.shmedo.monitor.monibotbaseapi.service;


import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkMonitorPointInfo;

public interface HkVideoService {


    HkMonitorPointInfo queryHkVideoPage(Object param);

    HkDeviceInfo queryDevice(String deviceSerial);
}
