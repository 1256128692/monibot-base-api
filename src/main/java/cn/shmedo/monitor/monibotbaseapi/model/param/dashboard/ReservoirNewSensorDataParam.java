package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ReservoirNewSensorDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer companyID;
    @NotNull
    private Integer projectID;

    @Override
    public ResultWrapper<?> validate() {
        // 加校验(1.监测点的项目ID必须与项目ID一致 2.密度不为空是,必须以h或者d结尾)
//        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
//        LambdaQueryWrapper<TbMonitorPoint> wrapper = new LambdaQueryWrapper<TbMonitorPoint>()
//                .eq(TbMonitorPoint::getID, monitorPointID);
//        this.tbMonitorPoint = tbMonitorPointMapper.selectOne(wrapper);
//
//        if (tbMonitorPoint == null) {
//            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点不存在");
//        } else {
//            if (!tbMonitorPoint.getProjectID().equals(projectID)) {
//                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点的项目ID与条件项目ID不符");
//            }
//            if (!tbMonitorPoint.getMonitorType().equals(MonitorType.INTERNAL_TRIAXIAL_DISPLACEMENT.getKey())) {
//                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "仅支持查询内部三轴位移类型");
//            }
//        }
//
//        if (StringUtils.isNotBlank(density)) {
//            if (!(density.endsWith("h") || density.endsWith("d") || density.endsWith("m") || density.equals("all"))) {
//                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前查询密度条件错误");
//            }
//            // 传输条件为all时,置空查询密度,即可查询全部数据
//            if (density.equals("all")){
//                density = null;
//            }
//        }

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
