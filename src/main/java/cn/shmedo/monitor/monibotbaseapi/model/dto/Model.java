package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 物模型抽象
 *
 * @author Chengfs on 2023/3/22
 */
@Data
public class Model implements Serializable {

    private Integer ID;

    /**
     * 物模型标识
     */
    private String modelToken;

    /**
     * 物模型名称
     */
    private String modelName;

    /**
     * 物模型类型 0–标准物模型1–产品自定义物模型
     */
    private Integer modelType;

    /**
     * 物模型字段列表
     */
    private List<Field> modelFieldList;

    @Serial
    private static final long serialVersionUID = 1L;

    public void setModelFieldList(List<Field> modelFieldList) {
        this.modelFieldList = CollUtil.isEmpty(modelFieldList) ? Collections.emptyList() : modelFieldList;
    }

    @Data
    public static class Field implements Serializable {

        private Integer id;

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 字段单位
         */
        private Integer fieldUnitID;

        /**
         * 物模型编号
         */
        private Integer modelID;

        /**
         * 字段标识(只能是大小写英文字符和数字，但是不能以数字开始)
         */
        private String fieldToken;

        /**
         * 字段类型
         */
        private String fieldDataType;

        /**
         * 父级字段
         */
        private Integer parentID;

        /**
         * 字段顺序。从1开始，不可跳跃。对应字符串传输数据的排列顺序。
         */
        private Integer fieldOrder;

        /**
         * 字段在json中的提取路径，为空则默认为$.fieldToken
         */
        private String fieldJsonPath;

        /**
         * 存储字段扩展JSON属性
         */
        private String exValues;

        private static final long serialVersionUID = 1L;
    }
}

    
    