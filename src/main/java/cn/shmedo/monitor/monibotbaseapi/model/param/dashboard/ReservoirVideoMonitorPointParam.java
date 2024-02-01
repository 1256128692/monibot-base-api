package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 17:29
 **/
@Data
public class ReservoirVideoMonitorPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    private Integer projectID;
    @JsonIgnore
    private TbProjectInfo tbProjectInfo;

    @Override
    public ResultWrapper validate() {
        if (projectID != null) {
            TbProjectInfoMapper tbProjectInfoMapper = SpringUtil.getBean(TbProjectInfoMapper.class);
            tbProjectInfo = tbProjectInfoMapper.selectOne(
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
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
