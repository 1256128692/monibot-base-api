package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 15:39
 **/
public enum CreateType {
    Predefined((byte)0, "预定义"),
    Customized((byte)1, "自定义");

    private Byte type;
    private String typeStr;

    CreateType(Byte type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public Byte getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }
}
