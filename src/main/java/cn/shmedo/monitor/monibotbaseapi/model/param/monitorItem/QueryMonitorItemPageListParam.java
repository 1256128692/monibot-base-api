package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-10 17:36
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryMonitorItemPageListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    private Integer companyID;
    private Integer projectID;
    private Integer monitorType;
    private Integer monitorItemID;
    private Byte createType;

    private String queryCode;
    private Boolean companyItem;

    @NotNull
    @Positive
    @Max(100)
    private Integer pageSize;
    @NotNull
    @Positive
    private Integer currentPage;


    @Override
    public ResultWrapper validate() {
        if (projectID != null && projectID != -1) {
            TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
            var tbProjectInfo = tbProjectInfoMapper.selectByPrimaryKey(projectID);
            if (tbProjectInfo == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程项目不存在");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        CurrentSubject currentSubject = CurrentSubjectHolder.getCurrentSubject();
        return new Resource(currentSubject.getCompanyID().toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
