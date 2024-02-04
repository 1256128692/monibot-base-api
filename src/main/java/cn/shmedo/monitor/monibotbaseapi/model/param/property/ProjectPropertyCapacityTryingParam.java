package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Field;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.TryingRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicFieldToken.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-04 11:21
 */
@Data
public class ProjectPropertyCapacityTryingParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;
    @NotNull(message = "库水位值不能为空")
    @Positive(message = "库水位值不能为负值")
    private Double distance;
    @JsonIgnore
    private TryingRequest tryingRequest;
    /**
     * 水位库容关系,该<b>模板基础信息名称</b>和<b>工程</b>唯一确定配置的公式
     */
    private final static String PROPERTY_NAME = "水位库容关系";
    /**
     * 水位的公式<br>
     * 因为这个水位是用户输入的水位,所以它既没有关联模板,也没有相应的公式,需要暂时写死在这里
     */
    private final static String DISTANCE_FORMULA = "${iot:x.waterValue}";

    @Override
    public ResultWrapper<?> validate() {
        List<TbProperty> tbPropertyList = SpringUtil.getBean(TbPropertyMapper.class).selectList(
                new LambdaQueryWrapper<TbProperty>().eq(TbProperty::getName, PROPERTY_NAME));
        if (CollUtil.isEmpty(tbPropertyList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板基础信息没有" + PROPERTY_NAME + "项");
        }
        Integer propertyID = tbPropertyList.stream().findFirst().map(TbProperty::getID).orElseThrow();
        String formula = SpringUtil.getBean(TbProjectPropertyMapper.class).selectList(new LambdaQueryWrapper<TbProjectProperty>()
                        .eq(TbProjectProperty::getProjectID, projectID).eq(TbProjectProperty::getPropertyID, propertyID))
                .stream().findFirst().map(TbProjectProperty::getValue).filter(StrUtil::isNotEmpty).orElse(StrUtil.EMPTY);
        if (StrUtil.isBlank(formula)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程未配置" + PROPERTY_NAME);
        }
        tryingRequest = new TryingRequest();
        tryingRequest.setFieldID(1);
        tryingRequest.setCalType(CalType.FORMULA);
        tryingRequest.setParamMap(Map.of(DISTANCE_FORMULA, distance));
        tryingRequest.setFieldList(List.of(buildField(0, DISTANCE, DISTANCE_FORMULA), buildField(1, CAPACITY, formula)));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }

    private Field buildField(final Integer id, final String fieldToken, final String formula) {
        Field field = new Field();
        field.setID(id);
        field.setFieldToken(fieldToken);
        field.setFormula(formula);
        return field;
    }
}
