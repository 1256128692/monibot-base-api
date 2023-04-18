package cn.shmedo.monitor.monibotbaseapi.model.db;

import lombok.Builder;

/**
    * 监测组别和监测项目关联表
    */
@Builder(toBuilder = true)
public class TbMonitorGroupItem {
    /**
    * ID
    */
    private Integer ID;

    /**
    * 监测组别ID
    */
    private Integer monitorGroupID;

    /**
    * 监测项目ID
    */
    private Integer monitorItemID;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getMonitorGroupID() {
        return monitorGroupID;
    }

    public void setMonitorGroupID(Integer monitorGroupID) {
        this.monitorGroupID = monitorGroupID;
    }

    public Integer getMonitorItemID() {
        return monitorItemID;
    }

    public void setMonitorItemID(Integer monitorItemID) {
        this.monitorItemID = monitorItemID;
    }
}