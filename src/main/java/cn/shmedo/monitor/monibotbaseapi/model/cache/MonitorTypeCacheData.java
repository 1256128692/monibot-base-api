package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.stream.Collectors;

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

    public static MonitorTypeCacheData valueof(TbMonitorType tbMonitorType, List<TbMonitorTypeField> list) {
        MonitorTypeCacheData monitorTypeCacheData = new MonitorTypeCacheData();
        monitorTypeCacheData.setID(tbMonitorType.getID());
        monitorTypeCacheData.setMonitorType(tbMonitorType.getMonitorType());
        monitorTypeCacheData.setTypeName(tbMonitorType.getTypeName());
        monitorTypeCacheData.setTypeAlias(tbMonitorType.getTypeAlias());
        monitorTypeCacheData.setDisplayOrder(tbMonitorType.getDisplayOrder());
        monitorTypeCacheData.setMultiSensor(tbMonitorType.getMultiSensor());
        monitorTypeCacheData.setCreateType(tbMonitorType.getCreateType().intValue());
        monitorTypeCacheData.setCompanyID(tbMonitorType.getCompanyID());
        monitorTypeCacheData.setExValues(tbMonitorType.getExValues());
        monitorTypeCacheData.setMonitortypeFieldList(
                list.stream().map(tbMonitorTypeField -> {
                    Field field = new Field();
                    field.setID(tbMonitorTypeField.getID());
                    field.setFieldToken(tbMonitorTypeField.getFieldToken());
                    field.setFieldName(tbMonitorTypeField.getFieldName());
                    field.setFieldDataType(tbMonitorTypeField.getFieldDataType());
                    field.setFieldClass(FieldClass.codeOf(tbMonitorTypeField.getFieldClass()));
                    field.setParentID(tbMonitorTypeField.getParentID());
                    field.setDisplayOrder(tbMonitorTypeField.getDisplayOrder());
                    field.setExValues(tbMonitorTypeField.getExValues());
                    field.setOperator(null);
                    field.setFieldUnitID(tbMonitorTypeField.getFieldUnitID());
                    return field;
                }).collect(Collectors.toList())
        );
        return monitorTypeCacheData;
    }

    public static TbMonitorType toNewVo(MonitorTypeCacheData monitorTypeCacheData) {
        TbMonitorType vo = new TbMonitorType();
        vo.setMonitorType(monitorTypeCacheData.getMonitorType());
        vo.setTypeName(monitorTypeCacheData.getTypeName());
        vo.setTypeAlias(monitorTypeCacheData.getTypeAlias());
        vo.setMultiSensor(monitorTypeCacheData.getMultiSensor());
        vo.setCompanyID(monitorTypeCacheData.getCompanyID());
        vo.setCreateType(monitorTypeCacheData.getCreateType().byteValue());
        vo.setDisplayOrder(monitorTypeCacheData.getDisplayOrder());
        vo.setExValues(monitorTypeCacheData.getExValues());
        return vo;
    }

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
        private String fieldDataType;

        /**
         * 字段类型
         */
        private FieldClass fieldClass;

        /**
         * 计算单位ID
         */
        private Integer fieldUnitID;

        /**
         * 父级ID
         */
        private Integer parentID;

        /**
         * 扩展字段
         */
        private String exValues;

        /**
         * 计算 公式/脚本
         */
        private String operator;

        /**
         * 排序字段
         */
        private Integer displayOrder;
    }
}

    
    