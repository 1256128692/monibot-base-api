package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkChannelInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys.YsChannelInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class VideoCaptureBaseInfo {

    // 可以为空
    private Integer sensorID;
    private String sensorName;

    private Boolean sensorEnable;
    // 可以为空
    private Integer captureInterval;
    // 可以为空
    private Integer projectID;
    private Integer channelNo;
    private String deviceSerial;

    private Boolean enable;

    private Boolean imageCapture;

    private Integer videoDeviceSourceID;
    @JsonIgnore
    private Integer videoDeviceID;

    @JsonIgnore
    private String exValues;


    // 添加一个静态方法用于转换
    public static VideoCaptureBaseInfo fromYsChannelInfo(YsChannelInfo ysChannelInfo, String deviceName) {
        VideoCaptureBaseInfo videoCaptureBaseInfo = new VideoCaptureBaseInfo();

        // 生成传感器名称 sensorName，设备 deviceName 名称加 "@" 加 index
        String sensorName = deviceName + "@" + ysChannelInfo.getChannelNo();

        // 设置属性
        videoCaptureBaseInfo.setSensorName(sensorName);
        videoCaptureBaseInfo.setChannelNo(ysChannelInfo.getChannelNo());
//        videoCaptureBaseInfo.setIpcSerial(ysChannelInfo.getIpcSerial());
        // 设置其他属性，根据需要设置

        return videoCaptureBaseInfo;
    }

    public static VideoCaptureBaseInfo fromHkChannelInfo(HkChannelInfo hkChannelInfo, String deviceName) {
        VideoCaptureBaseInfo videoCaptureBaseInfo = new VideoCaptureBaseInfo();

        // 生成传感器名称 sensorName，设备 deviceName 名称加 "@" 加 index
        String sensorName = deviceName + "@1";

        // 设置属性
        videoCaptureBaseInfo.setSensorName(sensorName);
        videoCaptureBaseInfo.setChannelNo(hkChannelInfo.getChannelNo());
        videoCaptureBaseInfo.setSensorEnable(true);

        return videoCaptureBaseInfo;

    }

    public static VideoCaptureBaseInfo fromChannelInfo(VideoCaptureBaseInfo pa, String deviceName) {
//        VideoCaptureBaseInfo videoCaptureBaseInfo = new VideoCaptureBaseInfo();

        // 生成传感器名称 sensorName，设备 deviceName 名称加 "@" 加 index
//        String sensorName = deviceName + "@" + channelNo;

        // 设置属性
        pa.setSensorName(deviceName + "@" + pa.getChannelNo());
        pa.setSensorEnable(true);
//        videoCaptureBaseInfo.setChannelNo(channelNo);
//        videoCaptureBaseInfo.setIpcSerial(ysChannelInfo.getIpcSerial());

        return pa;
    }

}
