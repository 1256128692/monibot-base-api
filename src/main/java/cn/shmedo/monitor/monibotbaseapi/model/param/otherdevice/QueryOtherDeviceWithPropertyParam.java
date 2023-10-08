package cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbOtherDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbOtherDevice;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 13:52
 **/
@Data
public class QueryOtherDeviceWithPropertyParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    @JsonAlias("ID")
    private Integer ID;
    @JsonIgnore
    private TbOtherDevice tbOtherDevice;

    @Override
    public ResultWrapper validate() {
        TbOtherDeviceMapper tbOtherDeviceMapper = ContextHolder.getBean(TbOtherDeviceMapper.class);
        tbOtherDevice = tbOtherDeviceMapper.selectById(ID);
        if (tbOtherDevice == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备不存在");
        }
        if (!tbOtherDevice.getCompanyID().equals(companyID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "设备不属于该公司");
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
