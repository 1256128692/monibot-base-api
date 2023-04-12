package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryFormulaParamsRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {
    /**
     * 公司ID
     */
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    /**
     * 监测类型模板ID
     */
    @NotNull(message = "监测类型模板ID不能为空")
    private Integer templateID;

    @JsonIgnore
    private TbMonitorTypeTemplate monitorTypeTemplate;

    @Override
    public ResultWrapper<?> validate() {
        TbMonitorTypeTemplateMapper monitorTypeTemplateMapper = ContextHolder.getBean(TbMonitorTypeTemplateMapper.class);
        monitorTypeTemplate = monitorTypeTemplateMapper.selectById(templateID);
        if (monitorTypeTemplate == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"监测类型模板不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
