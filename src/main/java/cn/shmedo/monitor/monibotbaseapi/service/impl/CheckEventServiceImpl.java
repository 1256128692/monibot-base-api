package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckEventMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.service.CheckEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CheckEventServiceImpl extends ServiceImpl<TbCheckEventMapper, TbCheckEvent> implements CheckEventService {

}
