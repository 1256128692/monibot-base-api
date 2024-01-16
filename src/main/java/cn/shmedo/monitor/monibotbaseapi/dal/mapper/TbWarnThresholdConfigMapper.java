package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnThresholdConfig;
import jakarta.annotation.Nonnull;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:20
 */
public interface TbWarnThresholdConfigMapper extends BasicMapper<TbWarnThresholdConfig> {

    WarnThresholdConfig getInfoById(@Nonnull Integer id);
}
