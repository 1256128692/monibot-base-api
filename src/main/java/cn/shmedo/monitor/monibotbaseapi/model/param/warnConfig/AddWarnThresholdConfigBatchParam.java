package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CompareMode;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IThresholdConfigValueCheck;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 15:59
 */
@Data
@Slf4j
public class AddWarnThresholdConfigBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck, IThresholdConfigValueCheck {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;
    private Boolean isEmptyCoverage;
    @NotEmpty(message = "传感器ID List不能为空")
    private List<Integer> sensorIDList;
    @NotEmpty(message = "数据列表不能为空")
    private List<@Valid AddWarnThresholdConfigBatchDataListParam> dataList;
    @JsonIgnore
    private Integer monitorItemID;
    @JsonIgnore
    private Integer monitorType;
    @JsonIgnore
    private Integer projectID;
    @JsonIgnore
    private List<TbWarnThresholdConfig> tbWarnThresholdConfigList;

    @Override
    public ResultWrapper<?> validate() {
        if (Objects.isNull(isEmptyCoverage)) {
            isEmptyCoverage = false;
        }
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        List<TbSensor> tbSensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getID, sensorIDList));
        if (tbSensorList.stream().anyMatch(u -> Objects.isNull(u.getMonitorPointID()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器未关联监测点");
        }
        if (!tbSensorList.stream().map(TbSensor::getID).collect(Collectors.toSet()).containsAll(sensorIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在");
        }
        Set<Integer> monitorPointIDSet = tbSensorList.stream().map(TbSensor::getMonitorPointID).collect(Collectors.toSet());
        List<TbMonitorPoint> tbMonitorPointList = ContextHolder.getBean(TbMonitorPointMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorPoint>().in(TbMonitorPoint::getID, monitorPointIDSet));
        Set<Integer> dbMonitorPointIDSet = tbMonitorPointList.stream().map(TbMonitorPoint::getID).collect(Collectors.toSet());
        if (!dbMonitorPointIDSet.containsAll(monitorPointIDSet)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在");
        }
        Set<Integer> monitorItemIDSet = tbMonitorPointList.stream().map(TbMonitorPoint::getMonitorItemID).collect(Collectors.toSet());
        if (monitorItemIDSet.size() != 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不属于同一监测项目");
        }
        this.monitorItemID = monitorItemIDSet.stream().findAny().orElseThrow();
        this.projectID = tbMonitorPointList.stream().map(TbMonitorPoint::getProjectID).findAny().orElseThrow();
        this.monitorType = tbMonitorPointList.stream().map(TbMonitorPoint::getMonitorType).findAny().orElseThrow();
        return validateDataList();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private ResultWrapper<?> validateDataList() {
        Date current = new Date();
        List<Integer> fieldIDList = dataList.stream().map(AddWarnThresholdConfigBatchDataListParam::getFieldID).toList();
        Set<Integer> fieldIDSet = ContextHolder.getBean(TbMonitorItemFieldMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorItemField>().eq(TbMonitorItemField::getMonitorItemID, monitorItemID))
                .stream().map(TbMonitorItemField::getMonitorTypeFieldID).collect(Collectors.toSet());
        if (!fieldIDSet.containsAll(fieldIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测属性ID不合法");
        }
        Map<Integer, String> fieldIDNameMap = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorTypeField>().in(TbMonitorTypeField::getID, fieldIDList))
                .stream().collect(Collectors.toMap(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName));
        final Set<Integer> warnLevelSet = ContextHolder.getBean(ITbWarnBaseConfigService.class).getWarnLevelSet(companyID, platform);
        try {
            dataList.stream().peek(u -> {
                if (Objects.isNull(u.getEnable())) {
                    u.setEnable(false);
                }
                List<String> configKeyList = CompareMode.fromCode(u.getCompareMode()).getConfigKeyList();
                this.validateValue(u.getValue(), u.getEnable(), warnLevelSet, configKeyList);
            }).toList();
        } catch (IllegalArgumentException e) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, e.getMessage());
        }
        tbWarnThresholdConfigList = dataList.stream().map(u -> sensorIDList.stream().map(w ->
                        new TbWarnThresholdConfig(null, companyID, platform, projectID, monitorType, monitorItemID,
                                u.getCompareMode(), w, u.getFieldID(), u.getEnable(), u.getWarnName(), u.getValue(),
                                null, current, null, current, null)).toList())
                .flatMap(Collection::stream).toList();
        // query which records needs to be updated.
        Map<String, TbWarnThresholdConfig> tbWarnThresholdConfigMap = ContextHolder.getBean(TbWarnThresholdConfigMapper.class)
                .selectList(new LambdaQueryWrapper<TbWarnThresholdConfig>().and(wrapper ->
                        tbWarnThresholdConfigList.stream().peek(u -> wrapper.or(w ->
                                w.eq(TbWarnThresholdConfig::getPlatform, u.getPlatform())
                                        .eq(TbWarnThresholdConfig::getMonitorItemID, u.getMonitorItemID())
                                        .eq(TbWarnThresholdConfig::getSensorID, u.getSensorID())
                                        .eq(TbWarnThresholdConfig::getFieldID, u.getFieldID()))).toList())).stream()
                .collect(Collectors.toMap(AddWarnThresholdConfigBatchParam::combineThresholdConfigKey, Function.identity()));
        if (CollUtil.isNotEmpty(tbWarnThresholdConfigMap)) {
            tbWarnThresholdConfigList.stream().filter(u -> tbWarnThresholdConfigMap.containsKey(combineThresholdConfigKey(u)))
                    .peek(u -> Optional.of(tbWarnThresholdConfigMap).map(w -> w.get(combineThresholdConfigKey(u))).ifPresent(w -> {
                        u.setId(w.getId());
                        u.setCreateTime(w.getCreateTime());
                        u.setCreateUserID(w.getCreateUserID());
                        final Integer compareMode = u.getCompareMode();
                        final String oldValue = w.getValue();
                        try {
                            if (!isEmptyCoverage) {
                                // 如果未选择'空值覆盖'并且用户没填{@code warnName}时,才会获取原来的报警名称并填充进来（这里的数据都是已经配置过的）
                                if (ObjectUtil.isEmpty(u.getWarnName())) {
                                    u.setWarnName(w.getWarnName());
                                }
                                JSONObject updateJson = JSONUtil.parseObj(u.getValue());
                                // 如果比较方式相同且要求空值不覆盖,才会根据{@code oldJson}将{@code updateJson}补全
                                if (compareMode.equals(w.getCompareMode())) {
                                    dealNotEmptyCoverage(warnLevelSet, JSONUtil.parseObj(oldValue), updateJson);
                                    u.setValue(JSONUtil.toJsonStr(updateJson));
                                }
                            }
                        } catch (JSONException e) {
                            // 传参json已经校验过,这里抛出来的异常必定是原有json(脏数据)解析错误,此时直接覆盖掉原来的错误json好了
                            log.error("parse json error,json:{}", oldValue);
                        }
                        // 如果之前的配置是'启用'的,就需要根据新配置是否能启用来设置对应的启用状态
                        if (w.getEnable()) {
                            u.setEnable(this.queryConfigStatus(u.getValue(), true));
                        }
                    })).toList();
        }
        // 确保新增的和空值覆盖的记录都有对应的报警名称
        tbWarnThresholdConfigList.stream().filter(u -> ObjectUtil.isEmpty(u.getWarnName())).peek(u ->
                u.setWarnName(fieldIDNameMap.get(u.getFieldID()) + "异常")).toList();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    /**
     * 根据{@code oldJson}对{@code updateJson}进行补全.<br>以下两种情况不进行补全
     * <p>
     * 1、变更了<b>比较方式</b>,此时原有的json不适用于当前的比较方式了;<br>
     * 2、选择了<b>'空值覆盖'</b>,此时是用户选择覆盖掉原配置.
     * </p>
     *
     * @param updateJson 传参解析的json,里面的参数会变动,会作为结果返回
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void dealNotEmptyCoverage(final Set<Integer> warnLevelSet, final JSONObject oldJson, JSONObject updateJson) {
        warnLevelSet.stream().map(String::valueOf).peek(u -> Optional.ofNullable(oldJson.getOrDefault(u, null))
                .map(w -> (JSONObject) w).filter(w -> Objects.isNull(updateJson.getOrDefault(u, null)))
                .ifPresent(w -> updateJson.putOnce(u, w))).toList();
    }

    /**
     * (non-Javadoc)
     *
     * @see #combineThresholdConfigKey(Integer, Integer, Integer, Integer)
     */
    private static String combineThresholdConfigKey(final TbWarnThresholdConfig config) {
        return combineThresholdConfigKey(config.getPlatform(), config.getMonitorItemID(), config.getSensorID(), config.getFieldID());
    }

    /**
     * combine to a unique key.
     */
    private static String combineThresholdConfigKey(final Integer platform, final Integer monitorItemID,
                                                    final Integer sensorID, final Integer fieldID) {
        return platform + "@@" + monitorItemID + "@@" + sensorID + "@@" + fieldID;
    }
}
