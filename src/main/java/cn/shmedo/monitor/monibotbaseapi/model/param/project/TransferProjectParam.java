package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-27 17:41
 **/
public class TransferProjectParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    private Integer companyID;
    @NotNull
    Integer projectID;

    @JsonProperty()
    private Integer rowCompanyID;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectByPrimaryKey(projectID);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        if (tbProjectInfo.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目标公司与当前公司一样");
        }
        this.rowCompanyID = tbProjectInfo.getCompanyID();
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return Arrays.asList(
                new Resource(rowCompanyID.toString(), ResourceType.COMPANY.toInt()),
                new Resource(this.companyID.toString(), ResourceType.COMPANY.toInt())
        );
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getRowCompanyID() {
        return rowCompanyID;
    }

    public void setRowCompanyID(Integer rowCompanyID) {
        this.rowCompanyID = rowCompanyID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }
}
