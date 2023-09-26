package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum WaterQuality {

    TYPE_1(1, "I类"),
    TYPE_2(2, "Ⅱ类"),
    TYPE_3(3, "Ⅲ类"),
    TYPE_4(4, "Ⅳ类"),
    TYPE_5(5, "Ⅴ类"),
    TYPE_6(6, "劣五类"),

    TYPE_7(7, "达标"),

    TYPE_8(8, "超标");

    private final int key;
    private final String value;

    WaterQuality(int key, String value) {
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
        for (WaterQuality waterQuality : WaterQuality.values()) {
            if (waterQuality.getKey() == key) {
                return waterQuality.getValue();
            }
        }
        return null;
    }
}
