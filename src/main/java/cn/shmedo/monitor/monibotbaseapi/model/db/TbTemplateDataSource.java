package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
    * 模板数据来源
    */
public class TbTemplateDataSource {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 监测类型模板分布式唯一ID
    */
    private String templateDataSourceID;

    /**
    * 1 - 物联网传感器 2 - 监测传感器
    */
    private Integer dataSourceType;

    /**
    * 模板数据源标识，使用代号。
在公式中可以使用代号引用对应的数据源。
物联网传感器代号：203_a,203_b,999_a
监测传感器代号：22_a
    */
    private String templateDataSourceToken;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTemplateDataSourceID() {
        return templateDataSourceID;
    }

    public void setTemplateDataSourceID(String templateDataSourceID) {
        this.templateDataSourceID = templateDataSourceID;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getTemplateDataSourceToken() {
        return templateDataSourceToken;
    }

    public void setTemplateDataSourceToken(String templateDataSourceToken) {
        this.templateDataSourceToken = templateDataSourceToken;
    }
}