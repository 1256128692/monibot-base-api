package cn.shmedo.monitor.monibotbaseapi.config;

import com.hikvision.artemis.sdk.config.ArtemisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ArtemisConfigWrapper {

    private final FileConfig fileConfig;

    @Autowired
    public ArtemisConfigWrapper(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    public ArtemisConfig getArtemisConfig() {
        return new ArtemisConfig(fileConfig.getHkHost(), fileConfig.getHkAppKey(), fileConfig.getHkAppSecret());
    }
}
