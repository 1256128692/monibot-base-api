package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
    * 监测类型模板
    */
public class TbMonitorTypeTemplate {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 模板名称
    */
    private String name;

    /**
    * 模板数据来源类型
1 -  单一物模型单一传感器
2 -  多个物联网传感器（同一物模型多个或者不同物模型多个）
3 -  物联网传感器+监测传感器
4 - 1个监测传感器
5 - 多个监测传感器
100 - API 推送    (无tb_template_data_source实体)
500 - 人工监测数据  (无tb_template_data_source实体)

    */
    private Integer dataSourceComposeType;

    /**
    * 监测类型模板分布式唯一ID
    */
    private String templateDataSourceID;

    /**
    * 监测类型
    */
    private Integer monitorType;

    /**
    * 计算类型 1 - 公式计算 2 - 脚本计算 3 - 外部HTTP计算 -1 不计算
    */
    private Integer calType;

    /**
    * 排序字段
    */
    private Integer displayOrder;

    /**
    * 存储模板的拓展信息。
比如：
对于 大于1个的物联网传感器，大于1个的监测传感器，物联网传感器+监测传感器组合的数据源，存储计算触发模式，限定数据时间边界等。
    */
    private String exValues;

    /**
    * 创建类型
    */
    private Byte createType;

    /**
    * 公司ID，预定义模板的公司ID为-1
    */
    private Integer companyID;

    /**
    * 1 - 是
0 - 否

对于单一物模型，单一物联网触感其的模板，是否作为默认模板使用。
如果是默认模板，来了该单一物联网传感器以后，自动创建监测传感器。
其他数据源组合类型都是0
    */
    private Boolean defaultTemplate;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDataSourceComposeType() {
        return dataSourceComposeType;
    }

    public void setDataSourceComposeType(Integer dataSourceComposeType) {
        this.dataSourceComposeType = dataSourceComposeType;
    }

    public String getTemplateDataSourceID() {
        return templateDataSourceID;
    }

    public void setTemplateDataSourceID(String templateDataSourceID) {
        this.templateDataSourceID = templateDataSourceID;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public Integer getCalType() {
        return calType;
    }

    public void setCalType(Integer calType) {
        this.calType = calType;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getExValues() {
        return exValues;
    }

    public void setExValues(String exValues) {
        this.exValues = exValues;
    }

    public Byte getCreateType() {
        return createType;
    }

    public void setCreateType(Byte createType) {
        this.createType = createType;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Boolean getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
}