package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 16:20
 **/
@Data
public class UpdateMonitorPointBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotEmpty
    @Valid
    @Size(max = 100)
    private List<UpdatePointItem> updatePointItemList;

    @JsonIgnore
    private List<TbMonitorPoint> tbMonitorPointList;


    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        tbMonitorPointList = tbMonitorPointMapper.selectBatchIds(updatePointItemList.stream().map(UpdatePointItem::getPointID).collect(Collectors.toList())
        );
        if (tbMonitorPointList.size() != updatePointItemList.size()
        ) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在");
        }
        if (tbMonitorPointMapper.selectCount(
                new QueryWrapper<TbMonitorPoint>().eq("projectID", projectID)
                        .in("Name", updatePointItemList.stream().map(UpdatePointItem::getName).toList())
                        .notIn("ID", updatePointItemList.stream().map(UpdatePointItem::getPointID).toList())
        )>0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目下监测点名称已存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }

    public List<TbMonitorPoint> updateBatch(Integer userID) {
        Map<Integer, UpdatePointItem> map = updatePointItemList.stream().collect(Collectors.toMap(UpdatePointItem::getPointID, Function.identity()));
        tbMonitorPointList.forEach(tbMonitorPoint -> {
            UpdatePointItem updatePointItem = map.get(tbMonitorPoint.getID());
            tbMonitorPoint.setName(updatePointItem.getName());
            tbMonitorPoint.setEnable(updatePointItem.getEnable());
            tbMonitorPoint.setGpsLocation(updatePointItem.getGpsLocation());
            tbMonitorPoint.setImageLocation(updatePointItem.getImageLocation());
            tbMonitorPoint.setOverallViewLocation(updatePointItem.getOverallViewLocation());
            tbMonitorPoint.setSpatialLocation(updatePointItem.getSpatialLocation());
            tbMonitorPoint.setExValues(updatePointItem.getExValues());
            tbMonitorPoint.setDisplayOrder(updatePointItem.getDisplayOrder());
            tbMonitorPoint.setUpdateTime(new Date());
            tbMonitorPoint.setUpdateUserID(userID);
        });

        return tbMonitorPointList;
    }
}
