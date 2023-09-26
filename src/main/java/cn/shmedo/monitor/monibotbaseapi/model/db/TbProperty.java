package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_property")
public class TbProperty {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 分组ID
     */
    @TableField("GroupID")
    private Integer groupID;

    /**
     * 属性名称
     */
    @TableField("`Name`")
    private String name;

    /**
     * 属性类型:1.数值,2.字符串,3.枚举,4.日期时间
     */
    @TableField("Type")
    private Byte type;

    /**
     * 属性单位
     */
    @TableField("Unit")
    private String unit;

    /**
     * 是否必填
     */
    @TableField("Required")
    private Boolean required;

    /**
     * 是否多选,限定枚举
     */
    @TableField("MultiSelect")
    private Boolean multiSelect;

    /**
     * 枚举字段
     */
    @TableField("EnumField")
    private String enumField;

    /**
     * 创建类型 0-预定义 1-自定义
     */
    @TableField("CreateType")
    private Byte createType;

    /**
     * 结构名称
     */
    @TableField("ClassName")
    private String className;

    /**
     * 排序字段
     */
    @TableField("DisplayOrder")
    private Integer displayOrder;

    /**
     * 模板ID
     */
    @TableField("ModelID")
    private Integer modelID;

    /**
     * 拓展信息
     */
    @TableField("ExValue")
    private String exValue;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

//    public Byte getProjectType() {
//        return projectType;
//    }
//
//    public void setProjectType(Byte projectType) {
//        this.projectType = projectType;
//    }


    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
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

    @Override
    public String toString() {
        return "TbProperty{" +
                "ID=" + ID +
//                ", projectType=" + projectType +
                ", groupID='" + groupID + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", unit='" + unit + '\'' +
                ", required=" + required +
                ", multiSelect=" + multiSelect +
                ", enumField='" + enumField + '\'' +
                ", createType=" + createType +
                ", className='" + className + '\'' +
                ", displayOrder=" + displayOrder +
                ", modelID=" + modelID +
                ", exValue='" + exValue + '\'' +
                '}';
    }
}