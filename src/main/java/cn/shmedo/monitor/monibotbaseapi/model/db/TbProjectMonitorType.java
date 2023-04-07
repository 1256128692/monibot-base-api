package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
    * 项目和监测类型关系表
    */
public class TbProjectMonitorType {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 工程项目ID
    */
    private Integer projectID;

    /**
    * 监测类型
    */
    private Integer monitorType;

    /**
    * 是否开启
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

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}