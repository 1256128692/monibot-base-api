package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataWarnLogMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnThresholdConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLogHistory;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.DataWarnDto;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnThresholdConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnCase;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevelStyle;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnTag;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDataWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnBaseConfigService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnCase.*;

/**
 * @author Chengfs on 2024/1/12
 */
@Service
@RequiredArgsConstructor
public class TbDataWarnLogServiceImpl extends ServiceImpl<TbDataWarnLogMapper, TbDataWarnLog> implements ITbDataWarnLogService {

    private final String WARN_CONTENT_FORMAT = "{} 内 {} {} 发生 {} — {}，实测数据：{} {} {}，请关注！";

    private final TbWarnThresholdConfigMapper thresholdConfigMapper;
    private final ITbWarnBaseConfigService baseConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDataWarnLog(List<DataWarnDto> request) {
        List<Integer> tidSet = request.stream().map(DataWarnDto::getThresholdID).toList();

        //查询已存在的报警
        Map<Integer, TbDataWarnLog> alreadyExists = this.list(Wrappers.<TbDataWarnLog>lambdaQuery()
                        .in(TbDataWarnLog::getWarnThresholdID, tidSet).ne(TbDataWarnLog::getDealStatus, 2)
                        .select(TbDataWarnLog::getId, TbDataWarnLog::getWarnThresholdID, TbDataWarnLog::getWarnLevel))
                .stream().collect(Collectors.toMap(TbDataWarnLog::getWarnThresholdID, e -> e));

        //查询阈值配置详情
        Map<Integer, WarnThresholdConfigInfo> group = thresholdConfigMapper.listInfoById(tidSet).stream()
                .collect(Collectors.toMap(WarnThresholdConfigInfo::getId, e -> e));

        //区分报警类型
        Map<DataWarnCase, List<DataWarnDto>> warnCaseGroup = request.stream()
                .filter(e -> group.containsKey(e.getThresholdID()))
                .peek(e -> e.perfect(group.get(e.getThresholdID())))
                .map(e -> {
                    TbDataWarnLog existLog = alreadyExists.get(e.getThresholdID());
                    if (existLog != null) {
                        if (Objects.equals(existLog.getWarnLevel(), e.getWarnLevel())) {
                            //等级未变更，更新报警时间，记录历史
                            return Tuples.of(SAME, e);
                        } else if (existLog.getWarnLevel() < e.getWarnLevel()) {
                            //报警升级，更新报警时间和内容，发送变更通知，记录历史
                            return Tuples.of(UPGRADE, e);
                        } else {
                            //报警降级，更新报警时间和内容，记录历史
                            return Tuples.of(DOWNLEVEL, e);
                        }
                    } else {
                        //不存在报警，生成报警记录，发送通知
                        return Tuples.of(NEW, e);
                    }
                }).collect(Collectors.groupingBy(Tuple2::getT1, Collectors.mapping(Tuple2::getT2, Collectors.toList())));


        //报警历史记录
        List<TbDataWarnLogHistory> historyList = warnCaseGroup.entrySet().stream()
                .flatMap(entry -> NEW.equals(entry.getKey()) ? Stream.empty() :
                        entry.getValue().stream().map(e -> {
                            TbDataWarnLog existLog = alreadyExists.get(e.getThresholdID());
                            TbDataWarnLogHistory history = new TbDataWarnLogHistory();
                            history.setWarnLogID(existLog.getId());
                            history.setWarnLevel(existLog.getWarnLevel());
                            history.setWarnTime(existLog.getWarnTime());
                            return history;
                        })).toList();

        //获取报警基础设置 生成报警内容
        Map<Integer, Map<Integer, TbWarnBaseConfig>> baseConfigMap = baseConfigService
                .groupByCompanyIDAndPlatform(warnCaseGroup.entrySet().stream()
                        .filter(entry -> !SAME.equals(entry.getKey()))
                        .flatMap(e -> e.getValue().stream())
                        .map(i -> Tuples.of(i.getCompanyID(), i.getPlatform()))
                        .distinct().toList());
        warnCaseGroup.entrySet().stream()
                .filter(entry -> !SAME.equals(entry.getKey()))
                .flatMap(e -> e.getValue().stream()).forEach(e -> {
                    TbWarnBaseConfig config = baseConfigMap.getOrDefault(e.getCompanyID(), Map.of())
                            .getOrDefault(e.getPlatform(), baseConfigService.getDefault(e.getCompanyID(), e.getPlatform()));

                    WarnTag warnTag = WarnTag.fromCode(config.getWarnTag());
                    WarnLevelStyle style = WarnLevelStyle.fromCode(config.getWarnLevelStyle());
                    String levelStr = style.getDesc().split(StrUtil.COMMA)[e.getWarnLevel()] + warnTag.getDesc();

                    String content = StrUtil.format(WARN_CONTENT_FORMAT, e.getProjectName(), e.getMonitorItemName(),
                            DateUtil.format(e.getWarnTime(), DatePattern.NORM_DATETIME_FORMAT),
                            e.getWarnName(), levelStr, e.getFieldName(), e.getWarnValue(), e.getFieldUnitEng());
                    e.setWarnContent(content);
                });
        baseConfigMap.clear();

        //报警记录
        List<TbDataWarnLog> warnLogList = warnCaseGroup.entrySet().stream()
                .flatMap(entry -> switch (entry.getKey()) {
                    case NEW -> entry.getValue().stream().map(DataWarnDto::toTbDataWarnLog);
                    case SAME -> entry.getValue().stream().map(e -> {
                        TbDataWarnLog item = alreadyExists.get(e.getThresholdID());
                        item.setWarnTime(e.getWarnTime());
                        return item;
                    });
                    case UPGRADE, DOWNLEVEL -> entry.getValue().stream().map(e -> {
                        TbDataWarnLog item = alreadyExists.get(e.getThresholdID());
                        item.setWarnTime(e.getWarnTime());
                        item.setWarnContent(e.getWarnContent());
                        item.setWarnLevel(e.getWarnLevel());
                        return item;
                    });
                }).toList();

        //TODO 通知处理、入库
    }

}