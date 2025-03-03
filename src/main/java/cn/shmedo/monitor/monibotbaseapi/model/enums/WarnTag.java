package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:50
 */
@Getter
@RequiredArgsConstructor
public enum WarnTag {
    TYPE1(1, "报警"),
    TYPE2(2, "告警"),
    TYPE3(3, "预警");
    private final Integer code;
    private final String desc;

    public static WarnTag fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny())
                .orElseThrow(() -> new IllegalArgumentException("Illegal WarnTag code,code: " + code));
    }
}
