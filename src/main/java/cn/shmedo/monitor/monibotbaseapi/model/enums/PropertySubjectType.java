package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @description: 属性主体类型枚举
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-16 10:28
 **/
public enum PropertySubjectType {
    Project(0, "工程"),
    OtherDevice(1, "其他设备"),
    Workflow(2, "工作流");

    private Integer type;
    private String typeStr;

    PropertySubjectType(Integer type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public Integer getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }
}
