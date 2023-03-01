package cn.shmedo.monitor.monibotbaseapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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
    @Value("${service.infoServiceAddress}")
    private String infoServiceAddress;

    @Value("${service.mdInfoServiceAddress}")
    private String mdInfoServiceAddress;


    public String getInfoServiceAddress() {
        return infoServiceAddress;
    }

    public void setInfoServiceAddress(String infoServiceAddress) {
        this.infoServiceAddress = infoServiceAddress;
    }

    public String getAuthServiceAddress() {
        return authServiceAddress;
    }

    public void setAuthServiceAddress(String authServiceAddress) {
        this.authServiceAddress = authServiceAddress;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public static FileConfig getInstance() {
        return ContextHolder.getBean(FileConfig.class);
    }

    public String getIotServiceAddress() {
        return iotServiceAddress;
    }

    public void setIotServiceAddress(String iotServiceAddress) {
        this.iotServiceAddress = iotServiceAddress;
    }

    public String getAuthAppKey() {
        return authAppKey;
    }

    public void setAuthAppKey(String authAppKey) {
        this.authAppKey = authAppKey;
    }

    public String getAuthAppSecret() {
        return authAppSecret;
    }

    public void setAuthAppSecret(String authAppSecret) {
        this.authAppSecret = authAppSecret;
    }

    public String getMdInfoServiceAddress() {
        return mdInfoServiceAddress;
    }

    public void setMdInfoServiceAddress(String mdInfoServiceAddress) {
        this.mdInfoServiceAddress = mdInfoServiceAddress;
    }
}
