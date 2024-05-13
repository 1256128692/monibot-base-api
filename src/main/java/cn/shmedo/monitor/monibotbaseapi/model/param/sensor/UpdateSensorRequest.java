package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private Integer monitorPointID;

    private List<Integer> monitorGroupIDList;

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

    @JsonIgnore
    private TbMonitorPoint tbMonitorPoint;

    @Override
    public ResultWrapper<?> validate() {
        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        this.sensor = sensorMapper.selectById(sensorID);
        Assert.notNull(sensor, "传感器不存在");

        // 校验监测点、监测组
        if (Objects.isNull(monitorPointID) && CollectionUtil.isNotEmpty(monitorGroupIDList))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "选择了监测组时，监测点不能为空");
        if (Objects.nonNull(monitorPointID)) {
            TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
            tbMonitorPoint = tbMonitorPointMapper.selectById(monitorPointID);
            if (Objects.isNull(tbMonitorPoint))
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
            // 如果绑定的监测点是已关联传感器的并且是单传感器类型的监测点，要提示监测点已绑定过传感器
            TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
            List<TbSensor> tbSensors = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>()
                    .eq(TbSensor::getProjectID, projectID)
                    .eq(TbSensor::getMonitorPointID, monitorPointID)
                    .eq(TbSensor::getDataSourceComposeType, 1));
            if (CollectionUtil.isNotEmpty(tbSensors))
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点已绑定过传感器");
            // 监测点、监测组关系不能重复添加
            if (CollectionUtil.isNotEmpty(monitorGroupIDList)) {
                TbMonitorGroupPointMapper tbMonitorGroupPointMapper = ContextHolder.getBean(TbMonitorGroupPointMapper.class);
                List<TbMonitorGroupPoint> existsGroupPointList = tbMonitorGroupPointMapper.selectList(new LambdaQueryWrapper<TbMonitorGroupPoint>()
                        .eq(TbMonitorGroupPoint::getMonitorPointID, monitorPointID)
                        .in(TbMonitorGroupPoint::getMonitorGroupID, monitorGroupIDList));
                monitorGroupIDList = monitorGroupIDList.stream().filter(newGroupID -> !existsGroupPointList.stream()
                        .map(TbMonitorGroupPoint::getMonitorGroupID).collect(Collectors.toSet()).contains(newGroupID)).collect(Collectors.toList());
            }
        }

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

    
    