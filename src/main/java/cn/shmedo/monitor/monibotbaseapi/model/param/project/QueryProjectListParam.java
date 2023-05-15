package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Data
public class QueryProjectListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    private Integer projectType;

    private String projectName;

    private Byte platformType;

    private Set<Integer> projectIDList;

    @JsonIgnore
    private Collection<Integer> projectIDs;

    @Override
    public ResultWrapper<?> validate() {
        Optional.ofNullable(platformType).ifPresent(val -> Assert.isTrue(PlatformType.validate(val), "platformType is invalid"));
        this.projectIDs = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);
        if (projectIDs.isEmpty()) {
            return ResultWrapper.withCode(ResultCode.NO_PERMISSION, "没有权限访问该公司下的项目");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
