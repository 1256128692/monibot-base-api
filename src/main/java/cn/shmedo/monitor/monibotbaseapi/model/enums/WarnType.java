package cn.shmedo.monitor.monibotbaseapi.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WarnType {

    @JsonProperty("1")
    MONITOR(1, "在线监测报警"),

    @JsonProperty("2")
    CAMERA(2, "视频/摄像头报警"),

    @JsonProperty("3")
    TERMINAL(3, "智能终端报警"),

    @JsonProperty("4")
    FLOOD(4, "江河洪水预警"),

    @JsonProperty("5")
    DANGEROUS(5, "险情预警"),

    @JsonProperty("6")
    RAINSTORM(6, "暴雨预警"),

    ;
    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;

    public static WarnType formCode(Integer code) {
        return Arrays.stream(WarnType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("WarnType code " + code + " is not exist"));
    }

    public static WarnType formName(String name) {
        return WarnType.valueOf(name);
    }

}
