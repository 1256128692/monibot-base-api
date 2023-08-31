package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 14:23
 */
@Data
public class VideoCompanyViewBaseInfo {
    private Integer deviceVideoID;
    private Boolean deviceStatus;
    private String deviceSerial;
    private String deviceName;
    private String deviceType;
    private Integer deviceChannelNum;
    private Integer accessChannelNum;
    private Integer accessPlatform;
    private Integer accessProtocol;
    private Integer companyID;
    private Integer projectID;
    private Integer storageType;
    private Boolean captureStatus;
    private Boolean allocationStatus;
    private List<Integer> deviceChannel;

    public static VideoCompanyViewBaseInfo build(final TbVideoDevice tbVideoDevice, final List<Integer> deviceChannel) {
        VideoCompanyViewBaseInfo res = new VideoCompanyViewBaseInfo();
        BeanUtil.copyProperties(tbVideoDevice, res);
        res.setDeviceVideoID(tbVideoDevice.getID());
        res.setDeviceChannel(deviceChannel);
        return res;
    }
}
