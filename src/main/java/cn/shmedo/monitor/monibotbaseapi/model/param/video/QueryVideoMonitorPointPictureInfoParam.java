package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorQueryType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class QueryVideoMonitorPointPictureInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "监测工程不能为空")
    private Integer projectID;
    @NotNull(message = "监测点不能为空")
    private Integer monitorPointID;

    @NotNull(message = "开始时间不能为空")
    private Date beginTime;

    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    @JsonIgnore
    private List<VideoMonitorPointLiveInfo> liveInfos;

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
        if (!CollectionUtil.isNullOrEmpty(liveInfos)){
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

            liveInfos.forEach(pojo -> {
                String exValues = pojo.getExValues();
                Dict dict = JSONUtil.toBean(exValues, Dict.class);
                if (dict.get("protocol") != null) {
                    pojo.setProtocol(dict.get("protocol").toString());
                }
                if (dict.get("seqNo") != null) {
                    pojo.setSeqNo(dict.get("seqNo").toString());
                }
                if (dict.get(DefaultConstant.VIDEO_CHANNEL) != null) {
                    pojo.setYsChannelNo(dict.get(DefaultConstant.VIDEO_CHANNEL).toString());
                }
            });
        }

        if (beginTime.after(endTime)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }

        long between = DateUtil.between(beginTime, endTime, DateUnit.DAY);
        if (between > 7) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "最多查询一周范围内的时间");
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
