package cn.shmedo.monitor.monibotbaseapi.model.param.video;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VideoDeviceInfoV2 {


    @NotBlank
    private String deviceSerial;
    @NotBlank
    @Size(max = 30)
    private String deviceName;

    @NotNull
    private Byte accessPlatform;


}
