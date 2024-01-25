package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @Author wuxl
 * @Date 2024/1/24 10:41
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller.dashboard
 * @ClassName: QueryProvinceProjectDetailParam
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class QueryProvinceProjectDetailParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    /**
     * 项目一级分类
     */
    private Byte projectMainType;

    @NotNull(message = "省份code码不能为空")
    private Integer provinceCode;

    @Override
    public ResultWrapper<?> validate() {
        Boolean validMainType = Optional.of(Arrays.stream(PlatformType.values()).map(PlatformType::getType).collect(Collectors.toSet()))
                .filter(t -> !PlatformType.MDNET.getType().equals(projectMainType))
                .map(t -> t.contains(projectMainType)).orElse(false);
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
