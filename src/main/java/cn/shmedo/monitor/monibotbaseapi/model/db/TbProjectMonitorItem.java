package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
    * 项目和监测项目关系表
    */
public class TbProjectMonitorItem {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 工程项目ID
    */
    private Integer projectID;

    /**
    * 监测项目ID
    */
    private Integer monitorItemID;

    /**
    * 是否启用
    */
    private Boolean enable;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getMonitorItemID() {
        return monitorItemID;
    }

    public void setMonitorItemID(Integer monitorItemID) {
        this.monitorItemID = monitorItemID;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}