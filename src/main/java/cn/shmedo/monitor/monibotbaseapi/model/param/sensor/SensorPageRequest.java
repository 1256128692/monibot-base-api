package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 传感器分页请求体
 *
 * @author Chengfs on 2023/3/31
 */
@Data
public class SensorPageRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 分页大小 (1-100)
     */
    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    /**
     * 当前页码 (大于0)
     */
    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 传感器名称 模糊查询
     */
    private String sensorName;

    /**
     * 监测类型 模糊查询
     */
    private String monitorType;

    /**
     * 关联监测点
     */
    private String monitorPoint;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}

    
    