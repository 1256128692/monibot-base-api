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
 * 量水点计算方法 枚举
 */
@Getter
@AllArgsConstructor
public enum CalculateType {

    @JsonProperty("1")
    WATER_WORK(1, "水工建筑法"),

    @JsonProperty("2")
    WATER_LEVEL_FLOW(2, "水位流量关系法"),

    @JsonProperty("3")
    FLOW_RATE_AREA(3, "流速面积法"),

    @JsonProperty("4")
    MEASURING_WEIR(4, "量水堰槽法"),

    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;


    public static ControlType formInt(@Nonnull Integer code) {
        return Arrays.stream(ControlType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("CalculateType code " + code + " is not exist"));
    }
}
