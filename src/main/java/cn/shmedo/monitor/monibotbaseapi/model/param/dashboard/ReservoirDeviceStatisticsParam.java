package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-01 10:15
 **/
@Data
public class ReservoirDeviceStatisticsParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    private Integer projectID;

    @Override
    public ResultWrapper validate() {
        if (projectID != null) {
            TbProjectInfoMapper tbProjectInfoMapper = SpringUtil.getBean(TbProjectInfoMapper.class);
            TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectOne(
                    new LambdaQueryWrapper<TbProjectInfo>()
                            .eq(TbProjectInfo::getID, projectID)
                            .eq(TbProjectInfo::getProjectType, ProjectType.RESERVOIR.getCode())
            );
            if (tbProjectInfo == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在或不是水库项目");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
