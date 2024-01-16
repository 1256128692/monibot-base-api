package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.MonitorWithThresholdConfigCountInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnThresholdConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:49
 */
@Service
@RequiredArgsConstructor
public class TbWarnThresholdConfigServiceImpl extends ServiceImpl<TbWarnThresholdConfigMapper, TbWarnThresholdConfig> implements ITbWarnThresholdConfigService {
    @Override
    public List<MonitorWithThresholdConfigCountInfo> queryMonitorWithThresholdConfigCountByProjectID(Integer projectID) {
        return baseMapper.selectMonitorWithThresholdConfigCountByProjectID(projectID);
    }
}
