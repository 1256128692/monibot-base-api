package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 15:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryHikVideoPlayBackParam extends HikVideoBaseParam implements ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private final Integer companyID;
    @NotNull(message = "存储类型不能为空")
    @Range(max = 1, message = "存储类型 0.中心存储 1.设备存储")
    private Integer recordLocation;
    @NotNull(message = "开始时间不能为空")
    private Date beginTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    private String uuid;

    @Override
    public Resource parameter() {
        return Objects.nonNull(projectID) ? new Resource(projectID.toString(), ResourceType.BASE_PROJECT) : new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
