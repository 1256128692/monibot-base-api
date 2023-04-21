package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.convert.Convert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTemplateFormulaMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTemplateScriptMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateFormula;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateScript;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Field;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Param;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 试运行 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class TryingRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 监测类型
     */
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    /**
     * 监测类型模板ID
     */
    @NotNull(message = "模板ID不能为空")
    private Integer templateID;

    /**
     * 监测字段ID
     */
    @NotNull(message = "待计算字段不能为空")
    private Integer fieldID;

    /**
     * 参数列表
     */
    private List<Param> paramList;

    @JsonIgnore
    private Map<String, Double> paramMap;

    @JsonIgnore
    private CalType calType;

    @JsonIgnore
    private List<Field> fieldList = new ArrayList<>();

    @JsonIgnore
    private String script;

    @Override
    public ResultWrapper<?> validate() {
        // 校验监测类型模板
        TbMonitorTypeTemplateMapper monitorTypeTemplateMapper = SpringUtil.getBean(TbMonitorTypeTemplateMapper.class);
        TbMonitorTypeTemplate monitorTypeTemplate = monitorTypeTemplateMapper.selectOne(new LambdaQueryWrapper<TbMonitorTypeTemplate>()
                .eq(TbMonitorTypeTemplate::getMonitorType, this.monitorType)
                .eq(TbMonitorTypeTemplate::getID, this.templateID)
                .select(TbMonitorTypeTemplate::getCalType));
        Optional.ofNullable(monitorTypeTemplate).orElseThrow(() -> new IllegalArgumentException("监测类型模板不存在"));

        // 校验监测类型字段
        TbMonitorTypeFieldMapper monitorTypeFieldMapper = SpringUtil.getBean(TbMonitorTypeFieldMapper.class);
        Map<Integer, TbMonitorTypeField> fieldMap = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                        .in(TbMonitorTypeField::getFieldClass, FieldClass.BASIC.getCode(),
                                FieldClass.EXTEND.getCode())
                        .eq(TbMonitorTypeField::getMonitorType, this.monitorType))
                .stream().collect(Collectors.toMap(TbMonitorTypeField::getID, e -> e));
        Optional.ofNullable(fieldMap.get(fieldID))
                .orElseThrow(() -> new IllegalArgumentException("监测类型字段" + fieldID + "不存在"));

        this.calType = CalType.codeOf(monitorTypeTemplate.getCalType());
        switch (calType) {
            case FORMULA:
                //获取所有公式，按计算顺序提取至当前字段
                TbTemplateFormulaMapper templateFormulaMapper = SpringUtil.getBean(TbTemplateFormulaMapper.class);
                List<TbTemplateFormula> formulaList = templateFormulaMapper.selectList(new LambdaQueryWrapper<TbTemplateFormula>()
                        .eq(TbTemplateFormula::getTemplateID, this.templateID)
                        .eq(TbTemplateFormula::getMonitorType, this.monitorType)
//                        .select(TbTemplateFormula::getFieldID, TbTemplateFormula::getFormula)
                        .orderByAsc(TbTemplateFormula::getFieldCalOrder));
                formulaList.stream().filter(formula -> formula.getFieldID().equals(fieldID)).findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("监测类型字段" + fieldID + "计算公式不存在"));

                for (TbTemplateFormula formula : formulaList) {
                    Field field = Field.valueOf(fieldMap.get(formula.getFieldID()));
                    field.setFormula(formula.getFormula());
                    this.fieldList.add(field);
                    if (formula.getFieldID().equals(fieldID)) {
                        break;
                    }
                }
                break;
            case SCRIPT:
                TbTemplateScriptMapper templateScriptMapper = SpringUtil.getBean(TbTemplateScriptMapper.class);
                TbTemplateScript templateScript = templateScriptMapper.selectOne(new LambdaQueryWrapper<TbTemplateScript>()
                        .eq(TbTemplateScript::getTemplateID, this.templateID)
                        .eq(TbTemplateScript::getMonitorType, this.monitorType)
                        .select(TbTemplateScript::getScript));
                Optional.ofNullable(templateScript).orElseThrow(() -> new IllegalArgumentException("监测类型模板计算脚本不存在"));
                this.script = templateScript.getScript();
                break;
            case HTTP:
                break;
            case NONE:
                return ResultWrapper.withCode(ResultCode.SUCCESS, "无需计算");
        }

        //转换参数列表为map
        this.paramList.stream().filter(param -> param.getOrigin() == null || param.getValue() == null)
                .findFirst().ifPresent(param -> {
                    throw new IllegalArgumentException("参数列表中存在空值");
                });
        this.paramMap = this.paramList.stream().collect(Collectors.toMap(Param::getOrigin, e -> Convert.toDouble(e.getValue())));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    