package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 监测类型目录查询
 *
 * @author Chengfs on 2023/3/31
 */
@Data
public class MonitorTypeCatalogRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 模板数据来源类型
     */
    private Integer dataSourceComposeType = DataSourceComposeType.SINGLE_IOT.getCode();

    /**
     * 监测类型模板分布式唯一ID
     */
    @NotEmpty(message = "监测类型模板分布式唯一ID不能为空")
    private String templateDataSourceID;

    /**
     * 监测类型模板ID
     */
    @NotNull(message = "监测类型模板ID不能为空")
    private Integer templateID;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    