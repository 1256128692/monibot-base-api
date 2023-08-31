package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:14
 */
@Data
public class QueryYsVideoDeviceInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private final Integer companyID;
    @NotNull(message = "视频设备ID不能为空")
    @Positive(message = "视频设备ID不能小于1")
    private final Integer deviceVideoID;
    @Positive(message = "工程ID不能小于1")
    private final Integer projectID;
    @Positive(message = "传感器ID不能小于1")
    private Integer sensorID;
    @Positive(message = "通道号不能小于1")
    private Integer deviceChannel;

    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return Objects.isNull(projectID) ? new Resource(companyID.toString(), ResourceType.COMPANY) : new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
