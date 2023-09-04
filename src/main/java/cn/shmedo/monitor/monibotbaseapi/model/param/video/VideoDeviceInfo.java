package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsChannelInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsDeviceInfo;
import lombok.Data;

import java.util.List;

@Data
public class VideoDeviceInfo extends TbVideoDevice {


    public static VideoDeviceInfo ysToNewValue(YsDeviceInfo data,
                                          List<YsChannelInfo> ysChannelInfoList,
                                          VideoDeviceBaseInfo a,
                                          AddVideoDeviceListParam pa) {
        VideoDeviceInfo vo = new VideoDeviceInfo();

        vo.setDeviceName(data.getDeviceName());
        vo.setDeviceType(data.getModel());
        vo.setDeviceSerial(data.getDeviceSerial());
        vo.setDeviceStatus(data.getStatus() == 1);
        vo.setAccessChannelNum(ysChannelInfoList.size());
        vo.setAccessPlatform(a.getAccessPlatform());
        vo.setAccessProtocol(a.getAccessProtocol());
        vo.setExValue(JSONUtil.toJsonStr(ysChannelInfoList));
        vo.setCreateTime(DateUtil.date());
        vo.setUpdateTime(DateUtil.date());
        // 第一次添加设备,都是未分配工程状态
        vo.setAllocationStatus(false);
        vo.setCreateUserID(pa.getCurrentSubject().getSubjectID());
        vo.setUpdateUserID(pa.getCurrentSubject().getSubjectID());
        vo.setCompanyID(pa.getCompanyID());

        return vo;
    }

    public static VideoDeviceInfo hkToNewValue(HkDeviceInfo hkDeviceInfo,
                                               VideoDeviceBaseInfo a,
                                               AddVideoDeviceListParam pa) {
        VideoDeviceInfo vo = new VideoDeviceInfo();

        vo.setDeviceName(hkDeviceInfo.getCameraName());
        vo.setDeviceType(hkDeviceInfo.getCameraTypeName());
        vo.setDeviceSerial(a.getDeviceSerial());
        if (hkDeviceInfo.getStatus() != null) {
            vo.setDeviceStatus(Integer.parseInt(hkDeviceInfo.getStatus()) == 1);
        } else {
            vo.setDeviceStatus(false);
        }
        // 海康平台设备的通道数默认都是1个
        vo.setAccessChannelNum(1);
        vo.setAccessPlatform(a.getAccessPlatform());
        vo.setAccessProtocol(a.getAccessProtocol());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelNo", Integer.parseInt(hkDeviceInfo.getChannelNo()));
        vo.setExValue(jsonObject.toString());
        vo.setCreateTime(DateUtil.date());
        vo.setUpdateTime(DateUtil.date());
        // 第一次添加设备,都是未分配工程状态
        vo.setAllocationStatus(false);
        vo.setCreateUserID(pa.getCurrentSubject().getSubjectID());
        vo.setUpdateUserID(pa.getCurrentSubject().getSubjectID());
        vo.setCompanyID(pa.getCompanyID());

        return vo;

    }
}
