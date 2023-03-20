package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 监测类型
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbMonitorType implements Serializable {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 小于10000是预定义预留

整个系统内不重复
    */
    private Integer monitorType;

    /**
    * 监测类型名称
    */
    private String typeName;

    /**
    * 监测类型别名
    */
    private String typeAlias;

    /**
    * 排序字段
    */
    private Integer displayOrder;

    /**
    * 监测点允许关联多传感器标识
    */
    private Boolean multiSensor;

    /**
    * 创建类型
    */
    private Byte createType;

    /**
    * 公司ID，预定义监测类型为-1
    */
    private Integer companyID;

    /**
    * 拓展字段
    */
    private String exValues;

    private static final long serialVersionUID = 1L;
}