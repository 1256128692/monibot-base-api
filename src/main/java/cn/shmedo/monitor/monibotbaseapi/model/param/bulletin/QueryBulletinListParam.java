package cn.shmedo.monitor.monibotbaseapi.model.param.bulletin;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IPlatformCheck;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 15:57
 */
@Data
public class QueryBulletinListParam implements ParameterValidator, ResourcePermissionProvider<Resource>, IPlatformCheck {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "平台key不能为空")
    @Positive(message = "平台key必须为正值")
    private Integer platform;
    @Positive(message = "公告数量限制必须为正值")
    private Integer size;
    @Range(max = 1, message = "发布状态 0.未发布 1.已发布")
    private Integer status;

    @Override
    public ResultWrapper<?> validate() {
        if (!validPlatform()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台不存在");
        }
        if (DefaultConstant.IGNORE_SERVICE_LIMIT_ID_SET.contains(platform)) {
            this.platform = null;
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
