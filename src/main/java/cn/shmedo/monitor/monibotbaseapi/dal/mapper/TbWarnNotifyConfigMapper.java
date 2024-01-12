package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:12
 */
public interface TbWarnNotifyConfigMapper extends BasicMapper<TbWarnNotifyConfig> {
    WarnNotifyConfigDetail selectWarnNotifyConfigDetailByID(Integer id);
}
