package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:51
 */
@Getter
@RequiredArgsConstructor
public enum WarnLevelStyle {
    COLOR(1, "红色,橙色,黄色,蓝色"), ARAB(2, "1级,2级,3级,4级"), ROMA(3, "Ⅰ级,Ⅱ级,Ⅲ级,Ⅳ级");
    private final Integer code;
    private final String desc;

    public static WarnLevelStyle fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny())
                .orElseThrow(() -> new IllegalArgumentException("Illegal WarnLevelStyle code,code: " + code));
    }
}
