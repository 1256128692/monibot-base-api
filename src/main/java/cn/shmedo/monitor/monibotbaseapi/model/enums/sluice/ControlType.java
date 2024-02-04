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
public enum ControlType {

    @JsonProperty("1")
    REMOTE(1, "远程控制", 0),

    @JsonProperty("2")
    MANUAL(2, "手动控制", 2),

    @JsonProperty("3")
    LOCAL(3, "现地控制", 1),
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    private final Integer deviceCode;

    public static ControlType formInt(@Nonnull Integer code) {
        return Arrays.stream(ControlType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ControlType code " + code + " is not exist"));
    }

    public static ControlType formDeviceCode(@Nonnull Integer deviceCode) {
        if (deviceCode != null) {
            return Arrays.stream(ControlType.values())
                    .filter(e -> e.getDeviceCode().equals(deviceCode)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("ControlType deviceCode " + deviceCode + " is not exist"));
        }
        return null;
    }
}
