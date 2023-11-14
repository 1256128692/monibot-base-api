package cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.workflow.SearchWorkFlowTemplateListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.workflow.DescribeWorkFlowTemplateResponse;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

/**
 * @Author wuxl
 * @Date 2023/10/24 15:53
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo
 * @ClassName: WorkFlowTemplateService
 * @Description: TODO
 * @Version 1.0
 */
public interface WorkFlowTemplateService {
    @RequestLine("POST /SearchWorkFlowTemplateList")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<List<DescribeWorkFlowTemplateResponse>> searchWorkFlowTemplateList(SearchWorkFlowTemplateListParam param);
}
