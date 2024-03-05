package cn.shmedo.monitor.monibotbaseapi.model.param.checkevent;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventTypeMapper;
import com.alibaba.nacos.shaded.org.checkerframework.checker.nullness.qual.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class QueryDailyTaskParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;
    private Integer serviceID;
    private Integer projectID;

    @Size(min = 0, max = 5)
    private List<@NotNull @Min(0) @Max(4) Integer> checkTypeList;

    @Size(min = 0, max = 4)
    private List<@NotNull @Min(0) @Max(3) Integer> statusList;
    @NotNull
    @Min(0)
    @Max(1)
    private Integer queryType;
    @NotNull
    private Date begin;
    @NotNull
    private Date end;

    @Override
    public ResultWrapper validate() {
        TbCheckEventTypeMapper checkEventTypeMapper = ContextHolder.getBean(TbCheckEventTypeMapper.class);

        if (begin.after(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }
        return null;
    }

    @Override
    public Resource parameter() {

        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
