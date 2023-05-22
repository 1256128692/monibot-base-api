package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-19 10:20
 */
@Data
public abstract class QueryThematicDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    private Timestamp startTime;
    private Timestamp endTime;
    @NotEmpty(message = "配置信息key不能为空")
    private String key;
    @NotEmpty(message = "配置信息group不能为空")
    private String group;
    @Range(max = 3, message = "密度枚举错误,密度 0.全部;1.日平均;2.月平均;3.年平均")
    private Integer density;

    @Override
    public ResultWrapper validate() {
        return Stream.of(density, startTime, endTime).map(Objects::isNull).distinct().toList().size() != 1 ?
                ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "密度、开始时间、结束时间必须全部存在或全部不存在") : null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
