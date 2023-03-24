package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 监测类型字段属性
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbMonitorTypeField implements Serializable {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 监测类型
    */
    private Integer monitorType;

    /**
    * 属性名称
    */
    private String fieldName;

    /**
    * 属性标识
    */
    private String fieldToken;

    /**
    * 字段类型，String,Double,Long
    */
    private String fieldDataType;

    /**
    * 属性类型 1 - 基础属性  2 - 扩展属性 3 - 扩展配置
    */
    private Integer fieldClass;

    /**
    * 计量单位ID
    */
    private Integer fieldUnitID;

    /**
    * 父属性ID
    */
    private Integer parentID;

    /**
    * 创建类型
    */
    private Integer createType;

    /**
     * 创建类型
     */
    private Integer displayOrder;

    /**
    * 拓展字段
    */
    private String exValues;

    private static final long serialVersionUID = 1L;
}