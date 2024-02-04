package cn.shmedo.monitor.monibotbaseapi.model.param.project;


import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDeviceCountStatisticsParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer companyID;

    @Override
    public ResultWrapper validate() {


//        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
//        LambdaQueryWrapper<TbProjectInfo> nameExistWrapper = new LambdaQueryWrapper<TbProjectInfo>()
//                .eq(TbProjectInfo::getID, this.projectID);
//        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectOne(nameExistWrapper);
//        if (!ObjectUtil.isNotNull(tbProjectInfo)) {
//            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前projectID不存在");
//        }
//        this.companyID = tbProjectInfo.getCompanyID();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
