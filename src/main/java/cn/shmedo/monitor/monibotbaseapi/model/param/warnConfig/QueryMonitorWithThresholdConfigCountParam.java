package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 16:06
 */
@Data
public class QueryMonitorWithThresholdConfigCountParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID必须为正值")
    private Integer projectID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;

    @Override
    public ResultWrapper<?> validate() {
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
