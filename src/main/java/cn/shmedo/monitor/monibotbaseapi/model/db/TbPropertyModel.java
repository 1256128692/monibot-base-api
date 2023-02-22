package cn.shmedo.monitor.monibotbaseapi.model.db;

public class TbPropertyModel {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 项目类型
    */
    private Integer projectType;

    /**
    * 创建类型 0预定义 1自定义
    */
    private Integer createType;

    /**
    * 模板名称
    */
    private String name;

    /**
    * 模板描述
    */
    private String desc;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}