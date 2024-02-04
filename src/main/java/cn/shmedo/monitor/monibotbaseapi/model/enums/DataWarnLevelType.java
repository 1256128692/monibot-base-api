package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 16:09
 */
@Getter
@RequiredArgsConstructor
public enum DataWarnLevelType {
    FOUR_LEVEL(1, Set.of(1, 2, 3, 4)),
    THREE_LEVEL(2, Set.of(1, 2, 3));
    private final Integer code;
    /**
     * 可选的报警等级枚举key,枚举值:<br>1: 红色/1级/Ⅰ级;<br>2: 橙色/2级/Ⅱ级;<br>3: 黄色/3级/Ⅲ级;<br>4: 蓝色/4级/Ⅳ级，依次往后推
     */
    private final Set<Integer> warnLevelSet;

    public static DataWarnLevelType fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny())
                .orElseThrow(() -> new IllegalArgumentException("Illegal DataWarnLevelType code,code: " + code));
    }
}
