package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-03 17:29
 **/
public enum CalType {
    Formula(1, "公式"),
    Script(2, "脚本"),
    HTTP(3, "外部http"),
    NoCal(-1, "公式");

    private static final Map<Integer, CalType> VALUES_MAP = new HashMap<>();

    static {
        for (CalType value : values()) {
            VALUES_MAP.put(value.getType(), value);
        }
    }
    private Integer type;
    private String typeStr;


    CalType(Integer type, String typeStr) {
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
