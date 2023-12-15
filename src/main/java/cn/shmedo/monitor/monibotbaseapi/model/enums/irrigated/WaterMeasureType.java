package cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated;

import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 量水类型 枚举
 */
@Getter
@AllArgsConstructor
public enum WaterMeasureType {

    @JsonProperty("1")
    SPECIAL(1, "特设量水设施量水", CalculateType.MEASURING_WEIR),

    @JsonProperty("2")
    INSTRUMENT(2, "仪器仪表量水", CalculateType.WATER_LEVEL_FLOW),

    @JsonProperty("3")
    WATERWORK(3, "水工建筑量水", CalculateType.WATER_WORK),

    @JsonProperty("4")
    STANDARD_SECTION(4, "标准断面量水", CalculateType.WATER_LEVEL_FLOW),

    @JsonProperty("5")
    CURRENT_METER(5, "流速仪量水", CalculateType.FLOW_RATE_AREA),

    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    private final CalculateType calculateType;

    public static ControlType formInt(@Nonnull Integer code) {
        return Arrays.stream(ControlType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("WaterMeasureType code " + code + " is not exist"));
    }

    public boolean validWaterMeasureWay(WaterMeasureWay way) {
        if (way != null) {
            return this.equals(way.getWaterMeasureType());
        }
        return false;
    }

    public boolean validCalculateType(CalculateType calculateType) {
        if (calculateType != null) {
            return this.calculateType.equals(calculateType);
        }
        return false;
    }

    public boolean validElements(MonitorType... element) {
        if (element != null) {
            switch (this) {
                case STANDARD_SECTION -> {
                    return Arrays.stream(element).allMatch(MonitorType.WATER_LEVEL::equals);
                }
                case CURRENT_METER -> {
                    return Arrays.stream(element)
                            .allMatch(e -> MonitorType.FLOW_VELOCITY.equals(e) || MonitorType.FLOW_CAPACITY.equals(e));
                }
                default -> {
                    return Arrays.stream(element)
                            .allMatch(e -> MonitorType.WATER_LEVEL.equals(e) || MonitorType.FLOW_VELOCITY.equals(e) || MonitorType.FLOW_CAPACITY.equals(e));
                }
            }
        }
        return false;
    }
}
