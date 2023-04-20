package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-19 14:29
 */
@Data
public class QueryWorkOrderStatisticsParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @Min(value = 1, message = "工程项目ID不能小于1")
    private Integer projectID;

    @Override
    public ResultWrapper validate() {
        if (projectID != null) {
            TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
            if (tbProjectInfoMapper.selectCount(new LambdaQueryWrapper<TbProjectInfo>().eq(TbProjectInfo::getID, projectID)) < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
            }
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
