package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
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

    /**
     * 用户有权限的项目
     */
    @JsonIgnore
    private Set<Integer> projectIDSet;

    @Override
    public ResultWrapper<?> validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (CollectionUtil.isNotEmpty(tbProjectInfoMapper.selectListByCompanyIDAndProjectIDList(companyID, null))) {
            projectIDSet = new HashSet<>(PermissionUtil.getHavePermissionProjectList(companyID));
            if (projectIDSet.isEmpty()) {
                return ResultWrapper.withCode(ResultCode.NO_PERMISSION, "没有权限访问该公司下的项目");
            }
        } else if (CollectionUtil.isNotEmpty(projectIDSet)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该公司下并没有工程项目,因此该重点工程项目ID列表不合法");
        }
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
