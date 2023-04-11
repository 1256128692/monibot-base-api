package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 传感器列表请求体
 *
 * @author Chengfs on 2023/4/11
 */
@Data
public class SensorListRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

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
}

    
    