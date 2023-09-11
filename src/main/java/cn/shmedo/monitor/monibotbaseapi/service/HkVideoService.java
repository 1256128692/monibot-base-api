package cn.shmedo.monitor.monibotbaseapi.service;


import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkMonitorPointInfo;

import java.util.Map;

public interface HkVideoService {


    HkMonitorPointInfo queryHkVideoPage(Integer pageNo);

    HkDeviceInfo queryDevice(String deviceSerial);

    String getStreamUrl(String deviceSerial, Integer streamType, String protocol, Integer transmode, String expand, String streamform);

    /**
     * @return {"list":[{"lockType":1,"beginTime":"2018-08-07T14:44:04.923+08:00","endTime":"2018-08-07T14:54:18.183+08:00","size":66479332}],"uuid":"e33421g1109046a79b6280bafb6fa5a7","url":"rtsp://10.2.145.66:6304/EUrl/Dib1ErK"}
     */
    Map<String, Object> getPlayBackStreamInfo(String deviceSerial, String recordLocation, String protocol,
                                              Integer transmode, String beginTime, String endTime, String uuid,
                                              String expand, String streamform, Integer lockType);

    Map<String, String> controllingPtz(String deviceSerial, Integer action, String command, Integer speed, Integer presetIndex);
}
