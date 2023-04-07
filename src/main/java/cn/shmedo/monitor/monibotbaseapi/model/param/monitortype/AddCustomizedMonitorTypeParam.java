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
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorTypeFieldClass;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-28 15:06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCustomizedMonitorTypeParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @Min(20001)
    private Integer monitorType;
    @NotBlank
    @Size(max = 50)
    private String typeName;
    @Size(max = 50)
    private String typeAlias;
    @NotNull
    private Boolean multiSensor;
    @NotNull
    private Boolean apiDataSource;
    @Size(max = 500)
    private String exValues;
    @Size(max = 50)
    private String monitorTypeClass;
    @NotEmpty
    @Valid
    @Size(max = 50)
    private List<@NotNull MonitorTypeField4Param> fieldList;
    @Override
    public ResultWrapper validate() {

        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);

        QueryWrapper<TbMonitorType> wrapper = new QueryWrapper<>();
        wrapper.eq("typeName", typeName);
        wrapper.eq("companyID", companyID).or().eq("companyID",-1).eq("typeName", typeName);
        if (tbMonitorTypeMapper.selectCount(wrapper) > 0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型名字已存在");
        }
        if (monitorType != null){
            wrapper.clear();
            if (tbMonitorTypeMapper.selectCount(wrapper.eq("monitorType", monitorType))>0){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型已存在");
            }
        }
        if (!StringUtils.isBlank(exValues)&&JSONUtil.isTypeJSON(exValues)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型额外属性不合法");
        }
        if (fieldList.stream().anyMatch(item -> !MonitorTypeFieldConfig.DataTypeList.contains(item.getFieldDataType()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性数据类型不合法");
        }
        if (fieldList.stream().anyMatch(item -> !MonitorTypeFieldClass.isValid(item.getFieldClass()))){
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
