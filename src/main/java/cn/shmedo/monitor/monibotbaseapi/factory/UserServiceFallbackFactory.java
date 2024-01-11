package cn.shmedo.monitor.monibotbaseapi.factory;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.auth.OpenAuthApplicationHasPermissionParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionInBatchResourceParameter;
import cn.shmedo.iot.entity.api.auth.OpenAuthQueryHasPermissionParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.*;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户服务远程调用 熔断降级
 *
 * @author Chengfs on 2022/11/24
 */
@Slf4j
@Component
public class UserServiceFallbackFactory implements FallbackFactory<UserService> {

    @Override
    @SuppressWarnings("unchecked")
    public UserService create(Throwable cause) {
        log.error("权限服务调用失败:{}", cause.getMessage());
        log.error(ExceptionUtil.stacktraceToString(cause));
        return new UserService() {

            @Override
            public ResultWrapper<CurrentSubject> getCurrentSubject(String accessToken) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<CurrentSubject> getCurrentSubjectByApp(String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<Boolean> applicationHasPermission(OpenAuthApplicationHasPermissionParameter pa,
                                                                   String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<Boolean> queryHasPermission(OpenAuthQueryHasPermissionParameter pa,
                                                             String accessToken) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<Boolean> queryHasPermissionInBatchResource(
                    OpenAuthQueryHasPermissionInBatchResourceParameter pa, String accessToken) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<UserIDName>> queryUserIDName(QueryUserIDNameParameter pa, String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<CompanyIDAndNameV2>> listCompanyIDName(CompanyIDListParam pa, String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<DeptSimpleInfo>> queryDeptSimpleList(QueryDeptSimpleListParam pa, String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }

            @Override
            public ResultWrapper<List<UserNoPageInfo>> queryUserNoPage(QueryUserNoPageParam pa, String appKey, String appSecret) {
                return ResultWrapper.withCode(ResultCode.THIRD_PARTY_SERVICE_INVOKE_ERROR);
            }
        };
    }
}

    
    