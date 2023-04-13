package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
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
    private Integer dataSourceComposeType;

    /**
     * 监测类型模板分布式唯一ID
     */
    private String templateDataSourceID;

    @Override
    public ResultWrapper<?> validate() {
        if (dataSourceComposeType != null) {
            Assert.isTrue(DataSourceComposeType.isValid(dataSourceComposeType), "数据来源类型不合法");
        } else {
            dataSourceComposeType = DataSourceComposeType.SINGLE_IOT.getCode();
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    