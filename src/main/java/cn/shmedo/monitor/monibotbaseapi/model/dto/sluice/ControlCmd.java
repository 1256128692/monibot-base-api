package cn.shmedo.monitor.monibotbaseapi.model.dto.sluice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 闸门控制指令
 *
 * @author Chengfs on 2023/11/24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlCmd {

    private final String CMD_NAME = "md_setgatecfg";
    private final String INDEX = "index";
    private final String TYPE = "type";
    private final String CRACK_LEVEL = "crackLevel";
    private final String WATER_LEVEL = "waterLevel";
    private final String START_TIME = "startTime";
    private final String STOP_TIME = "stopTime";
    private final String FIXED_TIME = "fixedTime";
    private final String TOTAL_FLOW = "totalFlow";
    private final String MOTOR_DIR = "motorDir";

    //$cmd=md_setgatecfg&index=1&type=0&crackLevel=1.0&waterLevel=1.2&startTime=123&stopTime=123&fixedTime=123&totalFlow=12&motorDir=1&apikey=123456&msgid=123456

    /**
     * 电机序号
     */
    private Integer index;

    /**
     * 软件模式<br/>
     * 0：开合度 1：恒定水位 2：恒定流量 3：固定时间段 4：恒定时长 5：远程手动控制（上、下、停）
     */
    private Integer type;

    /**
     * 闸门开合度大小
     */
    private Double crackLevel;

    /**
     * 恒定水位大小
     */
    private Double waterLevel;

    /**
     * 固定、恒定时间段开始时间
     */
    private LocalDateTime startTime;

    /**
     * 固定时间段结束时间
     */
    private LocalDateTime stopTime;

    /**
     * 恒定时间时长大小 (min)
     */
    private Integer fixedTime;

    /**
     * 累计流量 m³
     */
    private Integer totalFlow;

    /**
     * 远程手动 （0上、1下、2停）
     */
    private Integer motorDir;

    public String toRaw() {
        List<String> params = new ArrayList<>();
        params.add(CMD_NAME);
        Optional.ofNullable(index).ifPresent(e -> params.add(INDEX + "=" + e));
        Optional.ofNullable(type).ifPresent(e -> params.add(TYPE + "=" + e));
        Optional.ofNullable(crackLevel).ifPresent(e -> params.add(CRACK_LEVEL + "=" + e));
        Optional.ofNullable(waterLevel).ifPresent(e -> params.add(WATER_LEVEL + "=" + e));
        Optional.ofNullable(startTime).ifPresent(e -> params.add(START_TIME + "=" + e.toEpochSecond(ZoneOffset.ofHours(8))));
        Optional.ofNullable(stopTime).ifPresent(e -> params.add(STOP_TIME + "=" + e.toEpochSecond(ZoneOffset.ofHours(8))));
        Optional.ofNullable(fixedTime).ifPresent(e -> params.add(FIXED_TIME + "=" + e));
        Optional.ofNullable(totalFlow).ifPresent(e -> params.add(TOTAL_FLOW + "=" + e));
        Optional.ofNullable(motorDir).ifPresent(e -> params.add(MOTOR_DIR + "=" + e));
        return String.join("&", params);
    }
}