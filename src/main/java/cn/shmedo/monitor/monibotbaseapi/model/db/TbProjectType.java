package cn.shmedo.monitor.monibotbaseapi.model.db;

public class TbProjectType {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 类型名称
    */
    private String typeName;

    /**
    * 主类型名称
    */
    private String mainType;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
    }
}