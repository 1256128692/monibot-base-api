package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 11:49
 */
@Data
public class QueryVideoProjectViewBaseInfo implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @Range(max = 2, message = "视频设备状态枚举 0.全部 1.仅在线 2.仅离线")
    private Integer status;
    private String deviceSerial;
    private String queryCode;
    @JsonIgnore
    private Boolean deviceStatus;

    @Override
    public ResultWrapper validate() {
        deviceStatus = Objects.isNull(status) || status == 0 ? null : status == 1;
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
