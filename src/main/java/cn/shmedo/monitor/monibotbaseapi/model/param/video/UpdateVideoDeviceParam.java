package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UpdateVideoDeviceParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    @NotEmpty
    private List<@NotNull VideoDeviceInfoV2> updateVideoList;


    @JsonIgnore
    private List<TbVideoDevice> videoDeviceList = new ArrayList<>();

    @Override
    public ResultWrapper validate() {

        List<String> deviceSerialList = updateVideoList.stream().map(VideoDeviceInfoV2::getDeviceSerial).collect(Collectors.toList());

        TbVideoDeviceMapper tbVideoDeviceMapper = ContextHolder.getBean(TbVideoDeviceMapper.class);
        LambdaQueryWrapper<TbVideoDevice> wrapper = new LambdaQueryWrapper<TbVideoDevice>()
                .in(TbVideoDevice::getDeviceSerial, deviceSerialList);
        videoDeviceList = tbVideoDeviceMapper.selectList(wrapper);
        if (CollectionUtil.isNullOrEmpty(videoDeviceList) || videoDeviceList.size() != updateVideoList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前请求中有视频设备不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
