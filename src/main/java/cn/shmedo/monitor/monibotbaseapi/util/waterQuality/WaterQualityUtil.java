package cn.shmedo.monitor.monibotbaseapi.util.waterQuality;

import cn.shmedo.monitor.monibotbaseapi.model.enums.WaterQuality;

import java.util.Collections;
import java.util.List;

public class WaterQualityUtil {


    /**
     * @param v3 溶解氧计算规则
     * @return
     */
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

    /**
     * @param v1 ph计算规则
     * @return
     */
    // 定义v1的判定规则
    public static int getV1Category(double v1) {
        if (v1 >= 6 && v1 <= 9) {
            return WaterQuality.TYPE_1.getKey();
        } else {
            return WaterQuality.TYPE_6.getKey();
        }
    }

    /**
     * @param v6 高锰酸盐指数计算规则
     * @return
     */
    // 定义v6的判定规则
    public static int getV6Category(double v6) {
        if (v6 <= 2) {
            return WaterQuality.TYPE_1.getKey();
        } else if (v6 > 2 && v6 <= 4) {
            return WaterQuality.TYPE_2.getKey();
        } else if (v6 > 4 && v6 <= 6){
            return WaterQuality.TYPE_3.getKey();
        }else if (v6 > 6 && v6 <= 10){
            return WaterQuality.TYPE_4.getKey();
        }else if (v6 > 10 && v6 <= 15){
            return WaterQuality.TYPE_5.getKey();
        }else {
            return WaterQuality.TYPE_6.getKey();
        }
    }

    /**
     * @param v7 氨氮计算规则
     * @return
     */
    // 定义v7的判定规则
    public static int getV7Category(double v7) {
        if (v7 <= 0.15) {
            return WaterQuality.TYPE_1.getKey();
        } else if (v7 > 0.15 && v7 <= 0.5) {
            return WaterQuality.TYPE_2.getKey();
        } else if (v7 > 0.5 && v7 <= 1.0){
            return WaterQuality.TYPE_3.getKey();
        }else if (v7 > 1.0 && v7 <= 1.5){
            return WaterQuality.TYPE_4.getKey();
        }else if (v7 > 1.5 && v7 <= 2){
            return WaterQuality.TYPE_5.getKey();
        }else {
            return WaterQuality.TYPE_6.getKey();
        }
    }

    /**
     * @param v8 总磷计算规则
     * @return
     */
    // 定义v8的判定规则
    public static int getV8Category(double v8) {
        if (v8 <= 0.02) {
            return WaterQuality.TYPE_1.getKey();
        } else if (v8 > 0.02 && v8 <= 0.1) {
            return WaterQuality.TYPE_2.getKey();
        } else if (v8 > 0.1 && v8 <= 0.2){
            return WaterQuality.TYPE_3.getKey();
        }else if (v8 > 0.2 && v8 <= 0.3){
            return WaterQuality.TYPE_4.getKey();
        }else if (v8 > 0.3 && v8 <= 0.4){
            return WaterQuality.TYPE_5.getKey();
        }else {
            return WaterQuality.TYPE_6.getKey();
        }
    }

    // 获取判定结果的最大值
    public static int getMaxCategory(List<Integer> categories) {
        return Collections.max(categories);
    }

}
