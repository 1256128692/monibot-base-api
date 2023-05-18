package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-16 18:34
 */
@Data
public class QueryRealDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    @NotNull(message = "监测组ID不能为空")
    @Min(value = 1, message = "监测组ID不能小于1")
    private Integer monitorGroupID;
    @Range(max = 3, message = "密度枚举错误,密度 0.全部;1.日平均;2.月平均;3.年平均")
    private Integer density;
    private Date startTime;
    private Date endTime;
    @Range(min = 1, max = 3, message = "数据轴枚举错误,数据轴 1.A轴;2.B轴;3.C轴")
    private Integer monitorChildType;

    @Override
    public ResultWrapper validate() {
        if (!isAllNullOrNotNull(density, startTime, endTime)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "密度、开始时间、结束时间必须全部存在或全部不存在");
        }
        monitorChildType = Objects.isNull(monitorChildType) ? 1 : monitorChildType;
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    private boolean isAllNullOrNotNull(Object... obj) {
        return Arrays.stream(obj).map(Objects::isNull).distinct().toList().size() == 1;
    }
}
