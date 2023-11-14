package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryYsVideoDeviceInfoParam extends YsVideoBaseParam implements ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;

    @Override
    public Resource parameter() {
        return Objects.isNull(projectID) ? new Resource(companyID.toString(), ResourceType.COMPANY) : new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
