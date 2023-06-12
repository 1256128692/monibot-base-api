package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.service.third.mdinfo.MdInfoFileService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Chengfs on 2023/4/13
 */
@Component
@Slf4j
public class MdInfoFileServiceFallbackFactory implements FallbackFactory<MdInfoFileService> {
    @Override
    public MdInfoFileService create(Throwable cause) {
        log.error("信息化服务文件上传调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));
        return (file, companyID, bucketName, fileName, fileType, fileSecret, fileDesc, userID, folderID, exValue) -> ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
    }
}

    
    