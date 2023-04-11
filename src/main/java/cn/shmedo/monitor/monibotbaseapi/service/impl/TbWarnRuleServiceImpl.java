package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.QueryWtEnginePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtTriggerActionInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtEngineInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnRuleService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnRuleServiceImpl extends ServiceImpl<TbWarnRuleMapper, TbWarnRule> implements ITbWarnRuleService {
    private final TbWarnTriggerMapper tbWarnTriggerMapper;
    private final TbProjectInfoMapper tbProjectInfoMapper;
    private final TbMonitorItemMapper tbMonitorItemMapper;
    private final TbMonitorPointMapper tbMonitorPointMapper;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public PageUtil.Page<WtEngineInfo> queryWtEnginePage(QueryWtEnginePageParam param) {
        List<TbProjectInfo> tbProjectInfos = tbProjectInfoMapper.selectProjectInfoByCompanyID(param.getCompanyID());
        List<Integer> projectIdList = tbProjectInfos.stream().map(TbProjectInfo::getID).toList();
        if (CollectionUtil.isNullOrEmpty(projectIdList)) {
            return PageUtil.Page.empty();
        }
        final Map<Integer, String> projectIdNameMap = tbProjectInfos.stream().collect(Collectors.toMap(TbProjectInfo::getID, TbProjectInfo::getProjectName));
        final Map<Integer, TbMonitorItem> idItemMap = new HashMap<>();
        final Map<Integer, TbMonitorPoint> idPointMap = new HashMap<>();
        IPage<TbWarnRule> page = this.baseMapper.selectWarnRuleByPage(new Page<>(param.getCurrentPage(), param.getPageSize()), param, projectIdList);
        List<TbWarnRule> records = page.getRecords();
        List<Integer> ruleIds = records.stream().peek(u -> {
            idItemMap.put(u.getMonitorItemID(), null);
            idPointMap.put(u.getMonitorPointID(), null);
        }).map(TbWarnRule::getID).toList();
        this.tbMonitorItemMapper.selectBatchIds(idItemMap.keySet()).stream().peek(u -> idItemMap.put(u.getID(), u)).collect(Collectors.toList());
        this.tbMonitorPointMapper.selectBatchIds(idPointMap.keySet()).stream().peek(u -> idPointMap.put(u.getID(), u)).collect(Collectors.toList());
        Map<Integer, List<WtTriggerActionInfo>> engineIdWarnMap = Optional.of(ruleIds).filter(u -> u.size() > 0).map(tbWarnTriggerMapper::queryWarnStatusByEngineIds)
                .map(w -> w.stream().collect(Collectors.toMap(WtTriggerActionInfo::getWarnID, info -> info.setAction(info))))
                .map(Map::values).map(t -> t.stream().collect(Collectors.groupingBy(WtTriggerActionInfo::getEngineID))).orElse(new HashMap<>());
        List<WtEngineInfo> collect = records.stream().map(u -> WtEngineInfo.build(u).setProjectName(Optional.ofNullable(projectIdNameMap.get(u.getProjectID())).orElse("--"))
                .setMonitorItemName(Optional.ofNullable(idItemMap.get(u.getMonitorItemID())).map(TbMonitorItem::getName).orElse("--"))
                .setMonitorPointName(Optional.ofNullable(idPointMap.get(u.getMonitorPointID())).map(TbMonitorPoint::getName).orElse("--"))
                .setDataList(Optional.ofNullable(engineIdWarnMap.get(u.getID())).map(w -> w.stream().map(WtTriggerActionInfo::build)
                        .collect(Collectors.toList())).orElse(new ArrayList<>()))).collect(Collectors.toList());
        return new PageUtil.Page<>(page.getPages(), collect, page.getTotal());
    }
}
