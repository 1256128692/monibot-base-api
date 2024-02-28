package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.AddEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.service.CheckEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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


}
