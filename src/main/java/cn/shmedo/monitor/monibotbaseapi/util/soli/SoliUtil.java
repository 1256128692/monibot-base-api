package cn.shmedo.monitor.monibotbaseapi.util.soli;

public class SoliUtil {

    /**
     * @param v1 土壤计算
     * @return
     */
    public static String getV1Category(double v1) {
        if (v1 < 4.5) {
            // 0级风,级越大,风力越大
            return "极强酸性";
        } else if (v1 >= 4.5 && v1 < 5.5) {
            return "强酸性";
        } else if (v1 >= 5.5 && v1 < 6.5) {
            return "酸性";
        } else if (v1 >= 6.5 && v1 < 7.5) {
            return "中性";
        } else if (v1 >= 7.5 && v1 < 8.5) {
            return "碱性";
        } else if (v1 >= 8.5 && v1 < 9.5) {
            return "强碱性";
        } else {
            return "极强碱性";
        }
    }


    /**
     * @param v1 土壤盐分电导率
     * @return
     */
    public static String getV2Category(double v1) {
        if (v1 < 0.2) {
            // 0级风,级越大,风力越大
            return "低盐土";
        } else if (v1 >= 0.2 && v1 < 0.5) {
            return "轻度盐土";
        } else if (v1 >= 0.5 && v1 < 2) {
            return "中度盐土";
        } else if (v1 >= 2 && v1 < 4) {
            return "重度盐土";
        } else {
            return "极重度盐土";
        }
    }
}
