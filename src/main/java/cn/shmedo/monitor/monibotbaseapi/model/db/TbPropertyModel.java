package cn.shmedo.monitor.monibotbaseapi.model.db;

public class TbPropertyModel {
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 项目类型
     */
    private Byte projectType;

    /**
     * 创建类型 0预定义 1自定义
     */
    private Byte createType;

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

    public Byte getProjectType() {
        return projectType;
    }

    public void setProjectType(Byte projectType) {
        this.projectType = projectType;
    }

    public Byte getCreateType() {
        return createType;
    }

    public void setCreateType(Byte createType) {
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