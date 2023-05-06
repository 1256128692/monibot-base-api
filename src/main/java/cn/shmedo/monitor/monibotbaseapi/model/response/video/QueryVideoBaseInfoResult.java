package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsDeviceInfo;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder(toBuilder = true)
public class QueryVideoBaseInfoResult {
    private String videoName;
    private String videoType;
    private Integer status;
    private String netAddress;
    private String netType;
    private String signal;
    private Timestamp updateTime;
    private Integer isEncrypt;
    private Integer riskLevel;
    private Integer offlineNotify;
    private Integer alarmSoundMode;
    private Integer defence;

    public static QueryVideoBaseInfoResult valueOf(YsDeviceInfo deviceInfo) {
        return QueryVideoBaseInfoResult.builder()
                .videoName(deviceInfo.getDeviceName())
                .videoType(deviceInfo.getDeviceSerial())
                .status(deviceInfo.getStatus())
                .netAddress(deviceInfo.getNetAddress())
                .netType(deviceInfo.getNetType())
                .signal(deviceInfo.getSignal())
                .updateTime(new Timestamp(deviceInfo.getUpdateTime()))
                .isEncrypt(deviceInfo.getIsEncrypt())
                .riskLevel(deviceInfo.getRiskLevel())
                .offlineNotify(deviceInfo.getOfflineNotify())
                .alarmSoundMode(deviceInfo.getAlarmSoundMode())
                .defence(deviceInfo.getDefence())
                .build();
    }
}
