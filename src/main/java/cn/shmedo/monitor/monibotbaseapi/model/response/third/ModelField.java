package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-04 15:42
 **/
public class ModelField {
    private Integer id;
    private String fieldToken;
    private String fieldName;
    private String fieldDesc;
    private Integer fieldUnitID;
    private String engUnit;
    private String FieldDataType;
    private List<ModelField> childFieldList;

    private Integer modelID;
    private String modelToken;
    private Integer parentID;

    private Integer FieldOrder;
    private String FieldJsonPath;

    private String fieldStatisticsType;

    private String fieldStatisticsTypeString;

    private String exValues;

    public String getModelToken() {
        return modelToken;
    }

    public void setModelToken(String modelToken) {
        this.modelToken = modelToken;
    }

    public Integer getFieldOrder() {
        return FieldOrder;
    }

    public void setFieldOrder(Integer fieldOrder) {
        FieldOrder = fieldOrder;
    }

    public String getFieldJsonPath() {
        return FieldJsonPath;
    }

    public void setFieldJsonPath(String fieldJsonPath) {
        FieldJsonPath = fieldJsonPath;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public Integer getModelID() {
        return modelID;
    }

    public void setModelID(Integer modelID) {
        this.modelID = modelID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getEngUnit() {
        return engUnit;
    }

    public void setEngUnit(String engUnit) {
        this.engUnit = engUnit;
    }

    public String getFieldDataType() {
        return FieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        FieldDataType = fieldDataType;
    }

    public List<ModelField> getChildFieldList() {
        return childFieldList;
    }

    public void setChildFieldList(List<ModelField> childFieldList) {

        this.childFieldList = childFieldList;
    }

    public String getFieldStatisticsType() {
        return fieldStatisticsType;
    }

    public void setFieldStatisticsType(String fieldStatisticsType) {
        this.fieldStatisticsType = fieldStatisticsType;
    }

    public String getFieldStatisticsTypeString() {
        return fieldStatisticsTypeString;
    }

    public void setFieldStatisticsTypeString(String fieldStatisticsTypeString) {
        this.fieldStatisticsTypeString = fieldStatisticsTypeString;
    }

    public String getExValues() {
        return exValues;
    }

    public void setExValues(String exValues) {
        this.exValues = exValues;
    }
}
