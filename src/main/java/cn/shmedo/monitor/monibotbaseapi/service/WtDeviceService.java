package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryProductSimpleParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryWtDevicePageListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.Device4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.Collection;

public interface WtDeviceService {

    Collection<ProductSimple> productSimpleList(QueryProductSimpleParam param);

    PageUtil.Page<Device4Web> queryWtDevicePageList(QueryWtDevicePageListParam pa);
}
