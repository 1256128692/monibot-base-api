package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * @author Chengfs on 2023/4/11
 */
@Data
public class UpdateSensorStatusRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

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
     * 传感器状态
     */
    private Integer sensorStatus;

    /**
     * 监测开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date monitorBeginTime;

    @Override
    public ResultWrapper<?> validate() {
        Assert.isTrue(this.monitorBeginTime != null || this.sensorStatus != null, "监测开始时间和传感器状态不能同时为空");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    