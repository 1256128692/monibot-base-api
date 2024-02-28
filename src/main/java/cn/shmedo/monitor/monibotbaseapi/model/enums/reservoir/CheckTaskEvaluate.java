package cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckTaskEvaluate {

    //0-正常 1-异常
    @JsonProperty("0")
    NORMAL(0, "正常"),

    @JsonProperty("1")
    ABNORMAL(1, "异常")
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
