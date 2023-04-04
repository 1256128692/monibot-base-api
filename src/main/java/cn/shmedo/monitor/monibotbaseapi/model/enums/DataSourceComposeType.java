package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-03 17:34
 **/
public enum DataSourceComposeType {
    SingleIotSensor(1, "单个物联网传感器"),
    MultiIotSensor(2, "多个物联网传感器"),
    IotSensorAndMonitorSensor(3, "物联网传感器+监测传感器"),
    SingleMonitorSensor(4, "单个监测传感器"),
    MultiMonitorSensor(5, "多个监测传感器"),
    API(100, "api推送"),
    ManualData(500, "人工监测数据");

    private static final Map<Integer, DataSourceComposeType> VALUES_MAP = new HashMap<>();

    static {
        for (DataSourceComposeType value : values()) {
            VALUES_MAP.put(value.getType(), value);
        }
    }
    private Integer type;
    private String typeStr;


    DataSourceComposeType(Integer type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public static boolean isValid(Integer value) {
        return VALUES_MAP.containsKey(value);
    }

}