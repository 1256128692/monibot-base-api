package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.service.third.notify.MdNotifyService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Chengfs on 2024/1/12
 */
@Slf4j
@Component
public class MdNotifyServiceFallbackFactory implements FallbackFactory<MdNotifyService> {

    @Override
    public MdNotifyService create(Throwable throwable) {
        log.error("通知服务调用失败: {}", ExceptionUtil.stacktraceToString(throwable));
        return pa -> ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
    }
}