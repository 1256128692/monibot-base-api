package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 统计方式枚举
 *
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 11:29
 */
@Getter
@RequiredArgsConstructor
public enum StatisticalMethodEnum {
    LATEST(1, "最新一条"), AVG(2, "平均值"), SUM(3, "阶段累计"), DIFF(4, "阶段变化");
    private final Integer code;
    private final String desc;

    public static StatisticalMethodEnum getByCode(final Integer code) {
        return Arrays.stream(values()).filter(u -> u.getCode().equals(code)).findAny()
                .orElseThrow(() -> new RuntimeException("Illegal statistical method code,code:" + code));
    }
}
