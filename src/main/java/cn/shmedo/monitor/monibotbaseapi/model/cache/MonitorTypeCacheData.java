package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.iot.entity.api.iot.base.FieldType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorTypeFieldClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 监测类型
 *
 * @author Chengfs on 2023/3/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeCacheData extends AbstractCacheData {

    /**
     * 监测类型，小于10000是预定义预留
     */
    private Integer monitorType;

    /**
     * 监测类型名称
     */
    private String typeName;

    /**
     * 监测类型别名
     */
    private String typeAlias;

    /**
     * 排序字段
     */
    private Integer displayOrder;

    /**
     * 是否是多传感器
     */
    private Boolean multiSensor;

    /**
     * 创建类型
     */
    private Integer createType;

    /**
     * 公司ID，预定义为 -1
     */
    private Integer companyID;

    /**
     * 扩展字段
     */
    private String exValues;

    /**
     * 监测类型字段列表
     */
    private List<Field> monitortypeFieldList;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Field extends AbstractCacheData {

        /**
         * 字段标识
         */
        private String fieldToken;

        /**
         * 字段名称
         */
        private String fieldName;

        /**
         * 字段值类型
         */
        private FieldType fieldDataType;

        /**
         * 字段类型
         */
        private MonitorTypeFieldClass fieldClass;

        /**
         * 父级ID
         */
        private Integer parentID;

        /**
         * 计算排序
         */
        private Integer fieldCalOrder;

        /**
         * 扩展字段
         */
        private String exValues;

        /**
         * 计算 公式/脚本
         */
        private String operator;

    }
}

    
    