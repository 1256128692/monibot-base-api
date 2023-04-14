package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnActionMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnActionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class TbWarnActionServiceImpl extends ServiceImpl<TbWarnActionMapper, TbWarnAction> implements ITbWarnActionService {
}
