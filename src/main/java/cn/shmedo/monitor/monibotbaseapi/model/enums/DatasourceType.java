package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum DataSourceType {

    IOT(1, "物联网数据源"),
    MONITOR(2, "监测数据源"),

    ;

    private final int code;

    private final String desc;

    private static final Map<Integer, DataSourceType> VALUES_MAP = new HashMap<>();

    static {
        for (DataSourceType value : values()) {
            VALUES_MAP.put(value.getCode(), value);
        }
    }

    public static DataSourceType codeOf(int code) {
        DataSourceType dataSourceType = VALUES_MAP.get(code);
        if (dataSourceType == null){
            throw new IllegalArgumentException("Invalid DataSourceComposeType code: " + code);
        }else {
            return dataSourceType;
        }

    }
    public static boolean isValid(Integer value) {
        return VALUES_MAP.containsKey(value);
    }
}
