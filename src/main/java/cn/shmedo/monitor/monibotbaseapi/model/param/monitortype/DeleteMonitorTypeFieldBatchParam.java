package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
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
 * @create: 2023-03-30 20:05
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMonitorTypeFieldBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer monitorType;
    @NotEmpty
    @Size(max = 10)
    @Valid
    private List<@NotNull Integer> fieldIDList;

    @Override
    public ResultWrapper validate() {
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.selectOne(new QueryWrapper<TbMonitorType>().eq("monitorType", monitorType));
        if (tbMonitorType == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        if (tbMonitorType.getCompanyID().equals(-1)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不是自定义");
        }
        if (!tbMonitorType.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不属于该公司");
        }
        // 如果包含非3类的field，对于已经设置模板的监测类型，不可删除
        TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
        List<TbMonitorTypeField> tbMonitorTypeFields = tbMonitorTypeFieldMapper.selectBatchIds(fieldIDList);
        if (tbMonitorTypeFields.stream().anyMatch(item -> !item.getFieldClass().equals(FieldClass.EXTEND_CONFIG.getCode()))){
            TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper = ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
            if (tbMonitorTypeTemplateMapper.selectCount(
                    new QueryWrapper<TbMonitorTypeTemplate>().eq("monitorType", monitorType)
            ) > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测类型下已关联数据源，属性不予删除!");
            }
        }
        // 校验不能全部删除完
        if (tbMonitorTypeFieldMapper.selectCount(
                new QueryWrapper<TbMonitorTypeField>().notIn("id",fieldIDList).eq("monitorType", monitorType)) <1){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "请至少保留一个属性");
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

