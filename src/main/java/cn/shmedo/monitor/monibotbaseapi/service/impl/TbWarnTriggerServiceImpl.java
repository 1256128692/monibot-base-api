package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnActionMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnTriggerMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnTrigger;
import cn.shmedo.monitor.monibotbaseapi.model.response.wtengine.WtTriggerActionInfo;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnTriggerService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TbWarnTriggerServiceImpl extends ServiceImpl<TbWarnTriggerMapper, TbWarnTrigger> implements ITbWarnTriggerService {
    private final TbWarnActionMapper tbWarnActionMapper;

    @Override
    public List<WtTriggerActionInfo> queryWarnStatusByEngineIds(List<Integer> engineIds) {
        return this.baseMapper.queryWarnStatusByEngineIds(engineIds);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteWarnStatusByIDList(List<Integer> list) {
        List<TbWarnTrigger> tbWarnTriggers = this.baseMapper.selectBatchIds(list);
        deleteWarnStatusByList(tbWarnTriggers);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteWarnStatusByList(List<TbWarnTrigger> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            this.baseMapper.deleteBatchIds(list);
            List<Integer> triggerIDList = list.stream().map(TbWarnTrigger::getID).toList();
            tbWarnActionMapper.delete(new LambdaQueryWrapper<TbWarnAction>().in(TbWarnAction::getTriggerID, triggerIDList));
        }
    }
}
