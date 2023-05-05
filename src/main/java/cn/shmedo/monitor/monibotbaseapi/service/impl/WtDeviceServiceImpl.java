package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnRuleMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceSimpleBySenderAddressParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryProductSimpleParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryWtDevicePageListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.SimpleDeviceV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.Device4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;
import cn.shmedo.monitor.monibotbaseapi.service.WtDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
    private final TbMonitorPointMapper tbMonitorPointMapper;
    private final TbSensorMapper tbSensorMapper;
    private final TbWarnRuleMapper tbWarnRuleMapper;

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

    @Override
    public PageUtil.Page<Device4Web> queryWtDevicePageList(QueryWtDevicePageListParam pa) {
        List<Integer> projectIDList = CollectionUtils.isEmpty(pa.getProjectIDList())
                ? pa.getProjectInfos().stream().map(TbProjectInfo::getID).toList()
                : pa.getProjectIDList();
        QueryDeviceSimpleBySenderAddressParam request = QueryDeviceSimpleBySenderAddressParam.builder()
                .companyID(pa.getCompanyID())
                .sendType(SendType.MDMBASE.toInt())
                .sendAddressList(projectIDList.stream().map(String::valueOf).toList())
                .sendEnable(true)
                .deviceToken(null)
                .build();
        ResultWrapper<List<SimpleDeviceV5>> result = iotService.queryDeviceSimpleBySenderAddress(request);
        if (!result.apiSuccess()) {
            throw new CustomBaseException(result.getCode(), result.getMsg());
        }
        List<SimpleDeviceV5> origenData = result.getData();
        if (CollectionUtils.isEmpty(origenData)) {
            return PageUtil.Page.empty();
        }
        if (pa.getRuleID() != null) {
            TbWarnRule tbWarnRule = tbWarnRuleMapper.selectById(pa.getRuleID());
        }
        Collection<String> uniqueTokens = origenData.stream().map(SimpleDeviceV5::getUniqueToken).collect(Collectors.toSet());

        // tbSensorMapper.querySensorWithMoreBy(uniqueTokens, pa.getCompanyID(), projectIDList,pa.getQueryCode(),pa.getMonitorItemID());
        return null;
    }
}