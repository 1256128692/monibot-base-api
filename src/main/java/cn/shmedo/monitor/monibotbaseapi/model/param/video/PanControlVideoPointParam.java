package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorQueryType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoDeviceWithSensorIDInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class PanControlVideoPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    private Integer projectID;
    private Integer monitorPointID;

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
        TbProjectMonitorClass projectMonitorClass = projectMonitorClassMapper.selectOne(wrapper);
        if (projectMonitorClass == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测工程未配置监测类型归属");
        }

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

            liveInfos.forEach(pojo -> {
                if (accessPlatform.equals(AccessPlatformType.YING_SHI.getValue())) {
                    String exValues = pojo.getExValues();
                    Dict dict = JSONUtil.toBean(exValues, Dict.class);
                    if (dict.get("protocol") != null) {
                        pojo.setProtocol(dict.get("protocol").toString());
                    }
                    if (dict.get("seqNo") != null) {
                        pojo.setSeqNo(dict.get("seqNo").toString());
                    }
                    if (dict.get("ysChannelNo") != null) {
                        pojo.setYsChannelNo(dict.get("ysChannelNo").toString());
                    }
                }
            });
        }

        // TODO 加上参数方向的校验

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
