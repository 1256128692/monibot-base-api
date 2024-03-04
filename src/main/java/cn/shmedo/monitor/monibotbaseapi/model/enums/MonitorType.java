package cn.shmedo.monitor.monibotbaseapi.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MonitorType {
    @JsonProperty("2")
    WATER_LEVEL(2, "水位"),

    @JsonProperty("3")
    FLOW_VELOCITY(3, "流速"),

    @JsonProperty("4")
    WATER_QUALITY(4, "水质"),

    @JsonProperty("5")
    RAINFALL(5, "雨量"),

    @JsonProperty("6")
    TEMPERATURE(6, "温度"),

    @JsonProperty("7")
    HUMIDITY(7, "湿度"),

    @JsonProperty("8")
    WIND_SPEED(8, "风象"),

    @JsonProperty("9")
    SOIL_MOISTURE(9, "墒情"),

    @JsonProperty("10")
    SAND_CONTENT(10, "含沙量"),

    @JsonProperty("11")
    STRESS(11, "压力"),

    @JsonProperty("12")
    PRESSURE(12, "压强"),

    @JsonProperty("13")
    LEVEL(13, "物液位"),

    @JsonProperty("14")
    FLOW_CAPACITY(14, "流量"),

    @JsonProperty("17")
    DRY_BEACH(17, "干滩"),

    @JsonProperty("18")
    THREE_DIMENSIONAL_DISPLACEMENT_ABSOLUTE(18, "表面三维形变（绝对）"),
    @JsonProperty("19")
    THREE_DIMENSIONAL_DISPLACEMENT(19, "表面三维形变（相对）"),

    @JsonProperty("20")
    INTERNAL_TRIAXIAL_DISPLACEMENT(20, "内部三轴位移"),

    @JsonProperty("24")
    ONE_DIMENSIONAL_DISPLACEMENT(24, "表面一维形变（相对）"),

    @JsonProperty("31")
    WT_RAINFALL(31, "降雨量"),

    @JsonProperty("40")
    VIDEO(40, "视频"),

    @JsonProperty("41")
    EVAPORATION(41, "蒸发量"),

    @JsonProperty("42")
    SOIL_PH(42, "土壤PH"),

    @JsonProperty("43")
    ATMOSPHERIC_PRESSURE(43, "大气气压"),

    @JsonProperty("44")
    GATE(44, "水闸"),

    @JsonProperty("45")
    PUMPING_STATION(45, "泵站"),

    @JsonProperty("46")
    VALVE(46, "阀门"),

    @JsonProperty("47")
    NPK_CONTENT(47, "氮磷钾含量"),

    @JsonProperty("48")
    SOLAR_RADIATION(48, "太阳辐射"),

    @JsonProperty("54")
    SOIL_SALINITY_ELECTRICAL_CONDUCTIVITY(49, "土壤盐分电导率"),

    @JsonProperty("50")
    WET_LINE(50, "浸润线"),

    @JsonProperty("60")
    SLUICE_REGIMEN(60, "闸门水情"),

    @JsonProperty("70")
    CHANNEL_WATER_LEVEL(70, "渠道水位"),

    ;

    @EnumValue
    @JsonValue
    private final int key;
    private final String value;

    MonitorType(int key, String value) {
        this.key = key;
        this.value = value;
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
