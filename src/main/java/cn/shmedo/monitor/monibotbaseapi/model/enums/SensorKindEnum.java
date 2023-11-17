package cn.shmedo.monitor.monibotbaseapi.model.enums;

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
    AUTO_KIND(1),
    /**
     * 融合传感器
     */
    MERGE_KIND(2),
    /**
     * 人工传感器
     */
    MANUAL_KIND(3);
    private final Integer code;

    public static SensorKindEnum fromCode(final int code) {
        return Arrays.stream(values()).filter(u -> u.getCode().equals(code)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("未识别的传感器类型,code:" + code));
    }
}
