package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.QueryEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.UpdateEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue.EigenValueInfoV1;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorDataService;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MonitorDataServiceImpl implements MonitorDataService {

    private final TbEigenValueMapper tbEigenValueMapper;

    private final TbEigenValueRelationMapper tbEigenValueRelationMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addEigenValue(AddEigenValueParam pa) {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbEigenValue tbEigenValue = AddEigenValueParam.toNewVo(pa, subjectID);
        tbEigenValueMapper.insertSelective(tbEigenValue);
        tbEigenValueRelationMapper.insertBatch(pa.getMonitorPointIDList(), tbEigenValue.getId());
    }

    @Override
    public Object queryEigenValueList(QueryEigenValueParam pa) {

        List<EigenValueInfoV1> eigenValueInfoV1List = tbEigenValueMapper.selectListByCondition(pa.getMonitorItemID(), pa.getProjectID(), pa.getMonitorPointIDList());

        if (CollectionUtil.isNullOrEmpty(eigenValueInfoV1List)) {
            return Collections.emptyList();
        }

        eigenValueInfoV1List.forEach(item -> {
            item.setScopeStr(ScopeType.getDescriptionByCode(item.getScope()));
        });

        return eigenValueInfoV1List;
    }

    @Override
    public void updateEigenValue(UpdateEigenValueParam pa) {
        Integer subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        TbEigenValue tbEigenValue = UpdateEigenValueParam.toNewVo(pa, subjectID);
        tbEigenValueMapper.updateByPrimaryKeySelective(tbEigenValue);

        // 删除之前关系,重新绑定
        tbEigenValueRelationMapper.deleteByEigenValueID(pa.getEigenValueID());
        tbEigenValueRelationMapper.insertBatch(pa.getMonitorPointIDList(), pa.getEigenValueID());
    }
}
