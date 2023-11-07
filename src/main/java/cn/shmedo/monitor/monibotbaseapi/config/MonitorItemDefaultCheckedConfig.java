package cn.shmedo.monitor.monibotbaseapi.config;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 监测项目默认选中配置，与项目类型关联，用于创建项目时候页面勾选
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-11-07 11:43
 **/
@Data
public class MonitorItemDefaultCheckedConfig {
    /**
     * key 为项目类型，value为选中的监测项目列表
     */
    private static Map<Integer, List<String>> defaultCheckedMap;

    static {
        defaultCheckedMap = new HashMap<>();
        defaultCheckedMap.put(9, List.of("地下水水位", "地下水水质"));
        defaultCheckedMap.put(11, List.of("水闸", "视频图像"));
        defaultCheckedMap.put(12, List.of("泵站", "视频图像"));
        defaultCheckedMap.put(10, List.of("渠道水位", "渠道水质", "流量", "降雨量", "视频图像"));
        defaultCheckedMap.put(13, List.of("土壤PH", "土壤电导率", "土壤含盐量", "温度", "湿度", "风象", "气压", "太阳辐射", "降雨量", "视频图像"));
        defaultCheckedMap.put(5, List.of("堆积坝坡比", "坝体表面水平形变", "坝体表面竖向形变", "滑坡体表面水平位移", "滑坡体表面竖向位移",
                "浸润线", "库水位", "干滩", "库区监控"));
        defaultCheckedMap.put(1, List.of("表面三维形变(相对)", "水利水位", "流量", "降雨量", "温度", "湿度"));
        defaultCheckedMap.put(2, List.of("水利水位"));
        defaultCheckedMap.put(6, List.of("围护墙（边坡）顶部水平位移", "围护墙（边坡）顶部竖向位移", "围护墙深层水平位移", "支撑内力"
                , "立柱竖向位移", "基坑外地下水水位", "地表竖向位移", "邻近建（构）筑物竖向位移", "邻近建（构）筑物裂缝、地表裂缝", "邻近地下管线竖向位移"));

    }

    public static boolean idDefault(Integer projectTypeID, String name) {
        if (projectTypeID == null) {
            return false;
        }
        if (defaultCheckedMap.containsKey(projectTypeID)) {
            return defaultCheckedMap.get(projectTypeID).contains(name);
        }
        return false;
    }
}
