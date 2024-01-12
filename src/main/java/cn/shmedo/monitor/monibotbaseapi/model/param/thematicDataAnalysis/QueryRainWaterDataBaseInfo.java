package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 15:55
 */
@Data
public class QueryRainWaterDataBaseInfo implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Positive(message = "工程ID必须大于0")
    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @Positive(message = "降雨量监测点ID必须大于0")
    @NotNull(message = "降雨量监测点ID不能为空")
    private Integer rainfallMonitorPointID;
    @Positive(message = "库水位监测点ID必须大于0")
    @NotNull(message = "库水位监测点ID不能为空")
    private Integer distanceMonitorPointID;
    @Positive(message = "入库流量监测点ID必须大于0")
    private Integer volumeFlowInputMonitorPointID;
    @Positive(message = "出库流量监测点ID必须大于0")
    private Integer volumeFlowOutputMonitorPointID;
    @NotNull(message = "显示密度不能为空")
    @Range(min = 1, max = 6, message = "显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年")
    private Integer displayDensity;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @JsonIgnore
    private String rainfallToken;
    @JsonIgnore
    private List<Integer> monitorIDList;

    @Override
    public ResultWrapper validate() {
        Date currentDate = new Date();
        endTime = endTime.after(currentDate) ? currentDate : endTime;
        monitorIDList = new ArrayList<>();
        monitorIDList.add(rainfallMonitorPointID);
        monitorIDList.add(distanceMonitorPointID);
        Optional.ofNullable(volumeFlowInputMonitorPointID).ifPresent(monitorIDList::add);
        Optional.ofNullable(volumeFlowOutputMonitorPointID).ifPresent(monitorIDList::add);
        if (monitorIDList.size() != monitorIDList.stream().distinct().toList().size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不能相同!");
        }
        List<Map<String, Object>> maps = ContextHolder.getBean(TbMonitorPointMapper.class).selectMonitorTypeExValuesByPointIDList(monitorIDList);
        if (maps.size() != monitorIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在!");
        }
        List<String> exValuesList = maps.stream().map(u -> u.get("exValues")).filter(Objects::nonNull)
                .map(String::valueOf).filter(ObjectUtil::isNotEmpty).toList();
        if (exValuesList.stream().map(JSONUtil::parseObj).anyMatch(u -> u.containsKey(DbConstant.DISPLAY_DENSITY)
                && !JSONUtil.parseArray(u.get(DbConstant.DISPLAY_DENSITY)).contains(displayDensity))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在监测点不支持选择的显示密度!");
        }
        // 中台只会用到{@code periodRainfall}属性,{@code dailyRainfall}统计的是08:00:00~08:00:00的数据
        // rainfallToken = DefaultConstant.ThematicFieldToken.getRainfallToken(DisplayDensity.fromValue(displayDensity));
        Integer rainfallMonitorType = maps.stream().filter(u -> u.get("monitorPointID").equals(rainfallMonitorPointID))
                .map(u -> u.get("monitorType")).map(Convert::toInt).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unreachable Exception"));
        rainfallToken = rainfallMonitorType.equals(MonitorType.RAINFALL.getKey()) ?
                DefaultConstant.ThematicFieldToken.RAINFALL : DefaultConstant.ThematicFieldToken.PERIOD_RAINFALL;
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
