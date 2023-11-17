package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * 传感器统计类型
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-11-12 17:40
 **/
public enum SensorStatisticsType {

    MEAN(2, "mean", "平均值", "avg"),
    SUM(3, "sum", "求和", "sum"),
    DIFF(4, "diff", "阶段变化", "diff");


    private Integer code;
    private String stat;
    private String desc;
    private String tableSuffix;


    SensorStatisticsType(Integer code, String stat, String desc, String tableSuffix) {
        this.code = code;
        this.stat = stat;
        this.desc = desc;
        this.tableSuffix = tableSuffix;
    }

    public static SensorStatisticsType getByCode(Integer integer) {
        for (SensorStatisticsType value : SensorStatisticsType.values()) {
            if (value.getCode().equals(integer)) {
                return value;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public String getStat() {
        return stat;
    }

    public String getDesc() {
        return desc;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }
}
