package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeTemplateCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 试运行 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class CalculateFieldRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @Positive
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @NotNull
    @Positive
    private Integer sensorID;

    @NotNull
    @Positive
    private Integer targetFieldID;

    @Valid
    private List<@NotNull Param> paramList;

    record Param(@NotNull @Positive Integer fieldID, @NotNull Double value) {
    }

    @JsonIgnore
    private Map<String, Double> paramFiledMap;

    @JsonIgnore
    private MonitorTypeTemplateCacheData template;

    @JsonIgnore
    private Dict configFieldValue;

    @JsonIgnore
    private String fieldToken;

    @Override
    public ResultWrapper<?> validate() {
        paramList = Optional.ofNullable(paramList).orElse(List.of());

        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        TbSensor sensor = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery().eq(TbSensor::getID, sensorID)
                .eq(TbSensor::getProjectID, projectID).select(TbSensor::getProjectID, TbSensor::getMonitorType,
                        TbSensor::getConfigFieldValue, TbSensor::getTemplateID));
        Assert.notNull(sensor, () -> new InvalidParameterException("传感器不存在"));
        this.configFieldValue = JSONUtil.toBean(sensor.getConfigFieldValue(), Dict.class);

        RedisService redisService = SpringUtil.getBean(RedisConstant.MONITOR_REDIS_SERVICE, RedisService.class);
        template = redisService.get(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY,
                sensor.getTemplateID().toString(), MonitorTypeTemplateCacheData.class);
        Assert.notNull(template, "传感器计算模板不存在");

        TbMonitorTypeFieldMapper monitorTypeFieldMapper = SpringUtil.getBean(TbMonitorTypeFieldMapper.class);
        Map<Integer, TbMonitorTypeField> fieldMap = monitorTypeFieldMapper.selectList(Wrappers.<TbMonitorTypeField>lambdaQuery()
                        .in(TbMonitorTypeField::getID, Stream.concat(Stream.of(targetFieldID), paramList.stream().map(Param::fieldID)).toList())
                        .select(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldToken))
                .stream().collect(Collectors.toMap(TbMonitorTypeField::getID, e -> e));
        Assert.isTrue(fieldMap.containsKey(targetFieldID), () -> new InvalidParameterException("字段 " + targetFieldID + "不存在"));
        this.fieldToken = fieldMap.get(targetFieldID).getFieldToken();

        paramFiledMap = paramList.stream().map(e -> {
            TbMonitorTypeField f = fieldMap.get(e.fieldID());
            Assert.notNull(f, () -> new InvalidParameterException("字段 " + e.fieldID() + "不存在"));
            return Map.entry(f.getFieldToken(), e.value());
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    