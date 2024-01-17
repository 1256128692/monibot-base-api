package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 比较方式
 *
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 16:40
 */
@Getter
@RequiredArgsConstructor
public enum CompareMode {
    IN(1, 2, List.of("upper", "lower"), "在区间内"),
    OUT(2, 2, List.of("upper", "lower"), "偏离区间"),
    GT(3, 1, List.of("upper"), "大于"),
    GTE(4, 1, List.of("upper"), "大于等于"),
    LT(5, 1, List.of("upper"), "小于"),
    LTE(6, 1, List.of("upper"), "小于等于");

    private final Integer code;
    /**
     * 报警等级阈值配置json中,含有元素的个数<br>
     * 区间相关的比较方式,会有上限、下限两个值; 其他情况下暂时只有一个值
     *
     * @deprecated 下版本将被移除
     */
    @Deprecated
    private final Integer fieldCount;
    private final List<String> configKeyList;
    private final String desc;

    public static CompareMode fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny())
                .orElseThrow(() -> new IllegalArgumentException("Illegal CompareMode code,code: " + code));
    }
}
