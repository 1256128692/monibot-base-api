package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Data;

/**
 * @Author wuxl
 * @Date 2023/10/12 9:36
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.enums
 * @ClassName: DocumentSubjectType
 * @Description: TODO
 * @Version 1.0
 */
public enum DocumentSubjectType {
    PROJECT(1),
    OTHER_DEVICE(2);

    DocumentSubjectType(Integer code) {
        this.code = code;
    }

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
