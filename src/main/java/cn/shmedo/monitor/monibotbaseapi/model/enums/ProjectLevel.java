package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-25 14:12
 **/
public enum ProjectLevel {
    Son((byte) -1, "子工程"),
    Unallocated((byte) 0, "未分配非子工程"),
    One((byte) 1, "一级工程"),
    Two((byte) 2, "二级工程");

    private Byte level;
    private String description;

    ProjectLevel(Byte level, String description) {
        this.level = level;
        this.description = description;
    }

    public Byte getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }
}
