package cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 10:22
 **/
@Data
public class UpdateMonitorGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer groupID;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    private Boolean enable;

    @Valid
    @Size(max = 50)
    private List<@NotNull Integer> monitorItemIDList;
    @Valid
    @Size(max = 50)
    private List<@NotNull Integer> monitorPointIDList;

    @JsonIgnore
    private TbMonitorGroup tbMonitorGroup;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectById(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorGroupMapper tbMonitorGroupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
        tbMonitorGroup = tbMonitorGroupMapper.selectByPrimaryKey(groupID);
        if (tbMonitorGroup == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组不存在");
        }
        if (!tbMonitorGroup.getProjectID().equals(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组不属于该项目");
        }
        if (tbMonitorGroup.getParentID() == null && CollectionUtils.isNotEmpty(monitorPointIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一级组不能设置监测点");
        }
        if (tbMonitorGroup.getParentID() != null && CollectionUtils.isNotEmpty(monitorItemIDList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "二级组不能设置监测项目");
        }
        if (CollectionUtils.isNotEmpty(monitorItemIDList)) {
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectBatchIds(
                    monitorItemIDList
            );
            if (tbMonitorItems.size() != monitorItemIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测项不存在");
            }
            if (tbMonitorItems.stream().anyMatch(tbMonitorItem -> !tbMonitorItem.getProjectID().equals(projectID))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测项不属于该项目");
            }
            if (tbMonitorGroup.getParentID() != null) {
                if (CollectionUtils.isNotEmpty(monitorPointIDList)) {
                    TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
                    List<TbMonitorPoint> tbMonitorPoints = tbMonitorPointMapper.selectBatchIds(
                            monitorPointIDList
                    );
                    if (tbMonitorPoints.size() != monitorPointIDList.size()) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在");
                    }
                    if (tbMonitorPoints.stream().anyMatch(tbMonitorPoint -> !tbMonitorPoint.getProjectID().equals(projectID))) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不属于该项目");
                    }
                    TbMonitorGroupItemMapper tbMonitorGroupItemMapper = ContextHolder.getBean(TbMonitorGroupItemMapper.class);
                    List<Integer> parentItemIDList = tbMonitorGroupItemMapper.selectList(
                            new QueryWrapper<TbMonitorGroupItem>().lambda()
                                    .eq(TbMonitorGroupItem::getMonitorGroupID, tbMonitorGroup.getParentID())
                    ).stream().map(TbMonitorGroupItem::getMonitorItemID).toList();
                    if (tbMonitorPoints.stream().anyMatch(tbMonitorPoint -> !parentItemIDList.contains(tbMonitorPoint.getMonitorItemID()))) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不属于监测项目");
                    }
                }
            } else if (CollectionUtils.isNotEmpty(monitorPointIDList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一级分组不能设置监测点");
            }
        }
        return null;
    }

    public TbMonitorGroup update(Date now, Integer userID) {
        tbMonitorGroup.setName(name);
        tbMonitorGroup.setUpdateTime(now);
        tbMonitorGroup.setUpdateUserID(userID);
        tbMonitorGroup.setEnable(enable);
        return tbMonitorGroup;
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
