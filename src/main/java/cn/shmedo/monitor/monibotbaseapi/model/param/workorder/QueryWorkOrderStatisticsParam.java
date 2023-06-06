package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-19 14:29
 */
@Data
public class QueryWorkOrderStatisticsParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @Min(value = 1, message = "工程项目ID不能小于1")
    private Integer projectID;
    @Range(min = 1, max = 3)
    private Integer sourceType;

    private Boolean enable;
    @JsonIgnore
    private List<Integer> projectIDList;

    @Override
    public ResultWrapper validate() {
        projectIDList = PermissionUtil.getHavePermissionProjectList(companyID, projectID == null ? null: List.of(projectID)).stream().toList();
        if (CollUtil.isEmpty(projectIDList)) {
            return ResultWrapper.success(PageUtil.Page.empty());
        }
        if (projectID != null) {
            projectIDList = projectIDList.stream().filter(p -> p.equals(projectID)).collect(Collectors.toList());
            if (CollUtil.isEmpty(projectIDList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "找不到项目编号对应的工程项目");
            }
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
