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
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeTemplateCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

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

    @NotBlank
    private String targetFieldToken;

    @Valid
    private List<@NotNull Param> paramList;

    record Param(@NotEmpty String fieldToken, @NotNull Double value) {
    }

    @JsonIgnore
    private Map<String, Double> paramFiledMap;

    @JsonIgnore
    private MonitorTypeTemplateCacheData template;

    @JsonIgnore
    private Dict configFieldValue;

    @JsonIgnore
    private Integer targetFieldID;

    @Override
    public ResultWrapper<?> validate() {

        //校验传感器
        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        TbSensor sensor = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery().eq(TbSensor::getID, sensorID)
                .eq(TbSensor::getProjectID, projectID).select(TbSensor::getProjectID, TbSensor::getMonitorType,
                        TbSensor::getConfigFieldValue, TbSensor::getTemplateID));
        Assert.notNull(sensor, () -> new InvalidParameterException("传感器不存在"));
        this.configFieldValue = JSONUtil.toBean(sensor.getConfigFieldValue(), Dict.class);

        //获取模板
        RedisService redisService = SpringUtil.getBean(RedisConstant.MONITOR_REDIS_SERVICE, RedisService.class);
        template = redisService.get(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY,
                sensor.getTemplateID().toString(), MonitorTypeTemplateCacheData.class);
        Assert.notNull(template, "传感器计算模板不存在");

        // 校验字段
        Map<String, MonitorTypeCacheData.Field> fieldMap = redisService.get(RedisKeys.MONITOR_TYPE_KEY,
                        sensor.getMonitorType().toString(), MonitorTypeCacheData.class)
                .getMonitortypeFieldList().stream()
                .collect(Collectors.toMap(MonitorTypeCacheData.Field::getFieldToken, e -> e));
        Assert.isTrue(fieldMap.containsKey(targetFieldToken), () -> new InvalidParameterException("目标字段不存在"));
        this.targetFieldID = fieldMap.get(targetFieldToken).getID();


        paramFiledMap = Optional.ofNullable(paramList).orElse(List.of()).stream()
                .peek(e -> Assert.isTrue(fieldMap.containsKey(e.fieldToken()),
                        () -> new InvalidParameterException("未知参数字段: " + e.fieldToken)))
                .collect(Collectors.toMap(Param::fieldToken, Param::value));
        Assert.isFalse(paramFiledMap.containsKey(targetFieldToken), () -> new InvalidParameterException("目标字段不能作为参数"));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    