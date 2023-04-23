package cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 13:19
 **/
@Data
public class ConfigMonitorPointImageLocationParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer groupID;
    @NotEmpty
    @Size(max = 20)
    @Valid
    List<PointIDAndLocation> pointLocationList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorGroupMapper tbMonitorGroupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
        var tbMonitorGroup = tbMonitorGroupMapper.selectByPrimaryKey(groupID);
        if (tbMonitorGroup == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组不存在");
        }
        if (!tbMonitorGroup.getProjectID().equals(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组不属于该项目");
        }
        if (StrUtil.isBlank(tbMonitorGroup.getImagePath())){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组没有图片");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        List<TbMonitorPoint> tbMonitorPointList = tbMonitorPointMapper.selectBatchIds(pointLocationList.stream().map(PointIDAndLocation::getPointID).toList());  ;
        if (tbMonitorPointList.size() != pointLocationList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在");
        }
        TbMonitorGroupPointMapper tbMonitorGroupPointMapper = ContextHolder.getBean(TbMonitorGroupPointMapper.class);
        List<Integer> pointIDs = tbMonitorGroupPointMapper.queryPointIDByGroupID(groupID);
        if (CollectionUtils.isEmpty(pointIDs)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "分组下没有监测点");
        }
        if (!pointIDs.containsAll(pointLocationList.stream().map(PointIDAndLocation::getPointID).toList())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有检测点不在分组下");
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
