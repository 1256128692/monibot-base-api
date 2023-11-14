package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum MonitorType {

    WATER_LEVEL(2, "水位"),
    FLOW_VELOCITY(3, "流速"),
    WATER_QUALITY(4, "水质"),
    RAINFALL(5, "雨量"),
    TEMPERATURE(6, "温度"),
    HUMIDITY(7, "湿度"),
    WIND_SPEED(8, "风象"),
    SOIL_MOISTURE(9, "墒情"),
    SAND_CONTENT(10, "含沙量"),
    STRESS(11, "压力"),
    PRESSURE(12, "压强"),
    LEVEL(13, "物液位"),
    FLOW_CAPACITY(14, "流量"),
    THREE_DIMENSIONAL_DISPLACEMENT(19, "表面三维形变（相对）"),
    ONE_DIMENSIONAL_DISPLACEMENT(24, "表面一维形变（相对）"),
    INTERNAL_TRIAXIAL_DISPLACEMENT(20, "内部三轴位移"),

    WT_RAINFALL(31, "降雨量"),
    VIDEO(40, "视频"),

    EVAPORATION(41, "蒸发量"),
    SOIL_PH(42, "土壤PH"),
    ATMOSPHERIC_PRESSURE(43, "大气气压"),
    GATE(44, "水闸"),
    PUMPING_STATION(45, "泵站"),
    VALVE(46, "阀门"),
    NPK_CONTENT(47, "氮磷钾含量"),
    SOLAR_RADIATION(48, "太阳辐射"),
    SOIL_SALINITY_ELECTRICAL_CONDUCTIVITY(49, "土壤盐分电导率");

    private final int key;
    private final String value;

    MonitorType(int key, String value) {
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
        for (MonitorType monitoringType : MonitorType.values()) {
            if (monitoringType.getKey() == key) {
                return monitoringType.getValue();
            }
        }
        return null;
    }
}
