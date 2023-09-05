package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-04 14:14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryHikVideoDeviceInfoParam extends HikVideoBaseParam implements ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private final Integer companyID;
    @Positive(message = "工程ID不能小于1")
    private final Integer projectID;
    @Range(max = 2, message = "码流类型，0.主码流 1.子码流 2.第三码流 (默认为0.主码流)")
    private Integer streamType;

    @Override
    public ResultWrapper validate() {
        this.streamType = Objects.isNull(this.streamType) ? 0 : this.streamType;
        return super.validate();
    }

    @Override
    public Resource parameter() {
        return Objects.isNull(projectID) ? new Resource(companyID.toString(), ResourceType.COMPANY) : new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
