package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @Author wuxl
 * @Date 2023/9/19 18:12
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.enums
 * @ClassName: DocumentFileSubjectType
 * @Description: TODO
 * @Version 1.0
 */
public enum DocumentFileSubjectType {
    PROJECT(1, "工程项目"),
    OTHER_DEVICE(2, "其它设备");

    DocumentFileSubjectType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
