package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum SensorSatusType {

    TYPE_1(1, "一级报警"),
    TYPE_2(2, "二级报警"),
    TYPE_3(3, "三级报警"),
    TYPE_4(4, "四级报警"),
    TYPE_0(0, "正常"),
    TYPE_ERROR(-1, "无数据");

    private final int key;
    private final String value;

    SensorSatusType(int key, String value) {
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
        for (SensorSatusType sensorSatusType : SensorSatusType.values()) {
            if (sensorSatusType.getKey() == key) {
                return sensorSatusType.getValue();
            }
        }
        return null;
    }


}
