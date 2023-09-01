package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.util.CustomWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 15:45
 */
@Data
public class QueryYsVideoPlayBackParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private final Integer companyID;
    @NotNull(message = "存储类型不能为空")
    @Range(max = 1, message = "存储类型 0.中心存储 1.设备存储")
    private Integer recordLocation;
    @NotNull(message = "开始时间不能为空")
    private Date beginTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    private Integer deviceVideoID;
    private Integer deviceChannel;
    private Integer sensorID;
    private Integer monitorPointID;
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;

    @Override
    public ResultWrapper validate() {
        if (!valid()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备通道、传感器ID、监测点ID三组数据要求仅有一组不为空!");
        }
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        TbSensor tbSensor = null;
        if (Objects.nonNull(monitorPointID)) {
            if (!ContextHolder.getBean(TbMonitorPointMapper.class).exists(new LambdaQueryWrapper<TbMonitorPoint>().eq(TbMonitorPoint::getID, monitorPointID))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点不存在!");
            }
            List<TbSensor> list = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getMonitorPointID, monitorPointID));
            if (CollUtil.isEmpty(list)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点未绑定传感器!");
            }
            if (list.size() > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频监测点绑定了多个传感器!");
            }
            this.sensorID = list.get(0).getID();
            tbSensor = list.get(0);
        }
        if (Objects.nonNull(sensorID)) {
            if (Objects.isNull(tbSensor)) {
                List<TbSensor> sensorList = tbSensorMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getID, sensorID));
                if (CollUtil.isEmpty(sensorList)) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器不存在!");
                }
                tbSensor = sensorList.get(0);
            }
//            tbSensor.get
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return Objects.nonNull(projectID) ? new Resource(projectID.toString(), ResourceType.BASE_PROJECT) : new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    private boolean valid() {
        final CustomWrapper<Integer> wrapper = new CustomWrapper<>(3);
        Optional.ofNullable(this.deviceVideoID).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(this.deviceChannel).ifPresent(u -> wrapper.setValue(v -> v - 1));
        Optional.ofNullable(this.sensorID).ifPresent(u -> wrapper.setValue(v -> v - 2));
        Optional.ofNullable(this.monitorPointID).ifPresent(u -> wrapper.setValue(v -> v - 2));
        return wrapper.get() == 1;
    }
}
