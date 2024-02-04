package cn.shmedo.monitor.monibotbaseapi.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-17 14:40
 */
@Getter
@AllArgsConstructor
public enum SensorKindEnum {
    /**
     * 自动化传感器
     */
    @JsonProperty("1")
    AUTO_KIND((byte) 1),
    /**
     * 融合传感器
     */
    @JsonProperty("2")
    MERGE_KIND((byte) 2),
    /**
     * 人工传感器
     */
    @JsonProperty("3")
    MANUAL_KIND((byte) 3);

    @EnumValue
    @JsonValue
    private final Byte code;

    public static SensorKindEnum fromCode(final byte code) {
        return Arrays.stream(values()).filter(u -> u.getCode().equals(code)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("未识别的传感器类型,code:" + code));
    }
}
