package cn.shmedo.monitor.monibotbaseapi.model.param.workorder;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-18 18:03
 */
@Data
public class QueryWorkOrderPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Min(value = 1, message = "公司ID不能小于1")
    private Integer companyID;
    @Range(max = 6, message = "工单状态不合法,工单状态取值范围0~6")
    private Integer status;
    private String queryCode;
    @Range(min = 1, max = 4, message = "工单类型取值范围1~4")
    private Integer workOrderTypeID;
    private Date startTime;
    private Date endTime;
    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;
    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
    @Range(min = 1, max = 3)
    private Integer sourceType;
    @JsonIgnore
    private List<Integer> projectIDList;

    @Override
    public ResultWrapper<?> validate() {
        Date current = new Date();
        if (endTime != null && endTime.after(current)) {
            endTime = current;
        }
        this.sourceType = Optional.ofNullable(sourceType).orElse(1);
        Optional.of(sourceType).filter(u -> u == 1).ifPresent(u -> {
            List<Integer> permissProjectIDList = PermissionUtil.getHavePermissionProjectList(companyID, null)
                    .stream().toList();
            projectIDList = CollectionUtil.isEmpty(permissProjectIDList) ? null : permissProjectIDList;
        });
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
