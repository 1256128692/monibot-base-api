package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.sluice.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.sluice.GateSimple;
import cn.shmedo.monitor.monibotbaseapi.model.response.sluice.Sluice;
import cn.shmedo.monitor.monibotbaseapi.model.response.sluice.SluiceInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.sluice.SluiceSimple;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;

import java.util.List;

public interface SluiceService {

    PageUtil.Page<Sluice> sluicePage(QuerySluicePageRequest request);

    void sluiceControl(SluiceControlRequest request);

    SluiceInfo sluiceSingle(SingleSluiceRequest request);

    List<SluiceSimple> listSluiceSimple(ListSluiceRequest request);

    List<String> listSluiceManageUnit(ListSluiceManageUnitRequest request);

    List<GateSimple> listSluiceGate(ListSluiceGateRequest request);
}