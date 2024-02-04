package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDeviceWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.DescribeWorkFlowTemplateParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.SearchWorkFlowTemplateListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow.DescribeWorkFlowTemplateResponseV2;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.WorkFlowTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-22 10:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddWarnWorkFlowTaskParam extends WarnHandleParam {
    @Positive(message = "工程ID为正值")
    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @Positive(message = "模板ID为正值")
    @NotNull(message = "模板ID不能为空")
    private Integer templateID;
    @JsonIgnore
    private String startBpmnNodeID;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        Integer companyID = getCompanyID();
        if (!ContextHolder.getBean(TbProjectInfoMapper.class).exists(new LambdaQueryWrapper<TbProjectInfo>().eq(TbProjectInfo::getID, projectID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程不存在");
        }
        SearchWorkFlowTemplateListParam param = new SearchWorkFlowTemplateListParam();
        param.setCompanyID(companyID);
        param.setFormModelIDList(List.of(templateID));
        WorkFlowTemplateService workFlowTemplateService = ContextHolder.getBean(WorkFlowTemplateService.class);
        try {
            startBpmnNodeID = Optional.of(new DescribeWorkFlowTemplateParam(companyID, templateID))
                    .map(workFlowTemplateService::describeWorkFlowTemplate).filter(ResultWrapper::apiSuccess)
                    .map(ResultWrapper::getData).map(DescribeWorkFlowTemplateResponseV2::getStartBpmnNodeID).orElseThrow();
        } catch (NoSuchElementException e) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板ID不合法");
        }
        return null;
    }
}
