package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
