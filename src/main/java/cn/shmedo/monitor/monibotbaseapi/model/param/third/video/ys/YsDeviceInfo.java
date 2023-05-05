package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys;

import lombok.Data;

@Data
public class YsDeviceInfo {
    private String deviceSerial;
    private String deviceName;
    private String model;
    /**
     * 在线状态： 0-不在线  1-在线
     */
    private Integer status;
    private Integer defence;
    private Integer isEncrypt;
    private Integer alarmSoundMode;
    private Integer offlineNotify;
    private String category;
    private Long updateTime;
    private String netType;
    private String signal;
    private Integer riskLevel;
    private String netAddress;
}
