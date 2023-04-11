package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbParameterMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorTypeFieldClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ParamSubjectType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.Assert;

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
    private List<SensorConfigField> exFields;

    /**
     * 参数列表
     */
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

        if (CollUtil.isNotEmpty(exFields)) {
            TbMonitorTypeFieldMapper monitorTypeFieldMapper = SpringUtil.getBean(TbMonitorTypeFieldMapper.class);
            Map<Integer, String> exMap = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                    .eq(TbMonitorTypeField::getMonitorType, sensor.getMonitorType())
                    .eq(TbMonitorTypeField::getFieldClass, MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass())
                    .in(TbMonitorTypeField::getID, exFields.stream().map(SensorConfigField::getId).toList())
                    .select(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName)
            ).stream().collect(Collectors.toMap(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName));
            exFields.forEach(ex -> {
                Assert.isTrue(exMap.containsKey(ex.getId()), "扩展配置项不存在");
                ex.setName(exMap.get(ex.getId()));
            });
            Map<String, String> exConfig = exFields.stream()
                    .map(e -> Map.entry(e.getName(), e.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            sensor.setConfigFieldValue(JSONUtil.toJsonStr(exConfig));
        }

        //
        if (CollUtil.isNotEmpty(paramFields)) {
            TbParameterMapper parameterMapper = SpringUtil.getBean(TbParameterMapper.class);
            Map<Integer, TbParameter> paramMap = parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                    .eq(TbParameter::getSubjectType, ParamSubjectType.Sensor.getType())
                    .eq(TbParameter::getSubjectID, sensor.getID())
            ).stream().collect(Collectors.toMap(TbParameter::getID, e -> e));

            paramList = paramFields.stream().map(e -> {
                Assert.isTrue(paramMap.containsKey(e.getId()), "参数配置项 [" + e.getId() + "]不存在");
                TbParameter param = paramMap.get(e.getId());
                param.setPaValue(e.getValue());
                return param;
            }).toList();
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    