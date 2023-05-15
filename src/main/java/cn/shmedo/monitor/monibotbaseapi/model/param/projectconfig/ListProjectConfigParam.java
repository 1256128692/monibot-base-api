package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 18:26
 */
@Data
public class ListProjectConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    private String group;
    private String key;

    @Override
    public ResultWrapper validate() {
        key = ObjectUtil.isEmpty(key) || "all".equalsIgnoreCase(key) ? null : key;
        group = ObjectUtil.isEmpty(group) || "all".equalsIgnoreCase(group) ? null : group;
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
