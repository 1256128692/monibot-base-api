package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnThresholdConfigListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.MonitorWithThresholdConfigCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnThresholdConfigListInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:48
 */
public interface ITbWarnThresholdConfigService extends IService<TbWarnThresholdConfig> {
    List<MonitorWithThresholdConfigCountInfo> queryMonitorWithThresholdConfigCountByProjectID(Integer projectID);

    WarnThresholdConfigListInfo queryWarnThresholdConfigList(QueryWarnThresholdConfigListParam param, TbWarnBaseConfig tbWarnBaseConfig);
}
