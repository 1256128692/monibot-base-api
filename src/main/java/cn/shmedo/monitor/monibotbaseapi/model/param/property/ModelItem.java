package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 13:34
 **/
@ToString
public class ModelItem {
    @JsonProperty("ID")
    private Integer ID;
    private Integer groupID;
    private Integer ModelID;
    @NotBlank
    @Size(max = 20)
    private String name;
    @NotNull
    private Byte type;
    private String unit;
    @NotNull
    private Boolean required;
    private String enumField;
    private Boolean multiSelect;
    private String className;
    private Integer createType;
    private Integer displayOrder;
    private String exValue;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Integer getModelID() {
        return ModelID;
    }

    public void setModelID(Integer modelID) {
        ModelID = modelID;
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

    public String getEnumField() {
        return enumField;
    }

    public void setEnumField(String enumField) {
        this.enumField = enumField;
    }

    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getExValue() {
        return exValue;
    }

    public void setExValue(String exValue) {
        this.exValue = exValue;
    }

}
