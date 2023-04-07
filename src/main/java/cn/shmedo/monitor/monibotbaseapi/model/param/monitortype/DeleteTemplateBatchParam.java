package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
 * @create: 2023-03-30 19:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteTemplateBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(max = 10)
    @Valid
    private List<@NotNull Integer> templateIDList;
    @Override
    public ResultWrapper validate() {
        TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper  =ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
        List<TbMonitorTypeTemplate> tbMonitorTypeTemplates = tbMonitorTypeTemplateMapper.selectBatchIds(templateIDList);
        if (tbMonitorTypeTemplates.stream().anyMatch(item -> item.getCreateType().equals(CreateType.PREDEFINED.getType()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"预定义模板不可删除");
        }
        if (tbMonitorTypeTemplates.stream().anyMatch(item -> !item.getCompanyID().equals(companyID))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"有模板不属于该公司");
        }
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        if (tbSensorMapper.selectCount(new QueryWrapper<TbSensor>().in("templateID", templateIDList))>0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"模板已有传感器使用，不可删除");
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
