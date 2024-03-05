package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 15:17
 */
@Getter
@RequiredArgsConstructor
public enum DataDeviceWarnType {
    DATA(1, "数据报警"),
    DEVICE(2, "设备报警"),
    EVENT(3, "事件"),
    WORK_ORDER(4, "工单");
    private final Integer code;
    private final String desc;

    public static DataDeviceWarnType fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny()).orElseThrow();
    }
}
