package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class VideoDeviceInfoV3 {


    @NotNull
    private Integer videoDeviceID;

    private Integer projectID;

    private Integer companyID;

    @Size(max = 100)
    private List<SensorBaseInfoV1> addSensorList;


}
