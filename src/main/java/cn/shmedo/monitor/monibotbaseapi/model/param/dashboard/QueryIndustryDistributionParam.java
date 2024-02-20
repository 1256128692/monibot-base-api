package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2024/1/18 16:27
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.dashboard
 * @ClassName: QueryIndustryDistributionParam
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class QueryIndustryDistributionParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @JsonIgnore
    private Set<Integer> tokenSet;

    @Override
    public ResultWrapper<?> validate() {
        // 用户项目权限处理，只取用户有访问权限的项目
        tokenSet = PermissionUtil.getResourceList(CurrentSubjectHolder.getCurrentSubject().getSubjectID(), null,
                DefaultConstant.MDNET_SERVICE_NAME, DefaultConstant.LIST_PROJECT, ResourceType.BASE_PROJECT).stream().map(Integer::valueOf).collect(Collectors.toSet());

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
