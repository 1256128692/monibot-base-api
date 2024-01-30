package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CompareMode;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryMonitorWithThresholdConfigCountParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnThresholdConfigListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.UpdateWarnThresholdConfigEnableBatchParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorTypeFieldV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.MonitorWithThresholdConfigCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnFieldThresholdConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnThresholdConfigListInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnThresholdMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IThresholdConfigValueCheck;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnThresholdConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-16 15:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TbWarnThresholdConfigServiceImpl extends ServiceImpl<TbWarnThresholdConfigMapper, TbWarnThresholdConfig> implements ITbWarnThresholdConfigService, IThresholdConfigValueCheck {
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public List<MonitorWithThresholdConfigCountInfo> queryMonitorWithThresholdConfigCountByProjectID(QueryMonitorWithThresholdConfigCountParam param) {
        List<MonitorWithThresholdConfigCountInfo> infoList = baseMapper.selectMonitorWithThresholdConfigCountByProjectID(param.getProjectID(), param.getPlatform());
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
        List<WarnThresholdMonitorPointInfo> dataList = this.baseMapper.selectWarnThresholdConfigList(param)
                .stream().peek(u -> u.getSensorList().stream().peek(w -> w.getFieldList().stream().peek(s -> {
                    if (Objects.isNull(s.getConfigID())) {
                        s.setWarnName(s.getFieldName() + "异常");
                        s.setCompareMode(CompareMode.GT.getCode());
                    }
                }).toList()).toList()).toList();
        final Boolean status = param.getStatus();
        if (Objects.nonNull(status)) {
            final Function<String, Boolean> func = value -> {
                try {
                    return queryConfigStatus(value, status);
                } catch (JSONException e) {
                    // 解析异常,始终排除
                    log.error("parse json error, threshold value: {}", value);
                    return false;
                }
            };
            dataList.stream().peek(u -> u.getSensorList().stream().peek(w -> w.setFieldList(
                    w.getFieldList().stream().filter(s -> func.apply(s.getValue())).toList())).toList()).toList();
        }
        info.setDataList(dataList);
        return info;
    }

    @Override
    public void updateWarnThresholdConfigEnableBatch(UpdateWarnThresholdConfigEnableBatchParam param, Integer userID) {
        Date updateTime = new Date();
        Boolean enable = param.getEnable();
        List<Integer> configIDList = queryWarnThresholdConfigList(param, null).getDataList().stream()
                .map(u -> u.getSensorList().stream().map(w -> w.getFieldList().stream()
                        .map(WarnFieldThresholdConfigInfo::getConfigID).toList()).toList())
                .flatMap(Collection::stream).flatMap(Collection::stream).toList();

        Optional.of(configIDList).map(u -> new LambdaQueryWrapper<TbWarnThresholdConfig>().in(TbWarnThresholdConfig::getId, u)
                .ne(TbWarnThresholdConfig::getEnable, enable)).map(this::list).filter(CollUtil::isNotEmpty).ifPresent(updateList -> {
            if (enable) {
                final Function<String, Boolean> func = value -> {
                    try {
                        return queryConfigStatus(value, true);
                    } catch (JSONException e) {
                        // 解析异常,始终排除
                        log.error("parse json error, threshold value: {}", value);
                        return false;
                    }
                };
                updateList = updateList.stream().filter(u -> func.apply(u.getValue())).toList();
            }
            Optional.of(updateList).filter(CollUtil::isNotEmpty).map(u -> u.stream().map(TbWarnThresholdConfig::getId).toList())
                    .ifPresent(u -> this.update(new LambdaUpdateWrapper<TbWarnThresholdConfig>()
                            .in(TbWarnThresholdConfig::getId, u).set(TbWarnThresholdConfig::getEnable, enable)
                            .set(TbWarnThresholdConfig::getUpdateUserID, userID).set(TbWarnThresholdConfig::getUpdateTime, updateTime)));
        });
    }
}
