package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Objects;

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
     * 聚合模糊查询关键字
     */
    private String keyword;

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

    /**
     * 传感器ID
     */
    private Integer sensorID;

    /**
     * 监测类型ID
     */
    private Integer monitorTypeID;

    /**
     * 监测点ID
     */
    private Integer monitorPointID;
    /**
     * 数据来源关键字
     */
    @Range(max = 4, message = "0.全部 1.物联网数据 2.API外部数据 3.人工数据 4.视频数据 ")
    private Integer dataSourceKey;

    @Override
    public ResultWrapper<?> validate() {
        dataSourceKey = Objects.isNull(dataSourceKey) ? 0 : dataSourceKey;
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    