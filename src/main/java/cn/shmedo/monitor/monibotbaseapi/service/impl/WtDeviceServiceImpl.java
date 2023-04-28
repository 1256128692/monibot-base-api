package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryProductSimpleParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;
import cn.shmedo.monitor.monibotbaseapi.service.WtDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 水利设备服务
 *
 * @author Chengfs on 2023/4/28
 */
@Component
@AllArgsConstructor
public class WtDeviceServiceImpl implements WtDeviceService {

    private final IotService iotService;

    @Override
    public Collection<ProductSimple> productSimpleList(QueryProductSimpleParam param) {
        Collection<Integer> projectList = PermissionUtil.getHavePermissionProjectList(param.getCompanyID());
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(param.getCompanyID())
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(projectList.stream().map(String::valueOf).toList())
                .sendEnable(param.getIsEnable())
                .deviceToken(param.getDeviceToken())
                .build();
        ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);
        return result.apiSuccess() ?
                result.getData().stream().map(e -> ProductSimple.builder()
                        .productID(e.getProductID())
                        .productName(e.getProductName()).build()).collect(Collectors.toSet()) :
                Collections.emptyList();
    }
}