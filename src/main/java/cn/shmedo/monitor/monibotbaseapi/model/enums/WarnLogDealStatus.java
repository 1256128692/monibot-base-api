package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 15:32
 */
@Getter
@RequiredArgsConstructor
public enum WarnLogDealStatus {
    UNHANDLED(0, "未处理"), DEAL(1, "已处理"), CANCEL(2, "取消");
    private final Integer code;
    private final String desc;

    public static WarnLogDealStatus fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny()).orElseThrow();
    }
}
