package cn.shmedo.monitor.monibotbaseapi.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class FileConfig {
    @Value("${common.apiVersion}")
    private String apiVersion;
    @Value("${service.iotServiceAddress}")
    private String iotServiceAddress;
    @Value("${service.authAppKey}")
    private String authAppKey;
    @Value("${service.authAppSecret}")
    private String authAppSecret;
    @Value("${service.authServiceAddress}")
    private String authServiceAddress;
    @Value("${service.wtReportServiceAddress}")
    private String wtReportServiceAddress;
    @Value("${service.mdInfoServiceAddress}")
    private String mdInfoServiceAddress;
    @Value("${service.workFlowServiceAddress}")
    private String workFlowServiceAddress;
    @Value("${service.influxAddr}")
    private String influxAddr;
    @Value("${service.influxDatabase}")
    private String influxDatabase;
    @Value("${service.influxUsername}")
    private String influxUsername;
    @Value("${service.influxPassword}")
    private String influxPassword;
    @Value("${service.ysUrl}")
    private String ysUrl;
    @Value("${service.ysAppKey}")
    private String ysAppKey;
    @Value("${service.ysSecret}")
    private String ysSecret;
    @Value("${service.ysMaxCount}")
    private Integer ysMaxCount;
    @Value("${service.ysFlowCheckSeconds}")
    private Integer ysFlowCheckSeconds;

    @Value("${ArtemisConfig.host}")
    private String hkHost;

    @Value("${ArtemisConfig.appKey}")
    private String hkAppKey;

    @Value("${ArtemisConfig.appSecret}")
    private String hkAppSecret;

    @Value("${ArtemisConfig.streamProtocol}")
    private String streamProtocol;

    @Value("${iot.hkProductID}")
    private Integer hkProductID;


    @Value("${iot.ysProductID}")
    private Integer ysProductID;

    @Value("${service.notifyServiceAddress}")
    private String notifyServiceAddress;

    @Value("${service.notifyAppKey}")
    private String notifyAppKey;

    @Value("${service.notifyAppSecret}")
    private String notifyAppSecret;

    @Value("${service.warn.data.templateCode}")
    private String dataWarnTemplateCode;

    @Value("${service.warn.device.templateCode}")
    private String deviceWarnTemplateCode;

}
