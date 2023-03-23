package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
 * 模板公式
 */
public class TbTemplateFormula {
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 字段ID
     */
    private Integer fieldID;

    /**
     * 计算排序字段
     */
    private Integer fieldCalOrder;

    /**
     * 显示公式表达式
     */
    private String displayFormula;

    /**
     * 计算公式表达式
     */
    private String formula;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getTemplateID() {
        return templateID;
    }

    public void setTemplateID(Integer templateID) {
        this.templateID = templateID;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public Integer getFieldID() {
        return fieldID;
    }

    public void setFieldID(Integer fieldID) {
        this.fieldID = fieldID;
    }

    public Integer getFieldCalOrder() {
        return fieldCalOrder;
    }

    public void setFieldCalOrder(Integer fieldCalOrder) {
        this.fieldCalOrder = fieldCalOrder;
    }

    public String getDisplayFormula() {
        return displayFormula;
    }

    public void setDisplayFormula(String displayFormula) {
        this.displayFormula = displayFormula;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}