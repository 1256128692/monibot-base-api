package cn.shmedo.monitor.monibotbaseapi.service.third.wt;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryMaxPeriodRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryMaxPeriodResponse;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 16:23
 */
public interface WtReportService {
    @RequestLine("POST /QueryMaxPeriod")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<QueryMaxPeriodResponse> queryMaxPeriod(QueryMaxPeriodRequest pa, @Param("appKey") String appKey,
                                                         @Param("appSecret") String appSecret);
}
