package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.QueryProductSimpleParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;

import java.util.Collection;

public interface WtDeviceService {

    Collection<ProductSimple> productSimpleList(QueryProductSimpleParam param);
}
