package cn.shmedo.monitor.monibotbaseapi.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-22 14:07
 */
public enum ThematicPlainMonitorItemEnum {
    DISTANCE(1, 1, 2, "水位"),
    VELOCITY_FLOW(2, 1, 3, "流速"),
    RAIN_FALL(3, 1, 31, "降雨量"),
    WETTING_LINE(4, 2,2 , "浸润线"),
    DISPLACEMENT(5, 3, 20, "内部变形");

    private final Integer code;
    private final Integer thematicType;
    private final Integer monitorType;
    private final String desc;

    public Integer getCode() {
        return code;
    }

    public Integer getThematicType() {
        return thematicType;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public String getDesc() {
        return desc;
    }

    ThematicPlainMonitorItemEnum(Integer code, Integer thematicType, Integer monitorType, String desc) {
        this.code = code;
        this.monitorType = monitorType;
        this.thematicType = thematicType;
        this.desc = desc;
    }

    public static List<Integer> getThematicMonitorTypeIDs() {
        return Arrays.stream(values()).map(ThematicPlainMonitorItemEnum::getMonitorType).distinct().toList();
    }

    public static ThematicPlainMonitorItemEnum getByMonitorType(final Integer monitorType) {
        return Arrays.stream(values()).filter(u -> !(u.equals(DISTANCE) || u.equals(WETTING_LINE))).filter(u ->
                u.getMonitorType().equals(monitorType)).findFirst().orElseThrow(() -> new IllegalArgumentException(
                "monitorType:" + monitorType + " in ThematicPlainMonitorItemEnum.getByMonitorType is unsupported "));
    }
}
