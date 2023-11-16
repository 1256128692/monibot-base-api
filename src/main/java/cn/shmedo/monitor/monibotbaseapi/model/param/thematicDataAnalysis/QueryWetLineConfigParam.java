package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 14:26
 */
@Data
public class QueryWetLineConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotNull(message = "监测点组ID不能为空!")
    @Positive(message = "监测点组ID不能小于1")
    private Integer monitorGroupID;
    @NotEmpty(message = "监测点ID列表不能为空")
    private List<Integer> monitorPointIDList;

    @Override
    public ResultWrapper<?> validate() {
        if (!ContextHolder.getBean(TbMonitorGroupMapper.class).exists(new LambdaQueryWrapper<TbMonitorGroup>()
                .eq(TbMonitorGroup::getID, monitorGroupID).eq(TbMonitorGroup::getProjectID, projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点组不存在!");
        }
        if (ContextHolder.getBean(TbMonitorGroupPointMapper.class).selectCount(new LambdaQueryWrapper<TbMonitorGroupPoint>()
                .eq(TbMonitorGroupPoint::getMonitorGroupID, monitorGroupID)
                .in(TbMonitorGroupPoint::getMonitorPointID, monitorPointIDList)) != monitorPointIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有部分监测点不属于该监测点组!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
