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
 * 量水点采集要素 枚举
 */
@Getter
@AllArgsConstructor
public enum MonitorElement {

    @JsonProperty("2")
    WATER_LEVEL(2, "水位"),

    @JsonProperty("3")
    FLOW_SPEED(3, "流速"),

    @JsonProperty("14")
    FLOW_RATE(14, "流量"),

    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;


    public static ControlType formInt(@Nonnull Integer code) {
        return Arrays.stream(ControlType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("MonitorElement code " + code + " is not exist"));
    }
}
