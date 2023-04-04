package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum DataSourceType {

    IOT(1, "物联网数据源"),
    MONITOR(2, "监测数据源"),

    ;

    private final int code;

    private final String desc;

    public static DataSourceType codeOf(int code) {
        return Arrays.stream(DataSourceType.values()).filter(e -> code == e.code).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid DataSource code: " + code));
    }
}
