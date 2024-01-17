package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CompareMode;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnLevelType;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 14:55
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnThresholdConfigParam extends CompanyPlatformParam {
    @NotNull(message = "监测项目ID不能为空")
    @Positive(message = "监测项目ID必须为正值")
    private Integer monitorItemID;
    @NotNull(message = "传感器ID不能为空")
    @Positive(message = "传感器ID必须为正值")
    private Integer sensorID;
    @NotNull(message = "属性ID不能为空")
    @Positive(message = "属性ID必须为正值")
    private Integer fieldID;
    private String warnName;
    @NotNull(message = "比较方式不能为空")
    @Range(min = 1, max = 6, message = "比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于")
    private Integer compareMode;
    @NotNull(message = "是否启用不能为空")
    private Boolean enable;
    private String value;
    @JsonIgnore
    private TbWarnThresholdConfig tbWarnThresholdConfig;

    @Override
    public ResultWrapper validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        final Integer companyID = getCompanyID();
        final Integer platform = getPlatform();
//        final Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        final Date current = new Date();
        if (enable && ObjectUtil.isEmpty(value)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未配置完全的报警阈值配置无法启用");
        }
        if (!ContextHolder.getBean(TbMonitorItemMapper.class).exists(new LambdaQueryWrapper<TbMonitorItem>().eq(TbMonitorItem::getID, monitorItemID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getID, sensorID));
        if (CollUtil.isEmpty(sensorList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不存在");
        }
        final TbSensor tbSensor = sensorList.stream().findAny().orElseThrow();
        final Integer monitorType = tbSensor.getMonitorType();
        if (!ContextHolder.getBean(TbMonitorTypeFieldMapper.class).exists(new LambdaQueryWrapper<TbMonitorTypeField>()
                .eq(TbMonitorTypeField::getID, fieldID).eq(TbMonitorTypeField::getMonitorType, monitorType))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性不存在");
        }
        if (ObjectUtil.isNotEmpty(value)) {
            TbWarnBaseConfig tbWarnBaseConfig = ContextHolder.getBean(ITbWarnBaseConfigService.class)
                    .queryByCompanyIDAndPlatform(companyID, platform);
            Set<Integer> warnLevelSet = DataWarnLevelType.fromCode(tbWarnBaseConfig.getWarnLevelType()).getWarnLevelSet();
            List<String> configKeyList = CompareMode.fromCode(compareMode).getConfigKeyList();
            try {
                // e.g. {"1":{"upper":100,"lower":50},"2":{"upper":50,"lower":25},...}
                JSONObject object = JSONUtil.parseObj(value);
                Set<String> configWarnLevel = object.keySet();
                // check keys
                if (enable && configWarnLevel.size() == 0) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "未配置完全的报警阈值配置无法启用");
                }
                if (configWarnLevel.stream().map(Integer::valueOf).anyMatch(u -> !warnLevelSet.contains(u))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "配置的报警等级阈值和平台报警等级枚举设置不匹配");
                }
                // check values
                if (object.values().stream().map(JSONUtil::parseObj).map(MapWrapper::keySet).anyMatch(u -> !u.containsAll(configKeyList))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "配置的报警等级阈值和比较方式不匹配");
                }
            } catch (JSONException e) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "配置的报警等级阈值不合法");
            }
        }
        List<TbWarnThresholdConfig> tbWarnThresholdConfigList = ContextHolder.getBean(TbWarnThresholdConfigMapper.class)
                .selectList(new LambdaQueryWrapper<TbWarnThresholdConfig>().eq(TbWarnThresholdConfig::getCompanyID, companyID)
                        .eq(TbWarnThresholdConfig::getPlatform, platform).eq(TbWarnThresholdConfig::getMonitorItemID, monitorItemID)
                        .eq(TbWarnThresholdConfig::getSensorID, sensorID).eq(TbWarnThresholdConfig::getFieldID, fieldID));
        boolean notExistsConfig = CollUtil.isEmpty(tbWarnThresholdConfigList);
        this.tbWarnThresholdConfig = notExistsConfig ? new TbWarnThresholdConfig() :
                tbWarnThresholdConfigList.stream().findFirst().orElseThrow();
        BeanUtil.copyProperties(this, this.tbWarnThresholdConfig);
        this.tbWarnThresholdConfig.setUpdateTime(current);
        this.tbWarnThresholdConfig.setProjectID(tbSensor.getProjectID());
        this.tbWarnThresholdConfig.setMonitorType(tbSensor.getMonitorType());
        if (notExistsConfig) {
            this.tbWarnThresholdConfig.setCreateTime(current);
        }
        return null;
    }
}
