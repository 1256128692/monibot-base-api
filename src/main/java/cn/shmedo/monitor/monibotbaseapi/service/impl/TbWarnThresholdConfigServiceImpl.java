package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnThresholdConfigListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorTypeFieldV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.MonitorWithThresholdConfigCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnThresholdConfigListInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnThresholdMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnThresholdConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:49
 */
@Slf4j
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public WarnThresholdConfigListInfo queryWarnThresholdConfigList(QueryWarnThresholdConfigListParam param,
                                                                    TbWarnBaseConfig tbWarnBaseConfig) {
        WarnThresholdConfigListInfo info = new WarnThresholdConfigListInfo();
        BeanUtil.copyProperties(tbWarnBaseConfig, info);
        List<WarnThresholdMonitorPointInfo> dataList = this.baseMapper.selectWarnThresholdConfigList(param);
        final Boolean status = param.getStatus();
        if (Objects.nonNull(status)) {
            // 只要配置了某一级报警阈值,就是'已配置'
            final Function<String, Boolean> func = value -> {
                try {
                    return Optional.ofNullable(value).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj).map(MapWrapper::entrySet)
                            .filter(CollUtil::isNotEmpty).map(u -> status != u.stream().anyMatch(w -> Optional.ofNullable(w.getValue())
                                    .filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj).map(MapWrapper::entrySet)
                                    .filter(CollUtil::isNotEmpty).map(s -> s.stream().map(Map.Entry::getValue).allMatch(n ->
                                            ObjectUtil.isNotEmpty(n) && !"null".equals(n))).orElse(false))).orElse(status);
                } catch (JSONException e) {
                    // 解析异常,始终清空
                    log.error("parse json error, threshold value: {}", value);
                    return true;
                }
            };
            dataList.stream().peek(u -> u.getSensorList().stream().peek(w -> w.getFieldList().stream().peek(s ->
                    s.dealStatusFilter(func)).toList()).toList()).toList();
        }
        info.setDataList(dataList);
        return info;
    }
}
