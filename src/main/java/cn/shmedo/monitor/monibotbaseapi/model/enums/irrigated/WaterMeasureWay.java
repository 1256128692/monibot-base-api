package cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated;

import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 量水方式 枚举
 */
@Getter
@AllArgsConstructor
public enum WaterMeasureWay {

    @JsonProperty("11")
    TRIANGULAR_WEIR(11, "三角堰", WaterMeasureType.SPECIAL),

    @JsonProperty("12")
    RECTANGULAR_WEIR(12, "矩形堰", WaterMeasureType.SPECIAL),

    @JsonProperty("13")
    TRAPEZOIDAL_WEIR(13, "梯形堰", WaterMeasureType.SPECIAL),

    @JsonProperty("14")
    BARREL(14, "巴歇尔槽", WaterMeasureType.SPECIAL),

    @JsonProperty("15")
    NO_THROAT_WEIR(15, "无喉道段量水槽", WaterMeasureType.SPECIAL),

    @JsonProperty("21")
    FLOAT_WATER_GAUGE(21, "浮子式水位计", WaterMeasureType.INSTRUMENT),

    @JsonProperty("22")
    ULTRASONIC_WATER_GAUGE(22, "超声波水位计", WaterMeasureType.INSTRUMENT),

    @JsonProperty("23")
    PRESSURE_WATER_GAUGE(23, "压力式水位计", WaterMeasureType.INSTRUMENT),

    @JsonProperty("24")
    WATER_METER(24, "水表", WaterMeasureType.INSTRUMENT),

    @JsonProperty("25")
    ELECTRIC_FLOW_METER(25, "电磁流量计", WaterMeasureType.INSTRUMENT),

    @JsonProperty("26")
    ULTRASONIC_FLOW_METER(26, "超声波流量计", WaterMeasureType.INSTRUMENT),

    @JsonProperty("27")
    OTHER_WATER_METER(27, "其他量水仪表", WaterMeasureType.INSTRUMENT),

    @JsonProperty("31")
    SLUICE(31, "水闸", WaterMeasureType.WATERWORK),

    @JsonProperty("32")
    AQUEDUCT(32, "渡槽", WaterMeasureType.WATERWORK),

    @JsonProperty("33")
    FALLING_WATER(33, "跌水", WaterMeasureType.WATERWORK),

    @JsonProperty("34")
    DISCHARGE_CULVER(34, "放水涵", WaterMeasureType.WATERWORK),

    @JsonProperty("35")
    OTHER(35, "其他", WaterMeasureType.WATERWORK),

    @JsonProperty("41")
    U_PROFILES(41, "U形断面", WaterMeasureType.STANDARD_SECTION),

    @JsonProperty("42")
    T_PROFILES(42, "梯形断面", WaterMeasureType.STANDARD_SECTION),

    @JsonProperty("43")
    R_PROFILES(43, "矩形断面", WaterMeasureType.STANDARD_SECTION),

    @JsonProperty("51")
    CURRENT_METER(51, "流速仪", WaterMeasureType.CURRENT_METER),

    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    private final WaterMeasureType waterMeasureType;


    public static ControlType formInt(@Nonnull Integer code) {
        return Arrays.stream(ControlType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("WaterMeasureWay code " + code + " is not exist"));
    }
}
