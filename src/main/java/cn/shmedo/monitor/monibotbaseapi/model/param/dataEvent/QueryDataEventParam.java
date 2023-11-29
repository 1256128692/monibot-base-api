package cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
public class QueryDataEventParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    private Integer monitorItemID;
    private List<Integer> monitorPointIDList;

    private Date begin;
    private Date end;

    @JsonIgnore
    private List<Integer> monitorItemIDList = new LinkedList<>();
    @Override
    public ResultWrapper validate() {

        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        if (!CollectionUtil.isNullOrEmpty(monitorPointIDList)) {
            monitorItemIDList = tbMonitorPointMapper.selectItemIDsByIDs(monitorPointIDList);
            if (!monitorItemIDList.contains(monitorItemID)) {
                monitorItemIDList.add(monitorItemID);
            }
        }

        if (begin != null && end != null) {
            if (begin.after(end)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {

        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

}
