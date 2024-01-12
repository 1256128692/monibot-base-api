package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.GetLocationParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.ListAreaCodeByLocationNameParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RegionAreaService extends IService<RegionArea> {

    Object getLocationById(GetLocationParam param);

    Object listAreaCodeByLocationName(ListAreaCodeByLocationNameParam param);
}
