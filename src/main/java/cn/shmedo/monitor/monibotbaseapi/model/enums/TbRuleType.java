package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-25 16:27
 */
public enum TbRuleType {
    WARN_RULE(1, "报警规则"), VIDEO_RULE(2, "视频规则"), TERMINAL_RULE(3, "智能终端规则");
    private final Integer key;
    private final String desc;

    TbRuleType(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public Integer getKey() {
        return key;
    }

    public String getDesc() {
        return desc;
    }

    public static TbRuleType getTbRuleType(final Integer key) {
        for (TbRuleType value : values()) {
            if (value.getKey().equals(key)) {
                return value;
            }
        }
        return null;
    }
}
