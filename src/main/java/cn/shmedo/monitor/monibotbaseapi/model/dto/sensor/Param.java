package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.util.FormulaUtil;
import lombok.Data;

@Data
public class Param {
    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段值
     */
    private String value;

    /**
     * 字段单位
     */
    private Integer unit;

    /**
     * 字段原始标识
     */
    private String origin;

    /**
     * 字段标识
     */
    private String token;

    /**
     * 字段数据类型
     */
    private String dataType;

    /**
     * 字段扩展值
     */
    private String exValues;

    /**
     * 字段类型
     */
    private String type;

    public static Param valueOf(Model.Field field, String origin, FormulaUtil.DataType type) {
        Param param = new Param();
        param.setName(field.getFieldName());
        param.setUnit(field.getFieldUnitID());
        param.setOrigin(origin);
        param.setToken(field.getFieldToken());
        param.setDataType(field.getFieldDataType());
        param.setExValues(field.getExValues());
        param.setType(type.name());
        return param;
    }

    public static Param valueOf(TbParameter parameter, String origin, FormulaUtil.DataType type) {
        Param param = new Param();
        param.setName(parameter.getName());
        param.setUnit(parameter.getPaUnitID());
        param.setOrigin(origin);
        param.setToken(parameter.getToken());
        param.setDataType(parameter.getDataType());
        param.setValue(parameter.getPaValue());
        param.setType(type.name());
        return param;
    }

    public static Param valueOf(TbMonitorTypeField typeField, String origin, FormulaUtil.DataType type) {
        Param param = new Param();
        param.setName(typeField.getFieldName());
        param.setUnit(typeField.getFieldUnitID());
        param.setOrigin(origin);
        param.setToken(typeField.getFieldToken());
        param.setDataType(typeField.getFieldDataType());
        param.setExValues(typeField.getExValues());
        param.setType(type.name());
        return param;
    }
}