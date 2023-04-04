package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 模板的数据源的类型
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-03 17:42
 **/
public enum DatasourceType {
    IotSensor(1, "物联网传感器"),
    MonitorSensor(2, "监测传感器");

    private static final Map<Integer, DatasourceType> VALUES_MAP = new HashMap<>();

    static {
        for (DatasourceType value : values()) {
            VALUES_MAP.put(value.getType(), value);
        }
    }
    private Integer type;
    private String typeStr;


    DatasourceType(Integer type, String typeStr) {
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
