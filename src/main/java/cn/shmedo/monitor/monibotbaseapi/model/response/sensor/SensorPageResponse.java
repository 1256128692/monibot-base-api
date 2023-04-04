package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

/**
 * 传感器分页 响应体
 *
 * @author Chengfs on 2023/3/31
 */
@Data
public class SensorPageResponse {

    /**
     * 传感器ID
     */
    private Integer ID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 监测类型名称
     */
    private String monitorTypeName;

    /**
     * 传感器名称
     */
    private String name;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 显示排序字段
     */
    private Integer displayOrder;

    /**
     * 关联监测点ID
     */
    private Integer monitorPointID;

    /**
     * 关联监测点名称
     */
    private Integer monitorPointName;

    /**
     * 拓展信息
     */
    private String exValues;

    /**
     * 是否启用
     */
    private Boolean enable;

}

    
    