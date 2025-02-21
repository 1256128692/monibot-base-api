package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-14 11:38
 */
@Data
public class QueryDryBeachDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Positive(message = "工程ID必须大于0")
    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @Positive(message = "干滩监测点ID不能小于0")
    @NotNull(message = "干滩监测点ID不能为空")
    private Integer dryBeachMonitorPointID;
    @Positive(message = "库水位监测点ID不能小于0")
    @NotNull(message = "库水位监测点ID不能为空")
    private Integer distanceMonitorPointID;
    @Positive(message = "降雨量监测点ID不能小于0")
    private Integer rainfallMonitorPointID;
    @JsonIgnore
    private List<Map<String, Object>> pointIDExValuesList;
    @JsonIgnore
    private List<TbSensor> tbSensorList;
    @JsonIgnore
    private Map<Integer, List<TbMonitorTypeField>> monitorTypeFieldMap;
    @JsonIgnore
    private Integer rainfallMonitorType;

    @Override
    public ResultWrapper validate() {
        List<Integer> pointList = CollUtil.toList(dryBeachMonitorPointID, distanceMonitorPointID);
        Optional.ofNullable(rainfallMonitorPointID).ifPresent(pointList::add);
        pointIDExValuesList = ContextHolder.getBean(TbMonitorPointMapper.class).selectMonitorTypeExValuesByPointIDList(pointList);
        if (pointIDExValuesList.size() != pointList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在!");
        }
        for (Map<String, Object> map : pointIDExValuesList) {
            Integer pointID = (Integer) map.get("monitorPointID");
            Integer monitorType = (Integer) map.get("monitorType");
            if (dryBeachMonitorPointID.equals(pointID) && !monitorType.equals(MonitorType.DRY_BEACH.getKey())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传入的干滩监测点的监测类型错误!");
            } else if (distanceMonitorPointID.equals(pointID) && !monitorType.equals(MonitorType.WATER_LEVEL.getKey())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传入的库水位监测点的监测类型错误!");
            } else if (Optional.ofNullable(rainfallMonitorPointID).map(data -> data.equals(pointID)).orElse(false)) {
                rainfallMonitorType = monitorType;
                if (!(monitorType.equals(MonitorType.WT_RAINFALL.getKey()) || monitorType.equals(MonitorType.RAINFALL.getKey()))) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传入的降雨量监测点的监测类型错误!");
                }
            }
        }
        tbSensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .in(TbSensor::getMonitorPointID, pointList));
        List<Integer> monitorTypeList = tbSensorList.stream().map(TbSensor::getMonitorType).distinct().toList();
        monitorTypeFieldMap = ContextHolder.getBean(TbMonitorTypeFieldMapper.class).selectList(
                        new LambdaQueryWrapper<TbMonitorTypeField>().in(TbMonitorTypeField::getMonitorType, monitorTypeList))
                .stream().collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
