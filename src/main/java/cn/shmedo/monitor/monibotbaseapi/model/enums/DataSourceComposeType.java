package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum DataSourceComposeType {

    SINGLE_IOT(1, "单一物模型单一传感器"),

    MULTI_IOT(2, "多个物联网传感器（同一物模型多个或者不同物模型多个）"),

    MIXING(3, "物联网传感器+监测传感器"),

    SINGLE_MONITOR(4, "单个监测传感器"),

    MULTI_MONITOR(5, "多个监测传感器"),

    API(100, "API推送"),

    MANUAL_MONITOR_DATA(500, "人工监测数据"),

    ;
    private static final Map<Integer, DataSourceComposeType> VALUES_MAP = new HashMap<>();

    static {
        for (DataSourceComposeType value : values()) {
            VALUES_MAP.put(value.getCode(), value);
        }
    }
    private final int code;

    private final String desc;

    public static DataSourceComposeType codeOf(int code) {
        DataSourceComposeType dataSourceComposeType = VALUES_MAP.get(code);
        if (dataSourceComposeType == null){
            throw new IllegalArgumentException("Invalid DataSourceComposeType code: " + code);
        }else {
            return dataSourceComposeType;
        }

    }
    public static boolean isValid(Integer value) {
        return VALUES_MAP.containsKey(value);
    }
}
