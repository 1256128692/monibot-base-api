package cn.shmedo.monitor.monibotbaseapi.model.db;

/**
    * 标签关系表，多对多关系
    */
public class TbTagRelation {
    private Integer ID;

    /**
    * 标签表ID
    */
    private Integer tagID;

    /**
    * 项目ID
    */
    private Integer projectID;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getTagID() {
        return tagID;
    }

    public void setTagID(Integer tagID) {
        this.tagID = tagID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }
}