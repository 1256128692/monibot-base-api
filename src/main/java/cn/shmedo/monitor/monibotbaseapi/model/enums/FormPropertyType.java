package cn.shmedo.monitor.monibotbaseapi.model.enums;

/**
 * @Author wuxl
 * @Date 2023/10/19 11:56
 * @PackageName:cn.shmedo.workflowengine.infrastructure.base.enumeration
 * @ClassName: WorkFlowNodeStatus
 * @Description: TODO
 * @Version 1.0
 */
public enum FormPropertyType {
    /**
     * 数值
     **/
    NUMBER(1, "数值"),
    /**
     * 字符串
     **/
    MULTI_LINE_TEXT(2, "字符串"),
    /**
     * 枚举
     **/
    ENUM(3, "枚举"),
    /**
     * 日期时间
     **/
    DATETIME(4, "日期时间"),
    /**
     * 单行文本
     **/
    SINGLE_LINE_TEXT(5, "单行文本"),
    /**
     * 图片上传
     **/
    PICTURE(6, "图片上传"),
    /**
     * 文件上传
     **/
    FILE(7, "文件上传"),
    /**
     * 地图选点
     **/
    MAP(8, "地图选点"),
    /**
     * 实时定位
     **/
    POSITION(9, "实时定位");

    private Integer code;

    private String name;

    FormPropertyType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
