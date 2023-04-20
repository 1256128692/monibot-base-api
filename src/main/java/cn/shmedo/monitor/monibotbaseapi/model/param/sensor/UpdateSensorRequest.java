package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.ParameterSubjectType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 更新传感器 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class UpdateSensorRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 传感器ID
     */
    @NotNull(message = "传感器ID不能为空")
    private Integer sensorID;

    /**
     * 传感器图片路径
     */
    private String imagePath;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 扩展配置列表
     */
    @Valid
    private List<SensorConfigField> exFields;

    /**
     * 参数列表
     */
    @Valid
    private List<SensorConfigField> paramFields;

    /**
     * 扩展字段
     */
    private String exValues;

    /**
     * 显示排序
     */
    private Integer displayOrder;

    @JsonIgnore
    private String configFieldValue;

    @JsonIgnore
    private List<TbParameter> paramList = Collections.emptyList();

    @JsonIgnore
    private TbSensor sensor;

    @Override
    public ResultWrapper<?> validate() {
        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        this.sensor = sensorMapper.selectById(sensorID);
        Assert.notNull(sensor, "传感器不存在");

        RedisService redisService = SpringUtil.getBean(RedisConstant.MONITOR_REDIS_SERVICE, RedisService.class);
        if (CollUtil.isNotEmpty(exFields)) {
            MonitorTypeCacheData monitorTypeCacheData = redisService.get(RedisKeys.MONITOR_TYPE_KEY,
                    sensor.getMonitorType().toString(), MonitorTypeCacheData.class);
            Map<Integer, String> exMap = monitorTypeCacheData.getMonitortypeFieldList().stream()
                    .filter(e -> FieldClass.EXTEND_CONFIG.equals(e.getFieldClass()))
                    .collect(Collectors.toMap(MonitorTypeCacheData.Field::getID, MonitorTypeCacheData.Field::getFieldName));
            exFields.forEach(ex -> {
                Assert.isTrue(exMap.containsKey(ex.getId()), "扩展配置项 [" + ex.getId() + "]不存在");
                ex.setName(exMap.get(ex.getId()));
            });
            Map<String, String> exConfig = exFields.stream()
                    .map(e -> Map.entry(e.getName(), e.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            sensor.setConfigFieldValue(JSONUtil.toJsonStr(exConfig));
        }

        //校验参数
        if (CollUtil.isNotEmpty(paramFields)) {
            List<TbParameter> templateParams = redisService.getList(RedisKeys.PARAMETER_PREFIX_KEY +
                    ParameterSubjectType.TEMPLATE.getCode(), sensor.getTemplateID().toString(), TbParameter.class);
            Map<Integer, SensorConfigField> paramFieldMap = paramFields.stream().collect(Collectors.toMap(SensorConfigField::getId, e -> e));
            paramList = templateParams.stream().map(param -> {
                SensorConfigField field = paramFieldMap.get(param.getID());
                Assert.notNull(field, "参数配置项 [" + param.getID() + "]不存在");
                Assert.notNull(field.getValue(), "参数配置项 [" + param.getID() + "]值不能为空");
                TbParameter item = BeanUtil.copyProperties(param, TbParameter.class);
                item.setID(null);
                item.setSubjectType(ParameterSubjectType.SENSOR.getCode());
                item.setPaValue(paramFieldMap.get(param.getID()).getValue());
                return item;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    