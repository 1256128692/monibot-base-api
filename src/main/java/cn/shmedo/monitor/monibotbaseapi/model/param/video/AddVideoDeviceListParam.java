package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AccessPlatformType;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class AddVideoDeviceListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司不能为空")
    private Integer companyID;

    @NotEmpty(message = "设备信息不能为空")
    private List<VideoDeviceBaseInfo> addVideoList;

    @JsonIgnore
    private Byte accessPlatform;

    @JsonIgnore
    private CurrentSubject currentSubject;

    @Override
    public ResultWrapper validate() {
        long accessPlatformCount = addVideoList.stream().map(VideoDeviceBaseInfo::getAccessPlatform).distinct().count();
        if (accessPlatformCount > 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "单次只能添加同一种接入平台的数据");
        }

        currentSubject = CurrentSubjectHolder.getCurrentSubject();

        List<Byte> accessPlatformList = addVideoList.stream().map(VideoDeviceBaseInfo::getAccessPlatform).collect(Collectors.toList());
        accessPlatform = accessPlatformList.get(0);

        // 1.萤石云协议要校验验证码都不能为空
        // 2.萤石云数据要校验接入协议都必须存在
        if (accessPlatform == AccessPlatformType.YING_SHI.getValue()) {
            List<VideoDeviceBaseInfo> accessPlatformZeroDevices = addVideoList.stream()
                    .filter(device -> device.getAccessPlatform() == 0)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(accessPlatformZeroDevices)) {
                long emptyVerificationCodeCount = accessPlatformZeroDevices.stream()
                        .filter(device -> StringUtils.isEmpty(device.getValidateCode()))
                        .count();

                long accessProtocolCount = accessPlatformZeroDevices.stream()
                        .filter(device -> device.getAccessProtocol() == null).count();

                if (emptyVerificationCodeCount > 0) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "接入平台为萤石云时，验证码不能为空");
                }

                if (accessProtocolCount > 0) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "接入平台为萤石云时，接入协议不能为空");
                }
            }
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
