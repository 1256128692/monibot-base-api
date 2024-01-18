package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTriggerConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnLevelAliasMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryThresholdBaseConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.ThresholdBaseConfigFieldInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.ThresholdBaseConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 16:58
 */
@Service
@RequiredArgsConstructor
public class WarnConfigServiceImpl implements IWarnConfigService {
    private final ITbTriggerConfigService tbTriggerConfigService;
    private final ITbWarnLevelAliasService tbWarnLevelAliasService;

    @Override
    public ThresholdBaseConfigInfo queryThresholdBaseConfig(QueryThresholdBaseConfigParam param, TbWarnBaseConfig tbWarnBaseConfig) {
        ThresholdBaseConfigInfo info = new ThresholdBaseConfigInfo();
        BeanUtil.copyProperties(tbWarnBaseConfig, info);
        Optional.of(tbTriggerConfigService.list(new LambdaQueryWrapper<TbTriggerConfig>()
                        .eq(TbTriggerConfig::getPlatform, param.getPlatform()).eq(TbTriggerConfig::getProjectID, param.getProjectID())
                        .eq(TbTriggerConfig::getMonitorItemID, param.getMonitorItemID()))).filter(CollUtil::isNotEmpty)
                .flatMap(u -> u.stream().findAny()).ifPresent(u -> {
                    Integer triggerTimes = u.getTriggerTimes();
                    info.setTriggerType(triggerTimes == -1 ? 1 : 2);
                    info.setTriggerTimes(triggerTimes == -1 ? 1 : triggerTimes);
                });
        List<ThresholdBaseConfigFieldInfo> fieldList = ((TbWarnLevelAliasMapper) tbWarnLevelAliasService.getBaseMapper())
                .selectThresholdBaseConfigFieldInfoList(param.getPlatform(), param.getMonitorItemID());
        info.setFieldList(fieldList);
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateThresholdBaseConfig(TbTriggerConfig tbTriggerConfig, List<TbWarnLevelAlias> tbWarnLevelAliasList, Integer userID) {
        Optional.ofNullable(tbTriggerConfig).ifPresent(u -> {
            if (Objects.isNull(u.getCreateUserID())) {
                u.setCreateUserID(userID);
            }
            u.setUpdateUserID(userID);
            this.tbTriggerConfigService.saveOrUpdate(u);
        });
        Optional.ofNullable(tbWarnLevelAliasList).filter(CollUtil::isNotEmpty).ifPresent(u -> {
            List<TbWarnLevelAlias> list = u.stream().peek(w -> {
                if (Objects.isNull(w.getCreateUserID())) {
                    w.setCreateUserID(userID);
                }
                w.setUpdateUserID(userID);
            }).toList();
            this.tbWarnLevelAliasService.saveOrUpdateBatch(list);
        });
    }
}
