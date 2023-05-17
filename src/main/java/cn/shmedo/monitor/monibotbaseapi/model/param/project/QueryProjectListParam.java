package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.base.SubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.Optional;

@Data
public class QueryProjectListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    private Integer projectType;

    private String projectName;

    private Byte platformType;

    private Collection<Integer> projectIDList;

    @Override
    public ResultWrapper<?> validate() {
        Optional.ofNullable(platformType).ifPresent(val -> Assert.isTrue(PlatformType.validate(val), "platformType is invalid"));
        //只限制用户查询范围
        if (SubjectType.USER.equals(CurrentSubjectHolder.getCurrentSubject().getSubjectType())) {
            this.projectIDList = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);
            if (projectIDList.isEmpty()) {
                return ResultWrapper.successWithNothing();
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
