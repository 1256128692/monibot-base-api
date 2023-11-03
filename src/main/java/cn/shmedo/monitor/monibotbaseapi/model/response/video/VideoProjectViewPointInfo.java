package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 11:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoProjectViewPointInfo extends VideoProjectViewSensorInfo {
    private Integer monitorPointID;
    private Integer monitorItemID;
    private Integer monitorType;
    private Integer displayOrder;
    private String monitorPointName;
    private List<Integer> deviceChannel;
    @JsonIgnore
    private List<VideoProjectViewSensorInfo> sensorInfoList;

    public void afterProperties() {
        Optional.ofNullable(sensorInfoList).filter(CollUtil::isNotEmpty).ifPresent(u -> {
            VideoProjectViewSensorInfo info = u.get(0);
            setSensorID(info.getSensorID());
            setSensorName(info.getSensorName());
            setSensorAlias(info.getSensorAlias());
            setVideoDeviceID(info.getVideoDeviceID());
            setDeviceStatus(info.getDeviceStatus());
            setDeviceSerial(info.getDeviceSerial());
            setDeviceName(info.getDeviceName());
            setDeviceType(info.getDeviceType());
            setAccessChannelNum(info.getAccessChannelNum());
            setAccessPlatform(info.getAccessPlatform());
            setAccessProtocol(info.getAccessProtocol());
            setCompanyID(info.getCompanyID());
            setStorageType(info.getStorageType());
            setCaptureStatus(info.getCaptureStatus());
            setAllocationStatus(info.getAllocationStatus());
            setDeviceChannel(u.stream().map(VideoProjectViewSensorInfo::getChannelNo).filter(ObjectUtil::isNotEmpty).toList());
        });
    }
}
