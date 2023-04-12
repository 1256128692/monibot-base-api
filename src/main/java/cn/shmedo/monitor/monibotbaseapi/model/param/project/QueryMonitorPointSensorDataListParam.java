package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
public class QueryMonitorPointSensorDataListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer projectID;

    @NotNull
    private Integer monitorPointID;

    @NotNull
    private Timestamp begin;

    @NotNull
    private Timestamp end;

    private String density;

    @JsonIgnore
    private TbMonitorPoint tbMonitorPoint;


    @Override
    public ResultWrapper<?> validate() {
        // 加校验(1.监测点的项目ID必须与项目ID一致 2.密度不为空是,必须以h或者d结尾)
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        LambdaQueryWrapper<TbMonitorPoint> wrapper = new LambdaQueryWrapper<TbMonitorPoint>()
                .eq(TbMonitorPoint::getID, monitorPointID);
        this.tbMonitorPoint = tbMonitorPointMapper.selectOne(wrapper);

        if (tbMonitorPoint == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点不存在");
        } else {
            if (!tbMonitorPoint.getProjectID().equals(projectID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点的项目ID与条件项目ID不符");
            }
        }

        if (StringUtils.isNotBlank(density)) {
            if (!(density.endsWith("h") || density.endsWith("d") || density.endsWith("m"))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前查询密度条件错误");
            }
            // 传输条件为all时,置空查询密度,即可查询全部数据
            if (density.equals("all")){
                density = null;
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
