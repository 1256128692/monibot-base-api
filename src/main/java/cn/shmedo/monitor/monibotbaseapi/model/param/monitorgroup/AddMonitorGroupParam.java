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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 16:54
 **/
@Data
public class AddMonitorGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    private Integer parentID;
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

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        Integer count = 0;
        if (tbProjectInfoMapper.selectById(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorGroupMapper tbMonitorGroupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
        if (parentID != null) {
            // 二级组校验
            if (CollectionUtils.isNotEmpty(monitorItemIDList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "二级分组不能设置监测项");
            }
            TbMonitorGroup parentGroup = tbMonitorGroupMapper.selectByPrimaryKey(parentID);
            if (parentGroup == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "父分组不存在");
            }
            if (!parentGroup.getProjectID().equals(projectID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "父分组不属于该项目");
            }
            if (parentGroup.getParentID() != null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "父分组不是一级分组");
            }
            // 二级类别监测组名称校验
            count = tbMonitorGroupMapper.selectCountByName(name, true, projectID);
            if (count > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "二级分组名称重复,请重新输入");
            }
        } else {
            if (CollectionUtils.isNotEmpty(monitorPointIDList)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一级分组必须不能设置监测点");
            }
            // 一级类别监测组名称校验
            count = tbMonitorGroupMapper.selectCountByName(name, false, projectID);
            if (count > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "一级分组名称重复,请重新输入");
            }
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
            if (parentID != null) {

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
                                    .eq(TbMonitorGroupItem::getMonitorGroupID, parentID)
                    ).stream().map(TbMonitorGroupItem::getMonitorItemID).toList();
                    if (tbMonitorPoints.stream().anyMatch(tbMonitorPoint -> !parentItemIDList.contains(tbMonitorPoint.getMonitorItemID()))) {
                        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不属于父组的监测项目");
                    }
                }
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
