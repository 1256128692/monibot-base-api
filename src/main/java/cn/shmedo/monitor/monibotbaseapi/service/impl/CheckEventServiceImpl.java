package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEventType;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.AddEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.DeleteEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.QueryEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.UpdateEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.service.CheckEventService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@EnableTransactionManagement
@Service
@AllArgsConstructor
public class CheckEventServiceImpl extends ServiceImpl<TbCheckEventMapper, TbCheckEvent> implements CheckEventService {


    private final TbCheckEventTypeMapper tbCheckEventTypeMapper;

    private final TbSensorMapper tbSensorMapper;


    @Override
    public void addEventType(AddEventTypeParam pa) {
        tbCheckEventTypeMapper.insert(pa.toRawVo());
    }

    @Override
    public void deleteEventType(DeleteEventTypeParam pa) {
        tbCheckEventTypeMapper.deleteBatchIds(pa.getIdList());
    }

    @Override
    public void updateEventType(UpdateEventTypeParam pa) {
        tbCheckEventTypeMapper.updateSelectFieldById(pa);
    }

    @Override
    public Object queryEventTypeList(QueryEventTypeParam pa) {
        QueryWrapper<TbCheckEventType> queryWrapper = new QueryWrapper<>();
        if (!StringUtil.isNullOrEmpty(pa.getName())) {
            queryWrapper.eq("name", pa.getName());
        }
        return tbCheckEventTypeMapper.selectList(queryWrapper);
    }


}
