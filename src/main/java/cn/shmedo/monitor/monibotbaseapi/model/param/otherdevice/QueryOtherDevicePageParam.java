package cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 13:32
 **/
@Data
public class QueryOtherDevicePageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    private Integer projectID;
    private Integer templateID;
    private String fuzzyItem;
    @NotNull
    @Positive
    private Integer currentPage;
    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;

    @Override
    public ResultWrapper validate() {
        if (projectID != null) {
            TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
            if (tbProjectInfoMapper.selectById(projectID) == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
            }
        }
        if (templateID != null) {
            TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
            if (tbPropertyModelMapper.selectById(templateID) == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板不存在");
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
