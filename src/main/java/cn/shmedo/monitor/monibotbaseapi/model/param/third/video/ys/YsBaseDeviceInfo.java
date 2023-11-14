package cn.shmedo.monitor.monibotbaseapi.model.param.third.video.ys;

import lombok.Data;

import java.util.Date;

@Data
public class YsBaseDeviceInfo {

    private String displayName;
    private String subSerial;
    private String fullSerial;
    private String model;
    private String category;
    private Integer status;
    private String defaultPicPath;
    private Integer supportWifi;
    private String releaseVersion;
    private String version;
    private Integer availableChannelCount;
    private Integer relatedDeviceCount;
    private Integer supportCloud;
    private String supportExt;
    private String routerNamePre;
    private String routerPasswordPre;
    private Date createTime;
}
