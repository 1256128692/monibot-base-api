package cn.shmedo.monitor.monibotbaseapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileConfig {
    @Value("${common.apiVersion}")
    private String apiVersion;
    @Value("${service.authAddress}")
    private String authAddress;
    @Value("${service.iotAddress}")
    private String iotAddress;
    @Value("${service.authAppKey}")
    private String authAppKey;
    @Value("${service.authAppSecret}")
    private String authAppSecret;

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public static FileConfig getInstance() {
        return ContextHolder.getBean(FileConfig.class);
    }

    public String getAuthAddress() {
        return authAddress;
    }

    public void setAuthAddress(String authAddress) {
        this.authAddress = authAddress;
    }

    public String getIotAddress() {
        return iotAddress;
    }

    public void setIotAddress(String iotAddress) {
        this.iotAddress = iotAddress;
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
}
