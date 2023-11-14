package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum QueryType {

    TYPE_MEAN(0, "mean"),
    TYPE_FIRST(1, "first");

    private final int key;
    private final String value;

    QueryType(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getValueByKey(int key) {
        for (QueryType queryType : QueryType.values()) {
            if (queryType.getKey() == key) {
                return queryType.getValue();
            }
        }
        return null;
    }

    public static boolean isValidDensity(int key) {
        for (QueryType queryType : QueryType.values()) {
            if (queryType.getKey() == key) {
                return true;
            }
        }
        return false;
    }
}
