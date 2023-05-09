package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class VideoMonitorPointLiveInfo {

    private Integer companyID;
    private Integer sensorID;
    private String sensorName;

    private String configFieldValue;
    private Integer monitorPointID;

    private Integer monitorType;
    private String monitorPointName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;

    private String exValues;

    /**
     * 监测类型拓展配置值
     */
    @JsonIgnore
    private Dict configValue;

    /**
     * 标清直播地址
     */
    private String baseUrl;
    /**
     * 高清直播地址
     */
    private String hdUrl;
    /**
     * 萤石云token
     */
    private String ysToken;


    @JsonIgnore
    private String seqNo;
    @JsonIgnore
    private String ysChannelNo;
    @JsonIgnore
    private String protocol;

}
