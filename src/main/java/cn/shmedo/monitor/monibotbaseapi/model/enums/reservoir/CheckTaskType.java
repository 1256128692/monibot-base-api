package cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckTaskType {

    //0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查
    @JsonProperty("0")
    OTHER(0, "其他"),

    @JsonProperty("1")
    DAILY(1, "日常巡检"),

    @JsonProperty("2")
    DEVICE(2, "设备巡查"),

    @JsonProperty("3")
    HIDDEN(3, "隐患点检查"),

    @JsonProperty("4")
    SAFETY(4, "安全检查")
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
