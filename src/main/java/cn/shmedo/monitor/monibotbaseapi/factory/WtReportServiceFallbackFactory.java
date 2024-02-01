package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryMaxPeriodRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryMaxPeriodResponse;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryReservoirResponsibleListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.wt.QueryReservoirResponsibleListResponseItem;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.wt.WtReportService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 16:23
 */
@Component
@Slf4j
public class WtReportServiceFallbackFactory implements FallbackFactory<WtReportService> {
    @Override
    public WtReportService create(Throwable cause) {
        log.error("信息化服务调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));
        return new WtReportService() {

            @Override
            public ResultWrapper<QueryMaxPeriodResponse> queryMaxPeriod(QueryMaxPeriodRequest pa) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<QueryReservoirResponsibleListResponseItem>> queryReservoirResponsibleList(QueryReservoirResponsibleListRequest pa) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }
        };
    }
}
