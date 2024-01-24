package cn.shmedo.monitor.monibotbaseapi.model.param.project;


import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbUserFollowMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserFollowMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuerySingleProjectMonitorPointNewDataPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer projectID;

    private List<Integer> monitorStatusList;

    private List<Integer> monitorItemIDList;

    private String monitorPointName;

    private List<Integer> monitorPointIDList = new LinkedList<>();

    private Boolean monitorPointCollection;
    @JsonIgnore
    private Integer userID;


    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;
    @Override
    public ResultWrapper validate() {

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
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }



}
