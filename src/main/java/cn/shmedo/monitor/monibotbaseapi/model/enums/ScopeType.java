package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum ScopeType {

    SPECIAL_ANALYSIS(0, "专题分析"),
    HISTORICAL_DATA(1, "历史数据");

    private final int code;
    private final String description;

    ScopeType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValidScopeType(int code) {
        for (ScopeType scopeType : ScopeType.values()) {
            if (scopeType.getCode() == code) {
                return true;
            }
        }
        return false;
    }

    public static String getDescriptionByCode(int code) {
        for (ScopeType scopeType : ScopeType.values()) {
            if (scopeType.getCode() == code) {
                return scopeType.getDescription();
            }
        }
        return null;
    }
}
