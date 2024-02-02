package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-01 10:15
 **/
@Data
public class ReservoirDeviceStatisticsParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {
    @NotNull
    private Integer companyID;
    private Integer projectID;
    @JsonIgnore
    private Collection<Integer> havePermissionProjectList;
    @Override
    public ResultWrapper validate() {
        if (projectID != null) {
            havePermissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID, List.of(projectID));

        } else {
            havePermissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID);
        }
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
