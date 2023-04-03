package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 数据源级联查询 请求体
 *
 * @author Chengfs on 2023/3/31
 */
@Data
public class DataSourceCascadeRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 检索键
     */
    private String keyword;

    /**
     * 上级选中值
     */
    private Object previous;

    /**
     * 层级，默认为 1，最大为 3
     */
    @Range(min = 1, max = 3, message = "层级必须在1-3之间")
    private Integer level = 1;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    