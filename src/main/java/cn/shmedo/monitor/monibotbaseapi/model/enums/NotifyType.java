package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-12 16:38
 */
@Getter
@RequiredArgsConstructor
public enum NotifyType {
    DEVICE_NOTIFY(1, "设备报警通知"), DATA_NOTIFY(2, "数据报警通知");
    private final Integer code;
    private final String desc;
}
