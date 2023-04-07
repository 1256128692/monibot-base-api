package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.TryingDto;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 试运行 请求体
 *
 * @author Chengfs on 2023/4/3
 */
public class TryingRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 监测类型
     */
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    /**
     * 监测字段ID
     */
    private Integer fieldID;

    /**
     * 计算类型
     */
    @NotNull(message = "计算类型不能为空")
    private Integer calType;

    /**
     * 参数列表
     */
    private List<TryingDto.Param> paramList;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    