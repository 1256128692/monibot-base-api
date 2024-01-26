package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * 项目所属平台的类型的枚举
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 13:49
 **/
@AllArgsConstructor
@Getter
public enum PlatformType {
    WATER((byte) 1, "水文水利", "山东省小水库数据监视系统、株树桥水库大坝安全监测自动化系统、大化瑶族自治县水利工程站、云南丽江白水河泵站取水大坝项目".split("、"), "SL651协议、RS485协议、米度物联网协议、米度文本协议".split("、")),
    MINE((byte) 2, "矿山", "22-126中黄金内蒙古尾矿库805-810米坝面及排土场监测项目、鹤庆北衙矿业有限公司安全在线监测平台、中国黄金集团湖南鑫瑞矿业有限公司合仁坪尾矿库安全在线监测系统".split("、"), "RS485协议、米度物联网协议、米度文本协议".split("、")),
    LAND((byte) 3, "国土地灾", "贵州地质灾害防治指挥平台、四川成都山洪灾害在线监测预警、云南昭通大关悦乐姜家坪子不稳定斜坡监测".split("、"), "RS232协议、CAN协议、米度物联网协议、米度文本协议".split("、")),
    INFRASTRUCTURE((byte) 4, "基建", "金桥出口加工区T29号地块通用房项目周边环境及基坑自动化监测、上海隧道城建设计临港联合实验室基坑自动化监测实验平台、珠海隧道工程TJ2标段基坑自动化监测".split("、"), "GB/T28181协议、RS485协议、米度物联网协议、米度文本协议".split("、")),
    MDNET((byte) 5, "MD_Net3.0", null, null);

    private final Byte type;
    private final String typeStr;
    private final String[] platforms;
    private final String[] protocols;

    public static final Set<Byte> defaultTypes = Set.of((byte) 1,(byte) 2,(byte) 3,(byte) 4);

    public static boolean validate(Byte type) {
        return Arrays.stream(PlatformType.values()).anyMatch(item -> item.getType().equals(type));
    }

    /**
     * 根据类型获取枚举
     *
     * @param type 类型
     * @return 枚举
     */
    public static PlatformType getPlatformType(Byte type) {
        return Arrays.stream(PlatformType.values())
                .filter(item -> item.getType().equals(type)).findFirst().orElse(null);
    }

    /**
     * 根据类型描述获取枚举
     *
     * @param typeStr 类型描述
     * @return 枚举
     */
    public static PlatformType getByTypeStr(String typeStr) {
        return Arrays.stream(PlatformType.values())
                .filter(item -> item.getTypeStr().equals(typeStr)).findFirst().orElse(null);
    }

    /**
     * 获取数据共享平台列表
     * @param types 平台类型
     * @return
     */
    public static Set<String> getPlatformNames(Set<Byte> types){
        Set<String> set = new HashSet<>();
        for (PlatformType value : PlatformType.values()) {
            if(types.contains(value.getType())){
                set.addAll(new HashSet<>(Arrays.asList(value.getPlatforms())));
            }
        }
        return set;
    }

    /**
     * 获取协议支持列表
     * @param types 平台类型
     * @return
     */
    public static Set<String> getProtocols(Set<Byte> types){
        Set<String> set = new HashSet<>();
        for (PlatformType value : PlatformType.values()) {
            if(types.contains(value.getType())){
                set.addAll(new HashSet<>(Arrays.asList(value.getProtocols())));
            }
        }
        return set;
    }
}
