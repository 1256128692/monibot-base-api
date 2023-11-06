package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.HikPtzCommandEnum;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorQueryType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDeviceWithSensorIDInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Arrays;
import java.util.List;

@Data
public class PanControlVideoPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotNull(message = "监测点ID不能为空")
    @Positive(message = "监测点ID不能小于1")
    private Integer monitorPointID;
    @NotNull(message = "方向不能为空")
    @Range(max = 16, message = "方向 0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制")
    private Integer direction;

    @JsonIgnore
    private List<VideoMonitorPointLiveInfo> liveInfos;
    @JsonIgnore
    private VideoDeviceWithSensorIDInfo withSensorIDInfo;

    @Override
    public ResultWrapper<?> validate() {
        TbProjectMonitorClassMapper projectMonitorClassMapper = ContextHolder.getBean(TbProjectMonitorClassMapper.class);
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        LambdaQueryWrapper<TbProjectMonitorClass> wrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                .eq(TbProjectMonitorClass::getProjectID, projectID)
                .eq(TbProjectMonitorClass::getMonitorClass, MonitorQueryType.VIDEO.getValue())
                .eq(TbProjectMonitorClass::getEnable, true);
        // 先查询当前工程ID 是否配置了视频监测,如果没有配置则返回错误
//        TbProjectMonitorClass projectMonitorClass = projectMonitorClassMapper.selectOne(wrapper);
//        if (projectMonitorClass == null) {
//            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测工程未配置监测类型归属");
//        }

        liveInfos = tbMonitorPointMapper.selectListByIDList(Arrays.asList(monitorPointID));
        if (!CollectionUtil.isNullOrEmpty(liveInfos)) {
            // 过滤监测点列表,如果存在非法监测类型,则返回错误
            long count = liveInfos.stream().filter(item -> !item.getMonitorType().equals(MonitorType.VIDEO.getKey())).count();
            if (count > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表含有非法的监测类型,仅支持视频监测类型的监测点");
            }

            long notIncludeSensorPointCount = liveInfos.stream().filter(item -> item.getSensorID() == null).count();
            // 如果有监测点下未绑定设备的话也返回错误
            if (notIncludeSensorPointCount > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点列表含未绑定设备的监测点");
            }

            List<Integer> sensorIDList = liveInfos.stream().map(VideoMonitorPointLiveInfo::getSensorID).toList();
            //一般情况下，一个视频监测点只会绑定一个传感器，一个传感器就是一个视频设备的通道，只可能绑定在同一个设备下
            if (sensorIDList.size() > 1) {
                return ResultWrapper.withCode(ResultCode.SERVER_EXCEPTION, "一个视频监测点绑定了多个传感器");
            }
            List<VideoDeviceWithSensorIDInfo> videoDeviceWithSensorIDInfos = ContextHolder.getBean(TbVideoDeviceMapper.class)
                    .selectListWithSensorIDBySensorIDList(sensorIDList);
            if (CollUtil.isEmpty(videoDeviceWithSensorIDInfos)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测点绑定的传感器未绑定视频设备");
            }
            withSensorIDInfo = videoDeviceWithSensorIDInfos.get(0);
            Byte accessPlatform = withSensorIDInfo.getAccessPlatform();
            if (accessPlatform.equals(AccessPlatformType.YING_SHI.getValue()) && direction > 11 && direction < 16) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "萤石摄像头设备暂不支持该方向");
            } else if (accessPlatform.equals(AccessPlatformType.HAI_KANG.getValue()) && !HikPtzCommandEnum.isValidHikPtzCommandEnum(direction)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "海康摄像头设备暂不支持该方向");
            }

            liveInfos.forEach(pojo -> {
                if (accessPlatform.equals(AccessPlatformType.YING_SHI.getValue())) {
                    pojo.setSeqNo(withSensorIDInfo.getDeviceSerial());
                    pojo.setYsChannelNo(String.valueOf(withSensorIDInfo.getChannelNo()));
                }
            });
        } else {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
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
