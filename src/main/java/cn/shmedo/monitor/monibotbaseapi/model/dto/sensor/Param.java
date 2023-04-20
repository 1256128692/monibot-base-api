package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaData;
import cn.shmedo.monitor.monibotbaseapi.util.formula.Origin;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

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

    public static Param valueOf(Map<String, Model> modelMap, FormulaData data) {
        String modelToken = StrUtil.subBefore(data.getSourceToken(), StrUtil.UNDERLINE, false);
        Assert.isTrue(modelMap.containsKey(modelToken), "数据源物模型 {} 不存在", modelToken);

        Model model = modelMap.get(modelToken);
        Map<String, Model.Field> fieldMap = model.getModelFieldList().stream()
                .collect(Collectors.toMap(Model.Field::getFieldToken, field -> field));
        Param param = new Param();
        if (FormulaData.Provide.DATA.equals(data.getProvide())) {
            Model.Field field = fieldMap.get(data.getFieldToken());
            Assert.notNull(field, "数据源物模型 {} 不存在字段 {}", modelToken, data.getFieldToken());
            param.setName(field.getFieldName());
            param.setUnit(field.getFieldUnitID());
            param.setToken(field.getFieldToken());
            param.setDataType(field.getFieldDataType());
            param.setExValues(field.getExValues());
        } else {
            param.setName(model.getModelName() + data.getProvide().getValue());
        }
        param.setOrigin(data.getOrigin());
        param.setType(data.getType().name());
        return param;
    }



    public static Param valueOf(TbParameter parameter, String origin, Origin.Type type) {
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

    public static Param valueOf(TbMonitorTypeField typeField, String origin, Origin.Type type) {
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