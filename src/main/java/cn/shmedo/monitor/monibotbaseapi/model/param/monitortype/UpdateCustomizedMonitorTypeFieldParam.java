package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.DataUnitCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.MonitorTypeFieldConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
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

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 19:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomizedMonitorTypeFieldParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer monitorType;
    @NotEmpty
    @Valid
    @Size(max = 10)
    private List<@NotNull UpdateFieldItem> fieldList;
    @Override
    public ResultWrapper validate() {
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
       TbMonitorType tbMonitorType = tbMonitorTypeMapper.selectOne(new QueryWrapper<TbMonitorType>().eq("monitorType", monitorType));
        if (tbMonitorType == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        if (tbMonitorType.getCompanyID().equals(-1)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不是自定义");
        }
        if (!tbMonitorType.getCompanyID().equals(companyID)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不属于该公司");
        }
        if (fieldList.stream().anyMatch(item -> !MonitorTypeFieldConfig.DataTypeList.contains(item.getFieldDataType()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性数据类型不合法");
        }
        if (fieldList.stream().anyMatch(item -> !StringUtils.isBlank(item.getExValues()) && !JSONUtil.isTypeJSON(item.getExValues()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性的额外属性不合法");
        }
        if (fieldList.stream().anyMatch(item -> item.getFieldUnitID() != null && !DataUnitCache.dataUnitsMap.containsKey(item.getFieldUnitID()))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性单位不合法");
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
