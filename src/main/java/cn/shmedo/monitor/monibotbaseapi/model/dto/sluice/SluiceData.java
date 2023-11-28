package cn.shmedo.monitor.monibotbaseapi.model.dto.sluice;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 水情数据
 *
 * @author Chengfs on 2023/11/24
 */
@Data
public class SluiceData {
    public static final Integer MONITOR_TYPE = 60;
    public static final String TABLE_NAME = "tb_60_data";
    public static final String TIME = "time";
    public static final String TOTAL_FLOW = "total_flow";
    public static final String FLOW_RATE = "flow_rate";
    public static final String AFTERWATER = "afterwater";
    public static final String FRONTWATER = "frontwater";


    private Integer sid;

    private LocalDateTime time;

    /**
     * 累计流量
     */
    private Double totalFlow;

    /**
     * 瞬时流速
     */
    private Double flowRate;

    /**
     * 闸后水位
     */
    private Double afterwater;

    /**
     * 闸前水位
     */
    private Double frontwater;
}