package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk;

import lombok.Data;

import java.util.Date;

@Data
public class HkDeviceStatusInfo {


    private String deviceType;
    private String deviceIndexCode;
    private String regionIndexCode;
    private Date collectTime;
    private String regionName;
    private String indexCode;
    private String cn;
    private String treatyType;
    private String manufacturer;
    private String ip;
    private String port;
    private Integer online;

}
