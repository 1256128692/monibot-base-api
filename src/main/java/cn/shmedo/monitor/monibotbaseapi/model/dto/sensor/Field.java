package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

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
}