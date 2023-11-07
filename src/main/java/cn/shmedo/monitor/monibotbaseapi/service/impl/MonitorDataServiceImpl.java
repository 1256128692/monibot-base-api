package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorDataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
