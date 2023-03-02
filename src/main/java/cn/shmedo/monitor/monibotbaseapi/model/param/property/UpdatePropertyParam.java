package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.PredefinedModelProperTyCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.IDAndValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private List<@NotNull IDAndValue> modelValueList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbProjectInfo tbProjectInfo = tbProjectInfoMapper.selectByPrimaryKey(projectID);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        if (!tbProjectInfo.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不属于该公司");
        }
        List<TbProperty> properties = PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(tbProjectInfo.getProjectType());
        if (tbProjectInfo.getModelID() != null) {
            TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
            properties.addAll(tbPropertyMapper.queryByMID(tbProjectInfo.getModelID()));
        }
        Map<Integer, IDAndValue> idAndValueMap = modelValueList.stream().collect(Collectors.toMap(IDAndValue::getpID, Function.identity()));
        // 校验必填
        boolean b2 = properties.stream().filter(item ->!item.getRequired())
                .anyMatch(item -> {
                    IDAndValue temp = idAndValueMap.get(item.getID());
                    if (temp != null && ObjectUtil.isEmpty(temp.getValue())) {
                        return true;
                    }
                    return false;
                });
        // 校验枚举
        boolean b1 = properties.stream().filter(item -> item.getType().equals(PropertyType.TYPE_ENUM.getType()))
                .anyMatch(item -> {
                    IDAndValue temp = idAndValueMap.get(item.getID());
                    if (temp != null) {
                        JSONArray enums = JSONUtil.parseArray(item.getEnumField());
                        if (!enums.contains(temp.getValue())) {
                            return true;
                        }
                    }
                    return false;
                });
        if (b2 || b1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "枚举类型的属性值非法或必填项未填入");
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

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public List<IDAndValue> getModelValueList() {
        return modelValueList;
    }

    public void setModelValueList(List<IDAndValue> modelValueList) {
        this.modelValueList = modelValueList;
    }
}
