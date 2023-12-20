package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: monibot-base-api
 * @author: wuxl
 * @create: 2023-02-23 17:08
 **/
@Data
@ToString
public class UpdateModelParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "模板ID不能为空")
    @JsonProperty("ID")
    private Integer ID;

    private String modelName;

    private Integer modelType;

    @Min(0)
    @Max(3)
    private Integer modelTypeSubType;

    private Integer groupID;

    @NotNull(message = "项目类型不能为空")
    private Integer projectType;

    private String desc;

    @NotEmpty
    @Valid
    private List<ModelItem> modelPropertyList;

    @JsonIgnore
    private TbPropertyModel tbPropertyModel;

    @JsonIgnore
    private List<TbProperty> tbPropertyList;

    @JsonIgnore
    private Map<Integer, String> allPropertyIdAndNameMap;

    @JsonIgnore
    private List<ModelItem> addModelItemList;

    @JsonIgnore
    private List<ModelItem> updateModelItemList;

    @JsonIgnore
    Set<Integer> removeModelItemIDSet;

    @Override
    public ResultWrapper<?> validate() {
        modelType = Objects.isNull(this.modelType) ? PropertyModelType.BASE_PROJECT.getCode() : this.modelType;
        groupID = PropertyModelType.BASE_PROJECT.getCode().equals(modelType) ? this.projectType : this.groupID;

        // 校验表单模板类型
        if (!PropertyModelType.getModelTypeValues().contains(modelType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板类型不合法");
        }
        // 如果表单模板类型为项目表单模板，需要校验项目类型
        if (PropertyModelType.BASE_PROJECT.getCode().equals(modelType) && !ProjectTypeCache.projectTypeMap.containsKey(Byte.valueOf(String.valueOf(groupID)))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        // 校验表单模板是否存在
        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        LambdaQueryWrapper<TbPropertyModel> queryWrapper = new QueryWrapper<TbPropertyModel>().lambda()
                .eq(TbPropertyModel::getID, this.ID)
                .eq(TbPropertyModel::getModelType, this.modelType)
                .eq(TbPropertyModel::getCompanyID, this.companyID);
        if (Objects.nonNull(this.modelTypeSubType)) {
            queryWrapper.eq(TbPropertyModel::getModelTypeSubType, this.modelTypeSubType);
        }
        tbPropertyModel = tbPropertyModelMapper.selectOne(queryWrapper);
        if (Objects.isNull(tbPropertyModel)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板不存在");
        }
        // 校验模板名称是否重复
        if (StringUtils.isNotEmpty(modelName)) {
            LambdaQueryWrapper<TbPropertyModel> checkNameQueryWrapper = new QueryWrapper<TbPropertyModel>().lambda()
                    .ne(TbPropertyModel::getID, this.ID)
                    .eq(TbPropertyModel::getCompanyID, this.companyID)
                    .eq(TbPropertyModel::getModelType, this.modelType)
                    .eq(Objects.nonNull(this.groupID), TbPropertyModel::getGroupID, this.groupID)
                    .eq(Objects.nonNull(this.modelTypeSubType), TbPropertyModel::getModelTypeSubType, this.modelTypeSubType)
                    .eq(TbPropertyModel::getName, this.modelName);
            if (Objects.nonNull(this.modelTypeSubType)) {
                checkNameQueryWrapper.eq(TbPropertyModel::getModelTypeSubType, this.modelTypeSubType);
            }
            TbPropertyModel checkNameTbPropertyModel = tbPropertyModelMapper.selectOne(checkNameQueryWrapper);
            if (Objects.nonNull(checkNameTbPropertyModel)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板名称不能重复");
            }
        }
        // 校验模板下属性名称是否重复（前端页面填写的名称是否重复）
        if (modelPropertyList.stream().map(ModelItem::getName).distinct().count() != modelPropertyList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性名称存在重复");
        }
        // 校验模板下属性名称是否重复（数据库已经存在的名称是否重复）
        TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
        Map<String, List<ModelItem>> recordStateMap = modelPropertyList.stream().collect(Collectors.groupingBy(pro -> {
            pro.setModelID(tbPropertyModel.getID());
            if (Objects.isNull(pro.getID())) {
                // 新增
                pro.setCreateType(0);
                pro.setGroupID(tbPropertyModel.getGroupID());
                return OperationProperty.ADD.name();
            } else {
                // 修改
                return OperationProperty.UPDATE.name();
            }
        }));

        tbPropertyList = tbPropertyMapper.selectList(new QueryWrapper<TbProperty>().lambda()
                .eq(TbProperty::getModelID, this.ID));

        // -- 模板下属性删除
        Set<Integer> updateModelItemIDSet = modelPropertyList.stream().map(ModelItem::getID).collect(Collectors.toSet());
        removeModelItemIDSet = tbPropertyList.stream().map(TbProperty::getID).filter(id -> !updateModelItemIDSet.contains(id)).collect(Collectors.toSet());

        // -- 模板下属性编辑校验
        updateModelItemList = recordStateMap.get(OperationProperty.UPDATE.name());
        allPropertyIdAndNameMap = tbPropertyList.stream().collect(Collectors.toMap(TbProperty::getID, TbProperty::getName));
        if (CollectionUtil.isNotEmpty(updateModelItemList) && CollectionUtil.isNotEmpty(tbPropertyList)) {
            // 校验模板下是否已经有相同模板名称
            for (ModelItem item : updateModelItemList) {
                // 先排除自身Name
                String value = allPropertyIdAndNameMap.remove(item.getID());
                if (allPropertyIdAndNameMap.containsValue(item.getName()) && removeModelItemIDSet.contains(item.getID())) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性名称存在重复");
                }
                // 还原自身Name
                allPropertyIdAndNameMap.put(item.getID(), value);
            }
        }

        // -- 模板下属性新增校验
        addModelItemList = recordStateMap.get(OperationProperty.ADD.name());
        if (CollectionUtil.isNotEmpty(addModelItemList)) {
            // 查询模板下是否已经有相同模板属性名称
            List<TbProperty> addTbPropertyList = tbPropertyMapper.selectList(new QueryWrapper<TbProperty>().lambda()
                    .in(TbProperty::getName, addModelItemList.stream().map(ModelItem::getName).collect(Collectors.toList()))
                    .eq(TbProperty::getModelID, this.ID)
                    .notIn(CollectionUtil.isNotEmpty(removeModelItemIDSet), TbProperty::getID, removeModelItemIDSet));
            if (CollectionUtil.isNotEmpty(addTbPropertyList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性名称存在重复");
            }
        }

        // json校验
        if (modelPropertyList.stream().anyMatch(item -> ObjectUtil.isNotEmpty(item.getExValue()) && !JSONUtil.isTypeJSON(item.getExValue()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板的属性的额外属性应为json字符串");
        }
        return (ResultWrapper<?>) PropertyUtil.validate(modelPropertyList);
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    public TbPropertyModel getTbPropertyModel() {
        TbPropertyModel newTbPropertyModel = new TbPropertyModel();
        newTbPropertyModel.setID(ID);
        newTbPropertyModel.setCompanyID(companyID);
        newTbPropertyModel.setName(modelName);
        newTbPropertyModel.setModelType(modelType);
        newTbPropertyModel.setModelTypeSubType(modelTypeSubType);
        newTbPropertyModel.setGroupID(groupID);
        newTbPropertyModel.setDesc(desc);
        return newTbPropertyModel;
    }
}
