package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.Device4Web;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.ProductSimple;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.DeviceDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice.WtVideoPageInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.Collection;
import java.util.List;

public interface WtDeviceService {

    Collection<ProductSimple> productSimpleList(QueryProductSimpleParam param);

    PageUtil.PageWithMap<Device4Web> queryWtDevicePageList(QueryWtDevicePageListParam pa);

    PageUtil.PageWithMap<WtVideoPageInfo> queryWtVideoPageList(QueryWtVideoPageParam param);

    List<Device4Web> exportWtDevice(ExportWtDeviceParam pa);

    List<WtVideoPageInfo> exportWtVideo(ExportWtVideoParam param);

    Object queryWtVideoTypeList(QueryWtVideoTypeParam param);

    DeviceDetail deviceDetail(QueryDeviceDetailParam param);
}
