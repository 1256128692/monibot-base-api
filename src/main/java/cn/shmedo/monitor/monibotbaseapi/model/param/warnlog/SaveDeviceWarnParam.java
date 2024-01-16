package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveDeviceWarnParam {


    private Integer companyID;
    private Integer platform;
    private String deviceSerial;
    private Date time;
    private String deviceType;
    private String deviceToken;
    private String projectName;

    private String deviceSource;

}
