package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.video.hk.HkDeviceInfo;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QueryHkVideoDeviceBaseInfoParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    private Integer companyID;

    @NotBlank
    private String deviceSerial;

    @JsonIgnore
    private HkDeviceInfo hkDeviceInfo;


    @Override
    public ResultWrapper validate() {
        HkVideoService hkVideoService = ContextHolder.getBean(HkVideoService.class);
        hkDeviceInfo = hkVideoService.queryDevice(deviceSerial);
        if (hkDeviceInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该设备在海康平台不存在!");
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
