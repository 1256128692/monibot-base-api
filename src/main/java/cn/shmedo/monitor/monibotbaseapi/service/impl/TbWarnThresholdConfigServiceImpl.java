package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorTypeFieldV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.MonitorWithThresholdConfigCountInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnThresholdConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:49
 */
@Service
@RequiredArgsConstructor
public class TbWarnThresholdConfigServiceImpl extends ServiceImpl<TbWarnThresholdConfigMapper, TbWarnThresholdConfig> implements ITbWarnThresholdConfigService {
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public List<MonitorWithThresholdConfigCountInfo> queryMonitorWithThresholdConfigCountByProjectID(Integer projectID) {
        List<MonitorWithThresholdConfigCountInfo> infoList = baseMapper.selectMonitorWithThresholdConfigCountByProjectID(projectID);
        Optional.of(infoList.stream().map(MonitorItemV1::getItemID).toList()).filter(CollUtil::isNotEmpty)
                .map(tbMonitorTypeFieldMapper::queryMonitorTypeFieldV1ByMonitorItems).map(u ->
                        u.stream().collect(Collectors.groupingBy(MonitorTypeFieldV1::getItemID))).filter(CollUtil::isNotEmpty)
                .ifPresent(u -> infoList.stream().peek(w -> w.setFieldList(u.get(w.getItemID()))).toList());
        return infoList;
    }
}
