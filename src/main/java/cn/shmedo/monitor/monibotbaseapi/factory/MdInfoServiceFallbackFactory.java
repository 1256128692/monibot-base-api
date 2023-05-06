package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.*;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Chengfs on 2023/4/13
 */
@Component
@Slf4j
public class MdInfoServiceFallbackFactory implements FallbackFactory<MdInfoService> {
    @Override
    public MdInfoService create(Throwable cause) {
        log.error("信息化服务调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));
        return new MdInfoService() {
            @Override
            public ResultWrapper<FilePathResponse> addFileUpload(AddFileUploadRequest addFileUploadRequest) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<FileInfoResponse> queryFileInfo(QueryFileInfoRequest pojo) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<FileInfoResponse>> queryFileListInfo(QueryFileListInfoRequest pojo) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }
        };
    }
}

    
    