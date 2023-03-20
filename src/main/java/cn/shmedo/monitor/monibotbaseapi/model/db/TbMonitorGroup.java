package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.util.Date;

/**
    * 监测组别和监测组表
    */
public class TbMonitorGroup {
    /**
    * ID
    */
    private Integer ID;

    /**
    * 父级ID
    */
    private Integer parentID;

    /**
    * 工程项目ID
    */
    private Integer projectID;

    /**
    * 分组名称
    */
    private String name;

    /**
    * 是否启用
1:启用，0停用
    */
    private Boolean enable;

    /**
    * 底图路径
    */
    private String imagePath;

    /**
    * 拓展字段
    */
    private String exValue;

    /**
    * 排序字段
    */
    private Integer displayOrder;

    /**
    * 创建时间
    */
    private Date creatTime;

    /**
    * 创建用户ID
    */
    private Integer createUserID;

    /**
    * 更新时间
    */
    private Date updateTime;

    /**
    * 更新用户ID
    */
    private Integer updateUserID;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getExValue() {
        return exValue;
    }

    public void setExValue(String exValue) {
        this.exValue = exValue;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Date getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(Date creatTime) {
        this.creatTime = creatTime;
    }

    public Integer getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(Integer createUserID) {
        this.createUserID = createUserID;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUserID() {
        return updateUserID;
    }

    public void setUpdateUserID(Integer updateUserID) {
        this.updateUserID = updateUserID;
    }
}