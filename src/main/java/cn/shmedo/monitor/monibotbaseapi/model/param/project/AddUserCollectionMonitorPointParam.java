package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbUserFollowMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbUserFollowMonitorPoint;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class AddUserCollectionMonitorPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer projectID;
    @NotEmpty
    @Size(min = 1, max = 100)
    private List<Integer> monitorPointIDList;


    @JsonIgnore
    private List<TbUserFollowMonitorPoint> userFollowMonitorPointList = new LinkedList<>();

    @Override
    public ResultWrapper validate() {

        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbUserFollowMonitorPointMapper userFollowMonitorPointMapper = ContextHolder.getBean(TbUserFollowMonitorPointMapper.class);
        LambdaQueryWrapper<TbUserFollowMonitorPoint> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TbUserFollowMonitorPoint::getMonitorPointID, monitorPointIDList);
        queryWrapper.eq(TbUserFollowMonitorPoint::getUserID, subjectID);

        List<TbUserFollowMonitorPoint> tbUserFollowMonitorPoints = userFollowMonitorPointMapper.selectList(queryWrapper);

        if (CollectionUtils.isNotEmpty(tbUserFollowMonitorPoints)) {
            // 获取已经存在的监测点ID集合
            Set<Integer> existingMonitorPointIds = tbUserFollowMonitorPoints.stream()
                    .map(TbUserFollowMonitorPoint::getMonitorPointID)
                    .collect(Collectors.toSet());

            // 过滤掉已经存在的监测点
            monitorPointIDList = monitorPointIDList.stream()
                    .filter(monitorPointId -> !existingMonitorPointIds.contains(monitorPointId))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(monitorPointIDList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前用户收藏的监测点已经存在");
            }
        }

        DateTime date = DateUtil.date();
        monitorPointIDList.forEach(m -> {
            TbUserFollowMonitorPoint vo = new TbUserFollowMonitorPoint();
            vo.setUserID(subjectID);
            vo.setMonitorPointID(m);
            vo.setCreateTime(date);
            userFollowMonitorPointList.add(vo);
        });

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
