package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
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
import java.util.Optional;

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
        //校验扩展配置
        Optional.ofNullable(exFields).ifPresent(fields ->
                sensor.setConfigFieldValue(SaveSensorRequest.validExField(sensor.getMonitorType(), exFields, redisService)));
        //校验参数
        Optional.ofNullable(paramFields).ifPresent(fields ->
                this.paramList = SaveSensorRequest.validParamField(sensor.getTemplateID(), sensorID, paramFields, redisService));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    