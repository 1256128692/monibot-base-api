package cn.shmedo.monitor.monibotbaseapi.util.sensor;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-11 13:26
 */
public final class SensorWarnUtils {
    private final static List<Integer> WARN_STATUS_LIST = List.of(1, 2, 3, 4);

    /**
     * 判断传感器是否处于报警状态
     *
     * @param sensorStatus 传感器状态
     */
    public static boolean sensorIsWarn(@Nullable Integer sensorStatus) {
        return Objects.nonNull(sensorStatus) && WARN_STATUS_LIST.contains(sensorStatus);
    }
}
