package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
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
        // 对于已经设置模板的监测类型，不可删除
        TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper = ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
        if (tbMonitorTypeTemplateMapper.selectCount(
                new QueryWrapper<TbMonitorTypeTemplate>().eq("monitorType", monitorType)
        ) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测类型已经设置模板，不可删除");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}

