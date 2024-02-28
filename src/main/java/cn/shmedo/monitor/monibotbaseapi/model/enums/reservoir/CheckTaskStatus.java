package cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CheckTaskStatus {

    //0-未开始 1-进行中 2-已过期 3-已结束
    @JsonProperty("0")
    UN_START(0, "未开始"),

    @JsonProperty("1")
    PROCESSING(1, "进行中"),

    @JsonProperty("2")
    EXPIRED(2, "已过期"),

    @JsonProperty("3")
    FINISHED(3, "已结束")
    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
}
