package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:08
 **/
@Data
@ToString
public class AddModelParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "项目类型不能为空")
    private Integer projectType;

    @NotNull(message = "模板名称不能为空")
    private String modelName;

    private Integer modelType;

    @Min(0)
    @Max(3)
    private Integer modelTypeSubType;

    private Integer groupID;

    private Integer platform;

    private String desc;

    @NotEmpty
    @Valid
    private List<@NotNull ModelItem> modelPropertyList;

    @JsonIgnore
    RedisTemplate<String, String> redisTemplate = ContextHolder.getBean(RedisConstant.AUTH_REDIS_SERVICE);

    @Override
    public ResultWrapper<?> validate() {
        modelType = Objects.isNull(this.modelType) ? PropertyModelType.BASE_PROJECT.getCode() : this.modelType;
        groupID = PropertyModelType.BASE_PROJECT.getCode().equals(modelType) ? this.projectType : this.groupID;

        // 校验表单类型是否正确
        if (!PropertyModelType.getModelTypeValues().contains(modelType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型不合法");
        }

        // 如果表单模板类型为项目表单模板，需要校验项目类型
        if (PropertyModelType.BASE_PROJECT.getCode().equals(modelType) && !ProjectTypeCache.projectTypeMap.containsKey(Byte.valueOf(String.valueOf(groupID)))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }

        if (Objects.nonNull(platform) && !redisTemplate.opsForHash().hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, platform.toString())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "所属平台不合法");
        }

        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<TbPropertyModel>().lambda()
                .eq(TbPropertyModel::getCompanyID, this.companyID)
                .eq(TbPropertyModel::getModelType, this.modelType)
                .eq(TbPropertyModel::getPlatform, this.platform)
                .eq(TbPropertyModel::getGroupID, this.groupID)
                .eq(Objects.nonNull(modelTypeSubType), TbPropertyModel::getModelType, modelType)
                .eq(TbPropertyModel::getName, this.modelName));
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

}
