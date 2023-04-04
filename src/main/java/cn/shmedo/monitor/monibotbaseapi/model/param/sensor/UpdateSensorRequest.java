package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.SensorConfigField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 更新传感器 请求体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
public class UpdateSensorRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 传感器ID
     */
    @NotNull(message = "传感器ID不能为空")
    private Integer sensorID;

    /**
     * 传感器图片路径
     */
    private String imagePath;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 扩展配置列表
     */
    private List<SensorConfigField> exFields;

    /**
     * 参数列表
     */
    private List<SensorConfigField> paramFields;


    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    