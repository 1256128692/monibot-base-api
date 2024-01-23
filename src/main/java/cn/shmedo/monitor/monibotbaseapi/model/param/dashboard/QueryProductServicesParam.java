package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
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
@NoArgsConstructor
@AllArgsConstructor
public class QueryProductServicesParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    /**
     * 项目一级分类
     */
    private Byte projectMainType;

    @Override
    public ResultWrapper<?> validate() {
        Boolean validMainType = Optional.of(Arrays.stream(PlatformType.values()).map(PlatformType::getType).collect(Collectors.toSet()))
                .map(t -> t.contains(projectMainType)).get();
        if (Objects.nonNull(projectMainType) && !validMainType) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目一级分类不合法");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
