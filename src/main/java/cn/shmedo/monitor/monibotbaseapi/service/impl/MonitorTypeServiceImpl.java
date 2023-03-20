package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-20 16:54
 **/
@Service("monitorTypeService")
public class MonitorTypeServiceImpl extends ServiceImpl<TbMonitorTypeMapper, TbMonitorType> implements MonitorTypeService {
}
