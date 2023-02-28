package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

/**
 * 查询属性数据 请求体
 *
 * @author Chengfs on 2023/2/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPropertyValueParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    /**
     * 公司ID
     */
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    /**
     * 项目ID
     */
    private Integer projectID;

    /**
     * 项目类型
     */
    @NotNull(message = "项目类型不能为空")
    @Range(min = 1, message = "项目类型必须大于0")
    private Integer projectType;

    /**
     * 创建类型 0-预定义 1-自定义
     */
    @Range(min = 0, max = 1, message = "创建类型必须为0或1")
    private Integer createType;

    /**
     * 属性名称
     */
    @NotNull(message = "属性名称不能为空")
    private String propertyName;

    @Override
    public ResultWrapper<?> validate() {
        if (createType == null) {
            createType = 0;
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}

    
    