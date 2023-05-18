package cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * @author Chengfs on 2023/5/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryDeviceDetailParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty
    private Set<Integer> projectIDList;

    @NotBlank
    private String deviceToken;

    @JsonIgnore
    private SimpleDeviceV5 device;

    @Override
    public ResultWrapper<?> validate() {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        IotService iotService = SpringUtil.getBean(IotService.class);
        ResultWrapper<List<SimpleDeviceV5>> wrapper = iotService
                .queryDeviceSimpleBySenderAddress(QueryDeviceSimpleBySenderAddressParam.builder()
                        .companyID(subject.getCompanyID())
                        .deviceToken(deviceToken)
                        .sendType(SendType.MDMBASE.toInt())
                        .sendAddressList(projectIDList.stream().map(String::valueOf).toList())
                        .build());
        Assert.isTrue(wrapper.apiSuccess(), wrapper.getMsg());
        Assert.isTrue(CollUtil.isNotEmpty(wrapper.getData()), "设备不存在");
        device = wrapper.getData().get(0);

        return null;
    }

    @Override
    public List<Resource> parameter() {
        return projectIDList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}