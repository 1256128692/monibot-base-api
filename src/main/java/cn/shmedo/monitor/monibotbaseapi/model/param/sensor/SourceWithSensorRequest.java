package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author Chengfs on 2023/4/11
 */
@Data
public class SourceWithSensorRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @NotNull(message = "数据源标识不能为空")
    private String dataSourceToken;

    private Integer dataSourceComposeType;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }

}

    
    