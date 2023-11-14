package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import lombok.Data;

import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-04 13:28
 */
@Data
public class VideoDeviceBaseInfoV2 {
    private Integer videoDeviceID;
    private Boolean deviceStatus;
    private String deviceSerial;
    private String deviceName;
    private String deviceType;
    private Integer accessChannelNum;
    private Integer accessPlatform;
    private Integer accessProtocol;
    private Integer companyID;
    private Integer projectID;
    private Integer storageType;
    private Boolean captureStatus;
    private Boolean allocationStatus;
    private String baseUrl;
    private String hdUrl;
    private String ysToken;
    private Map<String, Integer> capabilitySet;

    public static VideoDeviceBaseInfoV2 build(TbVideoDevice tbVideoDevice) {
        VideoDeviceBaseInfoV2 res = new VideoDeviceBaseInfoV2();
        BeanUtil.copyProperties(tbVideoDevice, res);
        res.setVideoDeviceID(tbVideoDevice.getID());
        return res;
    }
}
