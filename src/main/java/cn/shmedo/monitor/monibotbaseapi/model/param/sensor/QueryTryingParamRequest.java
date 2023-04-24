package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeTemplateCacheData;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * 获取试运行参数 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class QueryTryingParamRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 监测类型
     */
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    /**
     * 监测类型模板ID
     */
    @NotNull(message = "监测类型模板ID不能为空")
    private Integer templateID;

    /**
     * 监测类型模板
     */
    @JsonIgnore
    private MonitorTypeTemplateCacheData typeTemplateCache;

    /**
     * 监测类型字段
     */
    @JsonIgnore
    private MonitorTypeCacheData monitorTypeCache;

    @Override
    public ResultWrapper<?> validate() {
        RedisService redisService = SpringUtil.getBean(RedisService.class);

        this.monitorTypeCache = redisService.get(RedisKeys.MONITOR_TYPE_KEY,
                monitorType.toString(), MonitorTypeCacheData.class);
        Assert.notNull(monitorTypeCache, "监测类型不存在");

        this.typeTemplateCache = redisService.get(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY,
                templateID.toString(), MonitorTypeTemplateCacheData.class);
        Assert.notNull(typeTemplateCache, "监测类型模板不存在");

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    