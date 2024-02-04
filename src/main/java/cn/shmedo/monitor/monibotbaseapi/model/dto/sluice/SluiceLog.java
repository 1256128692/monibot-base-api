package cn.shmedo.monitor.monibotbaseapi.model.dto.sluice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 控制记录
 *
 * @author Chengfs on 2023/11/24
 */
@Data
public class SluiceLog {

    private Integer id;

    private Integer sid;

    private LocalDateTime time;

    /**
     * 操作人
     */
    public Integer userID;

    /**
     * 运行状态 上、下、停
     */
    private Integer runningSta;

    /**
     * 软件模式 0开合度、1恒定水位、2恒定(累计)流量、3固定时间段、4恒定时长、5远程手动控制（上、下、停）、6自动校准 7恒定(瞬时)流量
     */
    private Integer software;

    /**
     * 硬件模式 0远程模式、1现地模式、2手动模式
     */
    private Integer hardware;

    /**
     * log日志描述
     */
    private String msg;

    /**
     * debug,info,warn,error
     */
    private String logLevel;

    public static final Integer MONITOR_TYPE = 62;
    public static final String TABLE = "tb_62_data";
    public static final String USER_ID = "userID";
    public static final String SOFTWARE = "software";
    public static final String HARDWARE = "hardware";
    public static final String RUNNING_STA = "runningSta";
    public static final String LOG_LEVEL = "logLevel";
    public static final String MSG = "msg";


}