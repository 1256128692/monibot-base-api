package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
 * 监测类型字段属性
 */
public class TbMonitorTypeField {
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

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public String getFieldToken() {
        return fieldToken;
    }

    public void setFieldToken(String fieldToken) {
        this.fieldToken = fieldToken;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public Integer getFieldClass() {
        return fieldClass;
    }

    public void setFieldClass(Integer fieldClass) {
        this.fieldClass = fieldClass;
    }

    public String getFieldDesc() {
        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {
        this.fieldDesc = fieldDesc;
    }

    public Integer getFieldUnitID() {
        return fieldUnitID;
    }

    public void setFieldUnitID(Integer fieldUnitID) {
        this.fieldUnitID = fieldUnitID;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    public String getExValues() {
        return exValues;
    }

    public void setExValues(String exValues) {
        this.exValues = exValues;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}