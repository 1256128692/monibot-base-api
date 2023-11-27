package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import lombok.Data;

import java.util.List;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class SluiceInfo {

    /**
     * 最大闸后水位 (项目属性)
     */
    private Double maxBackWaterLevel;

    /**
     * 最小闸后水位 (项目属性)
     */
    private Double minBackWaterLevel;

    /**
     * 最大过闸流量 (项目属性)
     */
    private Double maxFlowRate;

    /**
     * 闸前水位 (水情数据 监测类型)
     */
    private Double frontWaterLevel;

    /**
     * 闸后水位 (水情数据 监测类型)
     */
    private Double backWaterLevel;

    /**
     * 瞬时流量(m³/s) (水情数据 监测类型)
     */
    private Double flowRate;

    /**
     * 累计流量 (水情数据 监测类型)
     */
    private Double flowTotal;

    /**
     * 闸门列表
     */
    private List<GateInfo> gates;
}