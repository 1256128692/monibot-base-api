package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 16:53
 **/
public enum AssetUnit {
    Piece((byte) 1, "件"),
    Unit((byte) 2, "台"),
    Item((byte) 3, "个"),
    Set((byte) 4, "组"),
    MG((byte) 5, "毫克"),
    G((byte) 6, "克"),
    KG((byte) 7, "千克"),
    Ton((byte) 8, "吨");

    private Byte code;
    private String desc;

    AssetUnit(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getStrByCode(Byte assetUnit) {
        if (assetUnit == null) return null;
        return switch (assetUnit) {
            case 1 -> "件";
            case 2 -> "台";
            case 3 -> "个";
            case 4 -> "组";
            case 5 -> "毫克";
            case 6 -> "克";
            case 7 -> "千克";
            case 8 -> "吨";
            default -> null;
        };
    }

    public Byte getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
