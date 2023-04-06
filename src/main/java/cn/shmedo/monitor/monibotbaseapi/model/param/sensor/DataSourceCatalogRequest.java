package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据源下拉 请求体
 *
 * @author Chengfs on 2023/4/6
 */
@Data
public class DataSourceCatalogRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    /**
     * 数据源类型
     */
    private Integer dataSourceComposeType = DataSourceComposeType.SINGLE_IOT.getCode();

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 检索关键字
     */
    private String keyword;


    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    