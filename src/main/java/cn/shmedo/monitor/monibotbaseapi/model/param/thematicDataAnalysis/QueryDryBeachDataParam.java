package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
    @NotNull(message = "降雨量监测点ID不能为空")
    private Integer rainfallMonitorPointID;
    @JsonIgnore
    List<Map<String, Object>> dataList;

    @Override
    public ResultWrapper validate() {
        dataList = ContextHolder.getBean(TbMonitorPointMapper.class)
                .selectMonitorTypeExValuesByPointIDList(List.of(dryBeachMonitorPointID, distanceMonitorPointID, rainfallMonitorPointID));
        if (dataList.size() != 3) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
