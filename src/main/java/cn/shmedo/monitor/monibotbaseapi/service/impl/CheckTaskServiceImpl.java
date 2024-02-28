package cn.shmedo.monitor.monibotbaseapi.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.service.CheckTaskService;

@Service
public class CheckTaskServiceImpl extends ServiceImpl<TbCheckTaskMapper, TbCheckTask> implements CheckTaskService {

}
