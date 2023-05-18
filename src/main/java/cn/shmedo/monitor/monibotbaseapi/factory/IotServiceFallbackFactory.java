package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.*;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Iot服务远程调用 熔断降级
 *
 * @author Chengfs on 2023/4/6
 */
@Slf4j
@Component
public class IotServiceFallbackFactory implements FallbackFactory<IotService> {
    @Override
    public IotService create(Throwable cause) {
        log.error("物联网服务调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));

        return new IotService() {
            @Override
            public ResultWrapper<Map<String, List<ModelField>>> queryModelFieldBatch(QueryModelFieldBatchParam pojo,
                                                                                     String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<DeviceWithSensor>> queryDeviceAndSensorList(QueryDeviceAndSensorRequest request) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<DeviceBaseInfo>> queryDeviceBaseInfo(QueryDeviceBaseInfoParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<SimpleDeviceV5>> queryDeviceSimpleBySenderAddress(QueryDeviceSimpleBySenderAddressParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<ProductBaseInfo>> queryByProductIDList(QueryByProductIDListParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<DeviceStateResponse> queryDeviceState(QueryDeviceStateParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<DeviceStateResponse> queryDeviceExpandState(QueryDeviceStateParam param) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }
        };
    }

}

    
    