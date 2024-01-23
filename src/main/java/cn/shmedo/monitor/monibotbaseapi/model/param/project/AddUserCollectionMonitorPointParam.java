package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AddUserCollectionMonitorPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer projectID;
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<Integer> monitorPointIDList;

    @Override
    public ResultWrapper validate() {
//        TbProjectInfoMapper projectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
//        projectInfoList = projectInfoMapper.selectList(new LambdaQueryWrapper<>(
//                new TbProjectInfo()).in(TbProjectInfo::getID, dataIDList).eq(TbProjectInfo::getCompanyID, companyID));
//        if (CollUtil.isEmpty(projectInfoList) || projectInfoList.size() != dataIDList.size()) {
//            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程项目编号列表含不存在的工程项目");
//        }

        // 需要加入校验逻辑,已经添加过收藏的不允许添加收藏

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }


}
