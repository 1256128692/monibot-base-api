package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.service.third.wt.WtReportService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
        return pa -> ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
    }
}
