package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Optional;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 18:26
 */
@Data
public class ListProjectConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "分组信息不能为空")
    private String group;
    private String key;

    public LambdaQueryWrapper<TbProjectConfig> build() {
        LambdaQueryWrapper<TbProjectConfig> wrapper = new LambdaQueryWrapper<TbProjectConfig>()
                .eq(TbProjectConfig::getProjectID, projectID);
        Optional.ofNullable(group).filter(ObjectUtil::isNotEmpty).ifPresent(u -> wrapper.eq(TbProjectConfig::getGroup, u));
        Optional.ofNullable(key).filter(ObjectUtil::isNotEmpty).ifPresent(u -> wrapper.likeLeft(TbProjectConfig::getKey, u));
        return wrapper;
    }

    @Override
    public ResultWrapper validate() {
        key = ObjectUtil.isEmpty(key) || "all".equalsIgnoreCase(key) ? null : key;
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
