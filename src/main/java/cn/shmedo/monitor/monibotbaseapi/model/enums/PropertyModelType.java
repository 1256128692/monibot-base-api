package cn.shmedo.monitor.monibotbaseapi.model.enums;

import cn.hutool.core.collection.CollectionUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author wuxl
 * @Date 2023/9/21 15:49
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.enums
 * @ClassName: PropertyModelType
 * @Description: 属性模板类型
 * @Version 1.0
 */
public enum PropertyModelType {
    /** 非工程项目 **/
    UN_BASE_PROJECT(-1, "非工程项目"),
    /** 工程项目 **/
    BASE_PROJECT(0, "工程项目"),
    /** 其他设备 **/
    DEVICE(1, "其他设备"),
    /** 工作流 **/
    WORK_FLOW(2, "工作流");

    private Integer code;

    private String desc;

    private static Set<Integer> PROPERTY_MODEL_TYPE_CODE_SET = null;

    public static Set<Integer> getModelTypeValues(){
        if(CollectionUtil.isNotEmpty(PROPERTY_MODEL_TYPE_CODE_SET)){
            return PROPERTY_MODEL_TYPE_CODE_SET;
        }
        PROPERTY_MODEL_TYPE_CODE_SET = new HashSet<>();
        for(PropertyModelType propertyModelType : PropertyModelType.values()){
            PROPERTY_MODEL_TYPE_CODE_SET.add(propertyModelType.code);
        }
        return PROPERTY_MODEL_TYPE_CODE_SET;
    }

    PropertyModelType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

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
