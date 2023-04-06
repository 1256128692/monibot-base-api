package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 17:43
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryMonitorTypeFieldWithFormulaParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer monitorType;
    @NotNull
    private Integer templateID;
    @Override
    public ResultWrapper validate() {
        TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper = ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
        TbMonitorTypeTemplate tbMonitorTypeTemplate = tbMonitorTypeTemplateMapper.selectByPrimaryKey(templateID);
        if (tbMonitorTypeTemplate == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"模板不存在");
        }
        if (!tbMonitorTypeTemplate.getMonitorType().equals(monitorType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"模板不属于监测类型");
        }
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.selectOne(new QueryWrapper<TbMonitorType>().eq("MonitorType", monitorType));
        if (tbMonitorType == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"监测类型不存在");
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
