package cn.shmedo.monitor.monibotbaseapi.model.db;

public class TbProperty {
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 项目类型
     */
    private Byte projectType;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性类型:1.数值,2.字符串,3.枚举,4.日期时间
     */
    private Byte type;

    /**
     * 属性单位
     */
    private String unit;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否多选,限定枚举
     */
    private Boolean multiSelect;

    /**
     * 枚举字段
     */
    private String enumField;

    /**
     * 创建类型 0-预定义 1-自定义
     */
    private Byte createType;

    /**
     * 结构名称
     */
    private String className;

    /**
     * 排序字段
     */
    private Integer displayOrder;

    /**
     * 模板ID
     */
    private Integer modelID;

    /**
     * 拓展信息
     */
    private String exValue;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Byte getProjectType() {
        return projectType;
    }

    public void setProjectType(Byte projectType) {
        this.projectType = projectType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public String getEnumField() {
        return enumField;
    }

    public void setEnumField(String enumField) {
        this.enumField = enumField;
    }

    public Byte getCreateType() {
        return createType;
    }

    public void setCreateType(Byte createType) {
        this.createType = createType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getModelID() {
        return modelID;
    }

    public void setModelID(Integer modelID) {
        this.modelID = modelID;
    }

    public String getExValue() {
        return exValue;
    }

    public void setExValue(String exValue) {
        this.exValue = exValue;
    }
}