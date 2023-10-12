package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsChannelInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class VideoDeviceInfo extends TbVideoDevice {


    public static VideoDeviceInfo ysToNewValue(YsDeviceInfo data,
                                               List<YsChannelInfo> ysChannelInfoList,
                                               VideoDeviceBaseInfo a,
                                               AddVideoDeviceListParam pa, int num) {
        VideoDeviceInfo vo = new VideoDeviceInfo();

        vo.setDeviceName(data.getDeviceSerial());
        vo.setDeviceType(data.getModel());
        vo.setDeviceSerial(data.getDeviceSerial());
        if (data.getDeviceSerial().length() <= 30) {
            vo.setDeviceToken(data.getDeviceSerial());
        } else {
            String formatStr = DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss");
            vo.setDeviceToken("YS" + formatStr + num);
        }
        vo.setDeviceStatus(data.getStatus() == 1);
        vo.setAccessChannelNum(ysChannelInfoList.size() == 0 ? 1 : ysChannelInfoList.size());
        vo.setAccessPlatform(a.getAccessPlatform());
        vo.setAccessProtocol(a.getAccessProtocol());
        if (!CollectionUtil.isNullOrEmpty(ysChannelInfoList)) {
            vo.setExValue(JSONUtil.toJsonStr(ysChannelInfoList));
        }
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
                                               AddVideoDeviceListParam pa,
                                               String deviceToken) {
        VideoDeviceInfo vo = new VideoDeviceInfo();

        vo.setDeviceName(hkDeviceInfo.getCameraName());
        vo.setDeviceType(hkDeviceInfo.getCameraTypeName());
        vo.setDeviceSerial(a.getDeviceSerial());
        vo.setDeviceToken(deviceToken);
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
