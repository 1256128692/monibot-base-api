package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectMonitorClassMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateWtMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorClassService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@EnableTransactionManagement
@Service
@AllArgsConstructor
public class MonitorClassServiceImpl implements MonitorClassService {

    private final TbProjectMonitorClassMapper tbProjectMonitorClassMapper;

    private final TbMonitorItemMapper tbMonitorItemMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateWtMonitorClass(UpdateWtMonitorClassParam request) {

        LambdaQueryWrapper<TbProjectMonitorClass> wrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                .eq(TbProjectMonitorClass::getProjectID, request.getProjectID())
                .eq(TbProjectMonitorClass::getMonitorClass, request.getMonitorClass());

        TbProjectMonitorClass tbProjectMonitorClass = tbProjectMonitorClassMapper.selectOne(wrapper);
        if (tbProjectMonitorClass != null) {
            // 存在则更新
            tbProjectMonitorClassMapper.updateByCondition(request);
        } else {
            int i = 10;
            // 不存在则新增
            tbProjectMonitorClassMapper.insertByCondition(request);
        }

        // 先把之前的该监测类别和工程的数据的监测类别全部置空
        tbMonitorItemMapper.updateMonitorClassToNull(request.getProjectID(), request.getMonitorClass());

        // 然后再去绑定新的监测类别与监测项目
        tbMonitorItemMapper.updateByCondition(request.getProjectID(), request.getMonitorClass(), request.getMonitorItemIDList());
    }

    @Override
    public List<TbProjectMonitorClass> queryMonitorClassList(QueryMonitorClassParam request) {

        LambdaQueryWrapper<TbProjectMonitorClass> wrapper = new LambdaQueryWrapper<TbProjectMonitorClass>()
                .eq(TbProjectMonitorClass::getProjectID, request.getProjectID());
        if (request.getEnable() != null) {
            wrapper.eq(TbProjectMonitorClass::getEnable, request.getEnable());
        }
        return tbProjectMonitorClassMapper.selectList(wrapper);
    }
}
