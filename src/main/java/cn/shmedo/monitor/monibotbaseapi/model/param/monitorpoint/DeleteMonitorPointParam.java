package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 14:34
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMonitorPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotEmpty
    @Size(max = 100)
    @Valid
    private List<@NotNull Integer> pointIDList;
    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        if (tbMonitorPointMapper.selectCount(
             new QueryWrapper<TbMonitorPoint>().eq("projectID",projectID).in("ID", pointIDList)
        ) != pointIDList.size()){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点不存在或不属于该工程项目");
        }
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        if (tbSensorMapper.selectCount(
            new QueryWrapper<TbSensor>().in("MonitorPointID", pointIDList)
        ) > 0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测点下存在传感器，不可删除");

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
