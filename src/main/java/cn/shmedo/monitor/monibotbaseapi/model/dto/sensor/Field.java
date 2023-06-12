package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.cache.FormulaCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Field extends TbMonitorTypeField {
    /**
     * 字段值
     */
    private String value;

    /**
     * 显示公式
     */
    private String displayFormula;

    /**
     * 计算公式
     */
    private String formula;


    public static Field valueOf(TbMonitorTypeField typeField) {
        Field field = new Field();
        field.setID(typeField.getID());
        field.setMonitorType(typeField.getMonitorType());
        field.setFieldToken(typeField.getFieldToken());
        field.setFieldName(typeField.getFieldName());
        field.setFieldDataType(typeField.getFieldDataType());
        field.setFieldClass(typeField.getFieldClass());
        field.setFieldDesc(typeField.getFieldDesc());
        field.setFieldUnitID(typeField.getFieldUnitID());
        field.setDisplayOrder(typeField.getDisplayOrder());
        field.setParentID(typeField.getParentID());
        field.setCreateType(typeField.getCreateType());
        field.setExValues(typeField.getExValues());
        return field;
    }

    public static Field valueOf(MonitorTypeCacheData.Field e, FormulaCacheData formulaCacheData) {
        if (formulaCacheData == null) {
            return null;
        }
        Field field = new Field();
        field.setID(e.getID());
        field.setFieldToken(e.getFieldToken());
        field.setFieldName(e.getFieldName());
        field.setFieldUnitID(e.getFieldUnitID());
        field.setExValues(e.getExValues());
        field.setFieldClass(e.getFieldClass().getCode());
        field.setParentID(e.getParentID());
        field.setFieldDataType(e.getFieldDataType());
        field.setFormula(formulaCacheData.getFormula());
        field.setDisplayFormula(formulaCacheData.getDisplayFormula());
        field.setDisplayOrder(e.getDisplayOrder());
        return field;
    }
}