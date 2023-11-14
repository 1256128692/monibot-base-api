package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-21 15:24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryHikVideoTalkParam extends HikVideoBaseParam implements ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;

    @Override
    public Resource parameter() {
        return Objects.nonNull(projectID) ? new Resource(projectID.toString(), ResourceType.BASE_PROJECT) : new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
