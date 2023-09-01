package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VideoDeviceBaseInfo {


     @NotNull(message = "设备序列号不能为空")
     private String deviceSerial;
     private String validateCode;
     @NotNull(message = "接入平台不能为空")
     private Byte accessPlatform;
     @NotNull(message = "接入协议不能为空")
     private Byte accessProtocol;

}
