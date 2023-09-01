package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
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
public class QueryVideoCompanyViewBaseInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @Range(max = 2, message = "视频设备状态枚举 0.全部 1.仅在线 2.仅离线")
    private Integer status;
    private String deviceSerial;
    private Integer deviceVideoID;

    @Override
    public ResultWrapper validate() {
        status = Objects.isNull(status) ? 0 : status;
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
