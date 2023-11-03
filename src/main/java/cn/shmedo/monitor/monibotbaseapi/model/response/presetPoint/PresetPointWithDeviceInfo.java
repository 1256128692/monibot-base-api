package cn.shmedo.monitor.monibotbaseapi.model.response.presetPoint;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-25 13:52
 */
@Data
public class PresetPointWithDeviceInfo {
    private Integer presetPointID;
    private String presetPointName;
    private Integer presetPointIndex;
    private Integer channelNo;
    private Integer videoDeviceID;
    private String deviceSerial;
    private Integer accessPlatform;
}
