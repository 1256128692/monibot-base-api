package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
    * 模板脚本
    */
public class TbTemplateScript {
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
    * 计算脚本
    */
    private String script;

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

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}