package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.ParameterSubjectType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.DataUnitCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.MonitorTypeFieldConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbParameterMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 14:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetParamParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NonNull
    private Integer companyID;
    @NonNull
    private Integer subjectType;
    private Boolean deleteOnly;
    @Valid
    @NotEmpty
    @Size(max = 100)
    private List<@NonNull ParamItem> paramList;

    @Override
    public ResultWrapper validate() {
        if (ParameterSubjectType.codeOf(subjectType) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数类型有误");
        }
        if (deleteOnly != null && deleteOnly) {
            if (paramList.stream().allMatch(item -> item.getID() == null)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "删除请提供ID");
            }
        }
        if (paramList.stream().anyMatch(item -> !MonitorTypeFieldConfig.DataTypeList.contains(item.getDataType()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数的数据类型不合法");
        }
        if (paramList.stream().anyMatch(item -> !DataUnitCache.dataUnitsMap.containsKey(item.getPaUnitID()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数的数据单位不合法");
        }
        //  唯一性校验
        TbParameterMapper tbParameterMapper = ContextHolder.getBean(TbParameterMapper.class);
        QueryWrapper<TbParameter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("subjectType", subjectType);
        List<Integer> temp = paramList.stream().map(ParamItem::getID).filter(Objects::nonNull).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(temp)) {
            queryWrapper.notIn("id", temp);
        }
        paramList.forEach(item -> {
            queryWrapper.eq("subjectID", item.getSubjectID()).eq("token", item.getToken()).or();
        });
        if (tbParameterMapper.selectCount(queryWrapper) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数已经存在");
        }
        if (subjectType.equals(ParameterSubjectType.TEMPLATE.getCode())) {
            // 模板时候
            Set<Integer> subIDList = paramList.stream().map(ParamItem::getSubjectID).collect(Collectors.toSet());
            TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper = ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
            if (tbMonitorTypeTemplateMapper.selectCount(new QueryWrapper<TbMonitorTypeTemplate>().in("ID", subIDList)) != subIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有模板不存在");
            }
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
