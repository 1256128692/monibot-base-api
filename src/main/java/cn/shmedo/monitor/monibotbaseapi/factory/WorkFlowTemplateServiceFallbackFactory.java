package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.DescribeWorkFlowTemplateParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.SearchWorkFlowTemplateListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.StartWorkFlowTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow.DescribeWorkFlowTemplateResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow.DescribeWorkFlowTemplateResponseV2;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.WorkFlowTemplateService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Chengfs on 2023/4/13
 */
@Component
@Slf4j
public class WorkFlowTemplateServiceFallbackFactory implements FallbackFactory<WorkFlowTemplateService> {
    @Override
    public WorkFlowTemplateService create(Throwable cause) {
        log.error("信息化服务调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));
        return new WorkFlowTemplateService() {
            @Override
            public ResultWrapper<List<DescribeWorkFlowTemplateResponse>> searchWorkFlowTemplateList(SearchWorkFlowTemplateListParam param) {
                return null;
            }

            @Override
            public ResultWrapper<DescribeWorkFlowTemplateResponseV2> describeWorkFlowTemplate(DescribeWorkFlowTemplateParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<Integer> startWorkFlowTask(StartWorkFlowTaskParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }
        };
    }
}

    
    