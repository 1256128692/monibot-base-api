package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceComposeType;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Optional;

/**
 * 监测类型目录查询
 *
 * @author Chengfs on 2023/3/31
 */
@Data
public class MonitorTypeCatalogRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @Positive
    private Integer companyID;

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
            Assert.notNull(DataSourceComposeType.codeOf(dataSourceComposeType), "数据来源类型不合法");
        } else {
            dataSourceComposeType = DataSourceComposeType.SINGLE_IOT.getCode();
        }

        this.companyID = Optional.ofNullable(companyID)
                .orElse(CurrentSubjectHolder.getCurrentSubject().getCompanyID());
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }
}

    
    