package cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2023/9/15 17:14
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup
 * @ClassName: AddPropertyModelGroup
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class DeletePropertyModelGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotEmpty(message = "主键ID集合不能为空")
    @JsonProperty("IDList")
    private List<Integer> IDList;

    @Override
    public ResultWrapper<?> validate() {
        // 针对工程项目类型，如果其分组下存在模板，提示不允许删除
        TbPropertyModelGroupMapper tbPropertyModelGroupMapper = ContextHolder.getBean(TbPropertyModelGroupMapper.class);
        List<TbPropertyModelGroup> tbPropertyModelGroupList = tbPropertyModelGroupMapper.selectList(new QueryWrapper<TbPropertyModelGroup>().lambda()
                .eq(TbPropertyModelGroup::getCompanyID, companyID)
                .in(TbPropertyModelGroup::getID, IDList));
        if (CollectionUtil.isEmpty(tbPropertyModelGroupList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在错误的模板组ID");
        }
        Map<String, List<TbPropertyModelGroup>> groupMap = tbPropertyModelGroupList.stream().collect(Collectors.groupingBy(group
                -> group.getGroupType() + "_" + group.getGroupTypeSubType() + "_" + group.getPlatform() + "_" + group.getID()));

        TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
        List<TbPropertyModel> tbPropertyModelList = tbPropertyModelMapper.selectList(new QueryWrapper<TbPropertyModel>().lambda()
                .in(TbPropertyModel::getGroupID, IDList));
        if (CollectionUtil.isEmpty(tbPropertyModelList)) {
            return null;
        }
        Map<String, List<TbPropertyModel>> modelMap = tbPropertyModelList.stream().collect(Collectors.groupingBy(model
                -> model.getModelType() + "_" + model.getModelTypeSubType() + "_" + model.getPlatform() + "_" + model.getGroupID()));

        // 取集合交集
        List<String> maps = groupMap.keySet().stream().filter(group -> modelMap.keySet().stream().allMatch(model -> model.equals(group))).toList();
        if (CollectionUtil.isNotEmpty(maps)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板组下存在模板，不允许删除");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
