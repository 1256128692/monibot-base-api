package cn.shmedo.monitor.monibotbaseapi.model.db;

public class TbProjectProperty {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 项目编号
    */
    private Integer projectID;

    /**
    * 属性ID
    */
    private Integer propertyID;

    /**
    * 属性值，枚举数值为json数组
    */
    private String value;

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

    public Integer getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(Integer propertyID) {
        this.propertyID = propertyID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}