package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-26 16:58
 */
@Getter
@RequiredArgsConstructor
public enum DeviceWarnDeviceType {
    IOT_DEVICE(1, "物联网设备"), VIDEO_DEVICE(2, "视频设备");
    private final Integer code;
    private final String desc;

    public static DeviceWarnDeviceType fromCode(final Integer code) {
        return Optional.ofNullable(code).flatMap(u -> Arrays.stream(values()).filter(w -> w.getCode().equals(u)).findAny()).orElseThrow();
    }
}
