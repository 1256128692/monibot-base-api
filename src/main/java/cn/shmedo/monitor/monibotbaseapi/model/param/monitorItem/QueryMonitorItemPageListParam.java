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
    private Integer projectID;
    private String monitorItemName;
    private Integer monitorType;
    private Byte createType;

    private String fieldToken;
    private String fieldName;
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
        if (projectID!= null){
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
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
