package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTriggerConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLevelAlias;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryThresholdBaseConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.ThresholdBaseConfigInfo;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 16:58
 */
public interface IWarnConfigService {
    ThresholdBaseConfigInfo queryThresholdBaseConfig(QueryThresholdBaseConfigParam param, TbWarnBaseConfig tbWarnBaseConfig);

    void updateThresholdBaseConfig(TbTriggerConfig tbTriggerConfig, List<TbWarnLevelAlias> tbWarnLevelAliasList, Integer userID);
}
