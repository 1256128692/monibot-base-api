package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceIntelLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbDeviceIntelLocationMapper extends BaseMapper<TbDeviceIntelLocation> {
    void updateByTypeAndToken(TbDeviceIntelLocation tbDeviceIntelLocation);
}