package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 传感器数据源枚举
 *
 * @author Chengfs on 2023/4/4
 */
@Getter
@AllArgsConstructor
public enum SensorDataSource {

    IOT(1, "物联网数据"),

    MONITOR(2, "监测传感器数据"),

    API(-1, "API外部数据"),

    ;

    private final int code;

    private final String desc;

    public static SensorDataSource codeOf(int code) {
        return Arrays.stream(SensorDataSource.values()).filter(e -> code == e.code).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid SensorDataSource code: " + code));
    }
}

    
    