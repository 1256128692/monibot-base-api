package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数的类型
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 14:37
 **/
public enum ParamSubjectType {
    Formula(1, "公式"),
    Script(2, "脚本"),
    Sensor(3, "传感器"),
    Template(4, "模板");
    private static final Map<Integer, ParamSubjectType> VALUES_MAP = new HashMap<>();

    static {
        for (ParamSubjectType value : values()) {
            VALUES_MAP.put(value.type, value);
        }
    }

    private Integer type;
    private String typeStr;

    public static boolean isValid(Integer value) {
        return VALUES_MAP.containsKey(value);
    }

    ParamSubjectType(Integer type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

}
