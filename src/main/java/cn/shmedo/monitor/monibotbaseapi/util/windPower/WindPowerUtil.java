package cn.shmedo.monitor.monibotbaseapi.util.windPower;

public class WindPowerUtil {


    /**
     * @param v1 风力计算规则
     * @return
     */
    public static int getV1Category(double v1) {
        if (v1 >= 0.0 && v1 <= 0.2) {
            // 0级风,级越大,风力越大
            return 0;
        } else if (v1 >= 0.3 && v1 < 1.6) {
            return 1;
        } else if (v1 >= 1.6 && v1 < 3.4){
            return 2;
        }else if (v1 >= 3.4 && v1 < 5.5){
            return 3;
        }else if (v1 >= 5.5 && v1 < 8.0){
            return 4;
        }else if (v1 >= 8.0 && v1 < 10.8) {
            return 5;
        } else if (v1 >= 10.8 && v1 < 13.9){
            return 6;
        }else if (v1 >= 13.9 && v1 < 17.2){
            return 7;
        }else if (v1 >= 17.2 && v1 < 20.8) {
            return 8;
        } else if (v1 >= 20.8 && v1 < 24.5){
            return 9;
        }else if (v1 >= 24.5 && v1 < 28.5){
            return 10;
        }else if (v1 >= 28.5 && v1 < 32.7){
            return 11;
        }else if (v1 >= 32.7 && v1 < 36.9) {
            return 12;
        } else {
            // 危机风力
            return 13;
        }
    }
}
