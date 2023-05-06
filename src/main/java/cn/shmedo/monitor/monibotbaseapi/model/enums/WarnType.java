package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WarnType {

    MONITOR(1, "在线监测报警"),

    CAMERA(2, "视频/摄像头报警"),

    TERMINAL(3, "智能终端报警"),

    ;

    private final Integer code;

    private final String desc;

    public static WarnType formCode(Integer code) {
        return Arrays.stream(WarnType.values())
                .filter(e -> e.getCode().equals(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("WarnType code " + code + " is not exist"));
    }

    public static WarnType formName(String name) {
        return WarnType.valueOf(name);
    }

}
