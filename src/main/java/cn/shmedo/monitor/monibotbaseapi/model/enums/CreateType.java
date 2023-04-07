package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * （模板）创建类型的枚举
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-24 15:39
 **/
public enum CreateType {
    PREDEFINED((byte)0, "预定义"),
    CUSTOMIZED((byte)1, "自定义");

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

    public static boolean isValid(Byte type){
        if (PREDEFINED.type.equals(type) || CUSTOMIZED.type.equals(type)){
            return true;
        }
        return false;
    }
}
