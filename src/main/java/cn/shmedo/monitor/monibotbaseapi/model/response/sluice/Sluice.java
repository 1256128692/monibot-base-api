package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Sluice extends SluiceSimple {

    /**
     * 启闭状态 0：关闭；1：开启
     */
    private Integer openStatus;

    /**
     * 闸门列表
     */
    private List<Gate> gates;

    /**
     * 闸前水位 (水情数据 监测类型)
     */
    private Double frontWaterLevel;

    /**
     * 闸后水位 (水情数据 监测类型)
     */
    private Double backWaterLevel;

    /**
     * 瞬时流速(m3/s) (水情数据 监测类型)
     */
    private Double flowRate;

    /**
     * 水情数据
     */
    private WaterData waterData;

    /**
     * 视频监测组ID
     */
    private Integer videoMonitorGroupID;

    /**
     * 最后采集时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastCollectTime;

    public record WaterData(Integer monitorType, Integer monitorItemID, Collection<Integer> monitorPointIDList) {}

//    public record VideoMonitor(Collection<Integer> monitorPointIDList) {}
}