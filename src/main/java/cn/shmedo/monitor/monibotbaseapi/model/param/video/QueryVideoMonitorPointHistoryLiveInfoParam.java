package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorQueryType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.video.VideoMonitorPointLiveInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class QueryVideoMonitorPointHistoryLiveInfoParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {


    private Integer projectID;
    private Integer monitorPointID;
    private Date beginTime;
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
                String configFieldValue = pojo.getConfigFieldValue();
                Dict dict = JSONUtil.toBean(configFieldValue, Dict.class);
                String protocol = dict.get("protocol").toString();
                String seqNo = dict.get("seqNo").toString();
                String ysChannelNo = dict.get("ysChannelNo").toString();

                pojo.setProtocol(protocol);
                pojo.setSeqNo(seqNo);
                pojo.setYsChannelNo(ysChannelNo);
            });
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
