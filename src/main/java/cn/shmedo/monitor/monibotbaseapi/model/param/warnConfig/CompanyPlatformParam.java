package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

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

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 11:27
 */
@Data
public class CompanyPlatformParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;
    @Positive
    private Integer projectID;

    @Override
    public ResultWrapper<?> validate() {
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        if (Objects.nonNull(projectID) && !ContextHolder.getBean(TbProjectInfoMapper.class)
                .exists(new LambdaQueryWrapper<TbProjectInfo>().eq(TbProjectInfo::getID, projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程不存在!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
