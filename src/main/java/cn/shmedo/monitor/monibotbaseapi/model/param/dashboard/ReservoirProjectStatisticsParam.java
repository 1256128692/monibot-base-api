package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 11:08
 **/
@Data
public class ReservoirProjectStatisticsParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    @Positive
    private Integer companyID;
    @JsonIgnore
    private Collection<Integer> havePermissionProjectList;

    @Override
    public ResultWrapper validate() {
        havePermissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID);
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return havePermissionProjectList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}
