package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-09 15:46
 * @see WarnType
 */
public enum WarnLevel {
    MONITOR_LEVEL1(WarnType.MONITOR, 1, "一级报警"),
    MONITOR_LEVEL2(WarnType.MONITOR, 2, "二级报警"),
    MONITOR_LEVEL3(WarnType.MONITOR, 3, "三级报警"),
    MONITOR_LEVEL4(WarnType.MONITOR, 4, "四级报警"),
    CAMERA(WarnType.CAMERA, 1, "离线"),
    TERMINAL_LEVEL1(WarnType.TERMINAL, 1, "电量过低"),
    TERMINAL_LEVEL2(WarnType.TERMINAL, 2, "离线时间过长"),
    TERMINAL_LEVEL3(WarnType.TERMINAL, 3, "无数据时间过长");

    private final WarnType warnType;
    private final Integer levelCode;
    private final String desc;

    WarnLevel(WarnType warnType, Integer levelCode, String desc) {
        this.warnType = warnType;
        this.levelCode = levelCode;
        this.desc = desc;
    }

    public static List<WarnRecord> getEnum(final WarnType warnType) {
        return Arrays.stream(values()).filter(u -> u.warnType.equals(warnType)).map(WarnLevel::build).toList();
    }

    public static WarnLevel getWarnLevel(final WarnType warnType, final Integer levelCode) {
        return Arrays.stream(values()).filter(u -> u.warnType.equals(warnType)
                        && u.levelCode.equals(levelCode)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Illegal argument for warnType:" + warnType +
                        ",\tlevelCode:" + levelCode));
    }

    private WarnRecord build() {
        return new WarnRecord(this.levelCode, this.desc);
    }

    private record WarnRecord(Integer warnLevel, String desc) {
    }
}
