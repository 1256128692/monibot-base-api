package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.ToString;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:08
 **/
@ToString
public class AddModelParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "模板名称不能为空")
    private String modelName;

    @NotNull(message = "模板类型不能为空")
    private Integer modelType;

    @Min(0)
    @Max(3)
    private Integer modelTypeSubType;

    private Integer groupID;

    @NotBlank(message = "所属平台不能为空")

    private String platform;

    private String desc;

    @NotEmpty
    @Valid
    private List<@NotNull ModelItem> modelPropertyList;

    @Override
    public ResultWrapper<?> validate() {
        // 校验表单类型是否正确
        if(!PropertyModelType.getModelTypeValues().contains(modelType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型不合法");
        }

        // 如果表单模板类型为项目表单模板，需要校验项目类型
        if (PropertyModelType.BASE_PROJECT.getCode().equals(modelType) && !ProjectTypeCache.projectTypeMap.containsKey(Byte.valueOf(String.valueOf(groupID)))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }

        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<TbPropertyModel>().lambda()
                .eq(TbPropertyModel::getCompanyID, this.companyID)
                .eq(TbPropertyModel::getModelType, this.modelType)
                .eq(TbPropertyModel::getPlatform, this.platform)
                .eq(TbPropertyModel::getGroupID, this.groupID));
        if (!CollectionUtil.isNullOrEmpty(tbPropertyModelList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的名称已存在");
        }

        if (modelPropertyList.stream().map(ModelItem::getName).distinct().count() != modelPropertyList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性名称存在重复");
        }

        if (modelPropertyList.stream().anyMatch(item -> ObjectUtil.isNotEmpty(item.getExValue()) && !JSONUtil.isTypeJSON(item.getExValue()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性的额外属性应为json字符串");
        }

        ResultWrapper<?> temp = PropertyUtil.validate(modelPropertyList);
        if (temp != null) {
            return temp;
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


    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
    }

    public Integer getModelTypeSubType() {
        return modelTypeSubType;
    }

    public void setModelTypeSubType(Integer modelTypeSubType) {
        this.modelTypeSubType = modelTypeSubType;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ModelItem> getModelPropertyList() {
        return modelPropertyList;
    }

    public void setModelPropertyList(List<ModelItem> modelPropertyList) {
        this.modelPropertyList = modelPropertyList;
    }

}
