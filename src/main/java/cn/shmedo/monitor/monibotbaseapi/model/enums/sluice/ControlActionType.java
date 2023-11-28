package cn.shmedo.monitor.monibotbaseapi.model.enums.sluice;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ControlActionType {

    //1恒定水位 2恒定流量 3恒定闸位 4时长控制 5时段控制 6总量控制
    @JsonProperty("1")
    CONSTANT_WATER_LEVEL(1, "恒定水位", 1, ConstantWaterLevel.class),

    /**
     * 恒定流量（瞬时流量）
     */
    @JsonProperty("2")
    CONSTANT_FLOW(2, "恒定流量", 7, ConstantFlow.class),

    @JsonProperty("3")
    CONSTANT_SLUICE_LEVEL(3, "恒定闸位", 0, ConstantSluiceLevel.class),

    @JsonProperty("4")
    TIME_CONTROL(4, "时长控制", 4, TimeControl.class),

    @JsonProperty("5")
    TIME_PERIOD_CONTROL(5, "时段控制", 3, TimePeriodControl.class),

    @JsonProperty("6")
    TOTAL_CONTROL(8, "总量控制", 2, TotalControl.class),
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    private final Integer deviceCode;
    private final Class<?> validGroup;

    public static ControlActionType formInt(@Nonnull Integer code) {
        return Arrays.stream(ControlActionType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ControlActionType code " + code + " is not exist"));
    }

    public static interface ConstantWaterLevel {}
    public static interface ConstantFlow {}
    public static interface ConstantSluiceLevel {}
    public static interface TimeControl {}
    public static interface TimePeriodControl {}
    public static interface TotalControl {}
}
