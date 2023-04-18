package cn.shmedo.monitor.monibotbaseapi.model.db;

import lombok.Builder;

/**
    * 监测组和监测点关联表
    */
@Builder(toBuilder = true)
public class TbMonitorGroupPoint {
    /**
    * ID
    */
    private Integer ID;

    /**
    * 监测组ID
    */
    private Integer monitorGroupID;

    /**
    * 监测点ID
    */
    private Integer monitorPointID;

    /**
    * 监测点底图位置
    */
    private String imageLocation;

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

    public Integer getMonitorPointID() {
        return monitorPointID;
    }

    public void setMonitorPointID(Integer monitorPointID) {
        this.monitorPointID = monitorPointID;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }
}