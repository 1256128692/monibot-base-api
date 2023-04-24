package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaData;
import cn.shmedo.monitor.monibotbaseapi.util.formula.Origin;
import lombok.Data;

import java.util.Map;
import java.util.function.Supplier;
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
        param.setName(nameBuilder(model.getModelName(), param, data, () -> {
            Model.Field field = fieldMap.get(data.getFieldToken());
            Assert.notNull(field, "数据源物模型 {} 不存在字段 {}", modelToken, data.getFieldToken());
            param.setUnit(field.getFieldUnitID());
            param.setToken(field.getFieldToken());
            param.setDataType(field.getFieldDataType());
            param.setExValues(field.getExValues());
            return field.getFieldName();
        }));
        param.setOrigin(data.getOrigin());
        param.setType(data.getType().name());
        return param;
    }

    public static Param valueOf(MonitorTypeCacheData monitorType, FormulaData data) {
        Param param = new Param();
        Map<String, MonitorTypeCacheData.Field> fieldMap = monitorType.getMonitortypeFieldList().stream()
                .collect(Collectors.toMap(MonitorTypeCacheData.Field::getFieldToken, field -> field));

        String s = nameBuilder(StrUtil.EMPTY, param, data, () -> {
            MonitorTypeCacheData.Field field = fieldMap.get(data.getFieldToken());
            param.setUnit(field.getFieldUnitID());
            param.setToken(field.getFieldToken());
            param.setDataType(field.getFieldDataType());
            param.setExValues(field.getExValues());
            return field.getFieldName();
        });
        param.setName(s);
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

    public static Param valueOf(MonitorTypeCacheData.Field field, String origin, Origin.Type type) {
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

    private static String nameBuilder(String initStr, Param param, FormulaData data, Supplier<String> nameSupplier) {
        StringBuilder nameBuilder = new StringBuilder(initStr);
        if (data.getBeginDate() > 0 && data.getEndDate() > 0) {
            StrUtil.concat(true, DateTime.of(data.getBeginDate()).toStringDefaultTimeZone(), "至",
                    DateTime.of(data.getEndDate()).toStringDefaultTimeZone(), "最新");
        } else if (data.getBeginDate() > 0) {
            StrUtil.concat(true, DateTime.of(data.getBeginDate()).toStringDefaultTimeZone(), "之后最新");
        } else if (data.getEndDate() > 0) {
            StrUtil.concat(true, DateTime.of(data.getEndDate()).toStringDefaultTimeZone(), "之前最新");
        } else if (-1 == data.getEndDate()) {
            nameBuilder.append("上一组");
        }
        if (FormulaData.Provide.DATA.equals(data.getProvide())) {
            nameBuilder.append(nameSupplier.get());
            if (StrUtil.isNotBlank(data.getFieldChildToken())) {
                if (NumberUtil.isNumber(data.getFieldChildToken())) {
                    nameBuilder.append("第").append(data.getFieldChildToken()).append("个元素");
                } else {
                    nameBuilder.append(data.getFieldChildToken());
                }
            }
        } else {
            nameBuilder.append(data.getProvide().getValue());
            param.setDataType("Long");
        }
        return nameBuilder.toString();
    }
}