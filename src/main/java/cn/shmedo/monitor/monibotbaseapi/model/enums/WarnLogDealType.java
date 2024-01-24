package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 15:44
 */
@Getter
@RequiredArgsConstructor
public enum WarnLogDealType {
    WORK_ORDER(1, "派发工单"), DEAL_CONTENT(2, "填写处理意见");
    private final Integer code;
    private final String desc;

    public WarnLogDealType fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny()).orElseThrow();
    }
}
