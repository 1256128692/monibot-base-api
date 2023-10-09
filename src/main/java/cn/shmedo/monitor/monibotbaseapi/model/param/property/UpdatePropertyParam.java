package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.PredefinedModelProperTyCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 16:55
 **/
@Data
@ToString
public class UpdatePropertyParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;

    @NotNull
    private Integer projectID;

    private Integer modelType;

    @NotEmpty
    @Valid
    private List<@NotNull PropertyIdAndValue> modelValueList;

    @JsonIgnore
    private List<TbProperty> properties;
    @Override
    public ResultWrapper<?> validate() {
        modelType = Objects.isNull(this.modelType) ? PropertyModelType.BASE_PROJECT.getCode() : this.modelType;

        if(!PropertyModelType.BASE_PROJECT.getCode().equals(modelType))
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型除工程项目外，其他模板类型功能暂未开放");
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectById(projectID);

        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        if (!tbProjectInfo.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不属于该公司");
        }
        properties = new ArrayList<>(PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(tbProjectInfo.getProjectType()));
        if (tbProjectInfo.getModelID() != null) {
            TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
            properties.addAll(tbPropertyMapper.queryByMID(tbProjectInfo.getModelID()));
        }
        ResultWrapper<?> temp = PropertyUtil.validPropertyValue(modelValueList, properties, false);
        if (temp!=null){
            return temp;
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
