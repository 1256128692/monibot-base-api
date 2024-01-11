package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 11:27
 */
@Data
public class CompanyPlatformParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;

    @SuppressWarnings("unchecked")
    @Override
    public ResultWrapper<?> validate() {
        RedisTemplate<String, String> redisTemplate = ContextHolder.getBean(RedisTemplate.class);
        if (!redisTemplate.opsForHash().hasKey(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE, platform.toString())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在!");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
