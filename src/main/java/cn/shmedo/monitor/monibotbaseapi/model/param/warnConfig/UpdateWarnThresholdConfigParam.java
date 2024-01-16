package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 14:55
 */
@Data
@Slf4j
public class UpdateWarnThresholdConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "传感器ID不能为空")
    @Positive(message = "传感器ID必须为正值")
    private Integer sensorID;
    @NotNull(message = "属性ID不能为空")
    @Positive(message = "属性ID必须为正值")
    private Integer fieldID;
    private String warnName;
    @Range(min = 1, max = 6, message = "比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于")
    private Integer compareMode;
    private Boolean enable;
    private String value;

    @Override
    public ResultWrapper validate() {
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getID, sensorID));
        if (CollUtil.isEmpty(sensorList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不存在");
        }
        TbSensor tbSensor = sensorList.stream().findAny().orElseThrow();
        Integer monitorType = tbSensor.getMonitorType();
        if (!ContextHolder.getBean(TbMonitorTypeFieldMapper.class).exists(new LambdaQueryWrapper<TbMonitorTypeField>()
                .eq(TbMonitorTypeField::getID, fieldID).eq(TbMonitorTypeField::getMonitorType, monitorType))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性不存在");
        }
        if (ObjectUtil.isNotEmpty(value)) {
            try {
                JSONArray array = JSONUtil.parseArray(value);
            } catch (JSONException e) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "配置的报警等级阈值不合法");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
