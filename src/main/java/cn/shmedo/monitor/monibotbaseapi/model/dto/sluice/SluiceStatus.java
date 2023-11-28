package cn.shmedo.monitor.monibotbaseapi.model.dto.sluice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 运行状态
 *
 * @author Chengfs on 2023/11/24
 */
@Data
public class SluiceStatus {

    private Integer sid;

    private LocalDateTime time;

    /**
     * 驱动器温度
     */
    private Double gateTemp;

    /**
     * 电机实时电流
     */
    private Double gateCurrent;

    /**
     * 驱动器供电电压
     */
    private Double gateVolt;

    /**
     * 闸门最大开合度
     */
    private Double gateOpenMax;

    /**
     * 电机运行状态 上、下、停
     */
    private Integer motorSta;

    /**
     * 限位开关状态 0：上下限位均未触发；1：上限位触发；2：下限位触发；3：上下均触发
     */
    private Integer limitSwSta;

    /**
     * 闸门开度 闸门开度
     */
    private Double gateOpen;

    /**
     * 启闭状态 0：关闭；1：开启
     */
    private Integer gateSta;

    /**
     * 软件模式 开合度、恒定水位、累计流量、固定时间段、恒定时长、单独控制、自动校准
     */
    private Integer software;

    /**
     * 硬件模式 0远程模式、1现地模式、2手动模式
     */
    private Integer hardware;

    public static final Integer MONITOR_TYPE = 61;
    public static final String TABLE_NAME = "tb_61_data";
    public static final String GATE_TEMP = "gateTemp";
    public static final String GATE_CURRENT = "gateCurrent";
    public static final String GATE_VOLT = "gateVolt";
    public static final String GATE_OPEN_MAX = "gateOpenMax";
    public static final String MOTOR_STA = "motorSta";
    public static final String LIMIT_SW_STA = "limitSwSta";
    public static final String GATE_OPEN = "gateOpen";
    public static final String GATE_STA = "gateSta";
    public static final String SOFTWARE = "software";
    public static final String HARDWARE = "hardware";

}