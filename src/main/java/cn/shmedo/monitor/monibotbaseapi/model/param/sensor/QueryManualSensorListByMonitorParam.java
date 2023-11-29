package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-17 14:32
 */
@Data
public class QueryManualSensorListByMonitorParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotNull(message = "监测类型不能为空!")
    @Positive(message = "监测类型不能小于1")
    private Integer monitorType;

    @Override
    public ResultWrapper<?> validate() {
        if (!ContextHolder.getBean(TbMonitorTypeMapper.class).exists(new LambdaQueryWrapper<TbMonitorType>().eq(TbMonitorType::getMonitorType, monitorType))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
