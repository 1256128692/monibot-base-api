package cn.shmedo.monitor.monibotbaseapi.model.enums.sluice;

import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.SluiceStatus;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    OPEN(5, "开启", 0),

    /**
     * 关闭 通过开合度控制
     */
    @JsonProperty("6")
    CLOSE(6, "关闭", 0),

    ;

    @EnumValue
    @JsonValue
    private final Integer code;
    private final String desc;
    private final Integer deviceCode;

    public static ControlActionKind fromSluiceStatus(SluiceStatus status) {
        if (status.getSoftware() == 5) {
            switch (status.getMotorSta()) {
                case 0:
                    return ControlActionKind.RISE;
                case 1:
                    return ControlActionKind.FALL;
                case 2:
                    return ControlActionKind.STOP;
            }
        } else {
            return ControlActionKind.AUTO;
        }
        return null;
    }
}