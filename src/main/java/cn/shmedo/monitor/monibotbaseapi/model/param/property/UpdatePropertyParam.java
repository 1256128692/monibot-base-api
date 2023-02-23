package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.NameAndValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:55
 **/
public class UpdatePropertyParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;

    @NotEmpty
    @Valid
    private List< @NotNull NameAndValue> modelValueList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectByPrimaryKey(projectID);
        if (tbProjectInfo == null){
            ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        if (!tbProjectInfo.getCompanyID().equals(companyID)){
            ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不属于该公司");

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


    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public List<NameAndValue> getModelValueList() {
        return modelValueList;
    }

    public void setModelValueList(List<NameAndValue> modelValueList) {
        this.modelValueList = modelValueList;
    }
}
