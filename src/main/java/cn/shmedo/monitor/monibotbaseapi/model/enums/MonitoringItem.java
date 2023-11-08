package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum MonitoringItem {

    DAM_FRONT_WATER_LEVEL(1, "坝前水位"),
    CROSS_SECTION_WATER_LEVEL(2, "断面水位"),
    CROSS_SECTION_FLOW_VELOCITY(3, "断面流速"),
    RESERVOIR_WATER_QUALITY(4, "水库水质"),
    RAINFALL(5, "雨量"),
    TEMPERATURE(6, "温度"),
    HUMIDITY(7, "湿度"),
    WIND_SPEED(8, "风速"),
    SOIL_MOISTURE(9, "土壤含水量"),
    SAND_CONTENT(10, "含沙量"),
    RIVER_WATER_QUALITY(11, "河道水质"),
    WET_LINE(-1, "浸润线");

    private final int key;
    private final String value;

    MonitoringItem(int key, String value) {
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
        for (MonitoringItem monitoringProject : MonitoringItem.values()) {
            if (monitoringProject.getKey() == key) {
                return monitoringProject.getValue();
            }
        }
        return null;
    }
}
