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

    @Value("${service.influxAddr}")
    private String influxAddr;

    @Value("${service.influxDatabase}")
    private String influxDatabase;

    @Value("${service.influxUsername}")
    private String influxUsername;

    @Value("${service.influxPassword}")
    private String influxPassword;
    @Value("${service.iotInfluxAddr}")
    private String iotInfluxAddr;
    @Value("${service.iotInfluxDatabase}")
    private String iotInfluxDatabase;
    @Value("${service.iotInfluxUsername}")
    private String iotInfluxUsername;
    @Value("${service.iotInfluxPassword}")
    private String iotInfluxPassword;


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

    public String getInfluxAddr() {
        return influxAddr;
    }

    public void setInfluxAddr(String influxAddr) {
        this.influxAddr = influxAddr;
    }

    public String getInfluxDatabase() {
        return influxDatabase;
    }

    public void setInfluxDatabase(String influxDatabase) {
        this.influxDatabase = influxDatabase;
    }

    public String getInfluxUsername() {
        return influxUsername;
    }

    public void setInfluxUsername(String influxUsername) {
        this.influxUsername = influxUsername;
    }

    public String getInfluxPassword() {
        return influxPassword;
    }

    public void setInfluxPassword(String influxPassword) {
        this.influxPassword = influxPassword;
    }

    public String getIotInfluxAddr() {
        return iotInfluxAddr;
    }

    public void setIotInfluxAddr(String iotInfluxAddr) {
        this.iotInfluxAddr = iotInfluxAddr;
    }

    public String getIotInfluxDatabase() {
        return iotInfluxDatabase;
    }

    public void setIotInfluxDatabase(String iotInfluxDatabase) {
        this.iotInfluxDatabase = iotInfluxDatabase;
    }

    public String getIotInfluxUsername() {
        return iotInfluxUsername;
    }

    public void setIotInfluxUsername(String iotInfluxUsername) {
        this.iotInfluxUsername = iotInfluxUsername;
    }

    public String getIotInfluxPassword() {
        return iotInfluxPassword;
    }

    public void setIotInfluxPassword(String iotInfluxPassword) {
        this.iotInfluxPassword = iotInfluxPassword;
    }
}
