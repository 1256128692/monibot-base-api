package cn.shmedo.monitor.monibotbaseapi.model.enums.sluice;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author Chengfs on 2023/11/21
 */
@Getter
@AllArgsConstructor
public enum ControlActionKind {

    @JsonProperty("1")
    STOP(1, "停止", 2),

    @JsonProperty("2")
    RISE(2, "上升", 0),

    @JsonProperty("3")
    FALL(3, "下降", 1),

    @JsonProperty("4")
    AUTO(4, "自动控制", null),

    /**
     * 开启 通过开合度控制
     */
    @JsonProperty("5")
    OPEN(5, "开启", 5),

    /**
     * 关闭 通过开合度控制
     */
    @JsonProperty("6")
    CLOSE(6, "关闭", 5),

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
}