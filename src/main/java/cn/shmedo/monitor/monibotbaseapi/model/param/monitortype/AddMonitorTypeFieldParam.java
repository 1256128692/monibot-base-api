package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.DataUnitCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.MonitorTypeFieldConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 19:27
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMonitorTypeFieldParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer monitorType;
    @NotEmpty
    @Valid
    @Size(max = 50)
    private List<@NotNull MonitorTypeField4Param> fieldList;
    @Override
    public ResultWrapper validate() {
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.selectOne(new QueryWrapper<TbMonitorType>().eq("monitorType", monitorType));
        if (tbMonitorType == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        if (CreateType.PREDEFINED.getType().equals(tbMonitorType.getCreateType())){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "预定义监测类型不允许添加属性");
        }
        if (!tbMonitorType.getCompanyID().equals(companyID)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不属于该公司");
        }

        if (fieldList.stream().anyMatch(item -> !MonitorTypeFieldConfig.DataTypeList.contains(item.getFieldDataType()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性数据类型不合法");
        }
        if (fieldList.stream().anyMatch(item -> FieldClass.codeOf(item.getFieldClass()) == null)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性类型不合法");
        }
        if (fieldList.stream().anyMatch(item -> !CreateType.isValid(item.getCreateType().byteValue()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性创建类型不合法");
        }
        if (fieldList.stream().anyMatch(item -> !StringUtils.isBlank(item.getExValues())&&!JSONUtil.isTypeJSON(item.getExValues()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性的额外属性不合法");
        }
        if (fieldList.stream().anyMatch(item -> !DataUnitCache.dataUnitsMap.containsKey(item.getFieldUnitID()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性单位不合法");
        }
        if (fieldList.stream().map(MonitorTypeField4Param::getFieldToken).distinct().count()!=fieldList.size()){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新增的属性标识中存在重复");
        }
        if (fieldList.stream().map(MonitorTypeField4Param::getFieldToken).distinct().count()!=fieldList.size()){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新增的属性名称中存在重复");
        }
        TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
        QueryWrapper<TbMonitorTypeField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("monitorType", monitorType)
                .in("fieldToken",fieldList.stream().map(MonitorTypeField4Param::getFieldToken).collect(Collectors.toList()));
        if (tbMonitorTypeFieldMapper.selectCount(queryWrapper) > 0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新增的属性标识已存在");
        }
        queryWrapper.clear();
        queryWrapper.eq("monitorType", monitorType)
                .in("fieldToken",fieldList.stream().map(MonitorTypeField4Param::getFieldName).collect(Collectors.toList()));
        if (tbMonitorTypeFieldMapper.selectCount(queryWrapper) > 0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新增的属性名称已存在");
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
