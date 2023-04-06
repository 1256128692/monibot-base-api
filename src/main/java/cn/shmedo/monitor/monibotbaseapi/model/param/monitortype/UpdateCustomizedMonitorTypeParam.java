package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 18:03
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCustomizedMonitorTypeParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer monitorType;
    @NotBlank
    @Size(max = 50)
    private String typeName;
    @NotBlank
    @Size(max = 50)
    private String typeAlias;
    private Boolean apiDataSource;
    @Size(max = 500)
    private String exValues;

    @JsonIgnore
    private TbMonitorType tbMonitorType;
    @Override
    public ResultWrapper validate() {
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        tbMonitorType = tbMonitorTypeMapper.selectOne(new QueryWrapper<TbMonitorType>().eq("monitorType", monitorType));
        if (tbMonitorType == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        if (tbMonitorType.getCompanyID().equals(-1)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不是自定义");
        }
        if (!tbMonitorType.getCompanyID().equals(companyID)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不属于该公司");
        }
        if (tbMonitorTypeMapper.selectCount(new QueryWrapper<TbMonitorType>().eq("typeName",typeName).ne("monitorType", monitorType))>0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型要设置的名称已经存在");
        }
        if (!StringUtils.isBlank(exValues) && JSONUtil.isTypeJSON(exValues)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型额外属性不存在");

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


    /**
     * 将要更新的项设置到校验用的tbMonitorType中并返回
     * @return
     */
    public TbMonitorType update() {
        tbMonitorType.setTypeName(typeName);
        tbMonitorType.setTypeAlias(typeAlias);
        tbMonitorType.setApiDataSource(apiDataSource);
        tbMonitorType.setExValues(exValues);
        return tbMonitorType;
    }
}
