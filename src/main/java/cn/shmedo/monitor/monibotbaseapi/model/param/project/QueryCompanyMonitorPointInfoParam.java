package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbUserFollowMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserFollowMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QueryCompanyMonitorPointInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    private Integer monitorType;

    private List<Integer> monitorStatusList;

    private List<Integer> monitorItemIDList;

    private String monitorPointName;

    private List<Integer> monitorPointIDList = new LinkedList<>();

    private Boolean monitorPointCollection;
    @JsonIgnore
    private Integer userID;

    @Override
    public ResultWrapper validate() {

        Collection<Integer> permissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID, null);
        // 当前用户下没有任何工程则直接返回空
        if (permissionProjectList.isEmpty()) {
            return ResultWrapper.withCode(ResultCode.SUCCESS, null);
        }

        this.userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();

        TbUserFollowMonitorPointMapper userFollowMonitorPointMapper = ContextHolder.getBean(TbUserFollowMonitorPointMapper.class);

        List<TbUserFollowMonitorPoint> tbUserFollowMonitorPoints = userFollowMonitorPointMapper.selectList(new QueryWrapper<TbUserFollowMonitorPoint>()
                .lambda().eq(TbUserFollowMonitorPoint::getUserID, userID));
        if (!CollectionUtil.isNullOrEmpty(tbUserFollowMonitorPoints)) {
            monitorPointIDList = tbUserFollowMonitorPoints.stream().map(TbUserFollowMonitorPoint::getMonitorPointID).collect(Collectors.toList());
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
