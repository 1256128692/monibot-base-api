package cn.shmedo.monitor.monibotbaseapi.util.rainfall;

import cn.shmedo.monitor.monibotbaseapi.model.enums.WaterQuality;

public class RainfallUtil {

    public static int getV3Category(double v3) {
        if (v3 >= 7.5) {
            return WaterQuality.TYPE_1.getKey();
        } else if (v3 >= 6 && v3 < 7.5) {
            return WaterQuality.TYPE_2.getKey();
        } else if (v3 >= 5 && v3 < 6){
            return WaterQuality.TYPE_3.getKey();
        }else if (v3 >= 3 && v3 < 5){
            return WaterQuality.TYPE_4.getKey();
        }else if (v3 >= 2 && v3 < 3){
            return WaterQuality.TYPE_5.getKey();
        }else {
            return WaterQuality.TYPE_6.getKey();
        }
    }
}
