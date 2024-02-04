package cn.shmedo.monitor.monibotbaseapi.model.response.monitorType;


import lombok.Data;

@Data
public class MonitorTypeFieldWithUnitInfo {


    /**
     * 主键
     */
    private Integer ID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 属性标识
     */
    private String fieldToken;

    /**
     * 属性名称
     */
    private String fieldName;

    /**
     * 字段类型，String,Double,Long
     */
    private String fieldDataType;

    /**
     * 属性类型 1 - 基础属性  2 - 扩展属性 3 - 扩展配置
     */
    private Integer fieldClass;

    /**
     * 属性描述
     */
    private String fieldDesc;

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
     * 拓展字段
     */
    private String exValues;

    /**
     * 排序字段
     */
    private Integer displayOrder;


    /**
     * 计量单位名称
     */
    private String fieldUnitName;
}
