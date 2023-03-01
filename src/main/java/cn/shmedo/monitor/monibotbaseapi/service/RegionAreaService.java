package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.GetLocationParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RegionAreaService extends IService<RegionArea> {

    Object getLocationById(GetLocationParam param);
}
