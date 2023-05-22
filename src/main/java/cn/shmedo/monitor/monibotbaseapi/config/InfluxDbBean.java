package cn.shmedo.monitor.monibotbaseapi.config;


import cn.shmedo.monitor.monibotbaseapi.util.InfluxDBConnection;
import org.influxdb.InfluxDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class InfluxDbBean {
    private static final String IOT_DEVICE_STATE_RETENTION = "one_year";
    private static final String IOT_DB_RETENTION = "ten_year";

    private FileConfig fileConfig;

    @Autowired
    public InfluxDbBean(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    @Primary
    @Bean
    public InfluxDB getInfluxDB() {
        InfluxDBConnection influxDBConnection = new InfluxDBConnection(fileConfig.getInfluxUsername()
                , fileConfig.getInfluxPassword(), fileConfig.getInfluxAddr()
                , fileConfig.getInfluxDatabase(), IOT_DEVICE_STATE_RETENTION);// 保留策略模式为默认
        InfluxDB influxDB = influxDBConnection.influxDbBuild();
        influxDB.setDatabase(fileConfig.getInfluxDatabase());
        influxDB.enableBatch();
        return influxDB;
    }
}
