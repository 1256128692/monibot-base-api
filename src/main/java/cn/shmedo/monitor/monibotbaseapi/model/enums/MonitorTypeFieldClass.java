package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 监测类型属性的类型
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-28 16:40
 **/
public enum MonitorTypeFieldClass {
    BaseProperties(1, "基础属性"),
    ExtendedProperties(2, "扩展属性"),
    ExtendedConfigurations(3, "扩展配置");

    private static final Map<Integer, MonitorTypeFieldClass> VALUES_MAP = new HashMap<>();

    static {
        for (MonitorTypeFieldClass value : values()) {
            VALUES_MAP.put(value.fieldClass, value);
        }
    }
    private Integer fieldClass;
    private String fieldStr;

    MonitorTypeFieldClass(Integer fieldClass, String fieldStr) {
        this.fieldClass = fieldClass;
        this.fieldStr = fieldStr;
    }

    public static boolean isValid(Integer value) {
        return VALUES_MAP.containsKey(value);
    }

    public Integer getFieldClass() {
        return fieldClass;
    }

    public String getFieldStr() {
        return fieldStr;
    }

    public static MonitorTypeFieldClass codeOf(Integer value) {
        return Optional.of(VALUES_MAP.get(value)).orElseThrow(() -> new IllegalArgumentException("Invalid MonitorTypeFieldClass value: " + value));
    }
}
