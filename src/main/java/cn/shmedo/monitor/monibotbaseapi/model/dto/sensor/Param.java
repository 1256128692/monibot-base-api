package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.util.FormulaUtil;
import lombok.Data;

@Data
public class Param {
    private String name;
    private String value;
    private Integer unit;
    private String origin;
    private String token;
    private String dataType;
    private String exValues;
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