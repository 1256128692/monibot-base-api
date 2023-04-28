package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-28 13:36
 */
public enum SensorStatusDesc {
    NO_DATA(-1, null, "无数据"),
    NORMAL(0, null, "正常"),
    WARM_LEVEL1(1, 1, "一级报警"),
    WARM_LEVEL2(2, 2, "二级报警"),
    WARM_LEVEL3(3, 3, "三级报警"),
    WARM_LEVEL4(4, 4, "四级报警");

    private final Integer status;
    private final Integer warnLevel;
    private final String desc;

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getWarnLevel() {
        return warnLevel;
    }

    SensorStatusDesc(Integer status, Integer warnLevel, String desc) {
        this.status = status;
        this.warnLevel = warnLevel;
        this.desc = desc;
    }

    //    private interface SensorStatus {
    //        Integer NO_DATA = -1;
    //        Integer NORMAL = 0;
    //        Integer WARM_LEVEL1 = 1;
    //        Integer WARM_LEVEL2 = 2;
    //        Integer WARM_LEVEL3 = 3;
    //        Integer WARM_LEVEL4 = 4;
    //    }
}
