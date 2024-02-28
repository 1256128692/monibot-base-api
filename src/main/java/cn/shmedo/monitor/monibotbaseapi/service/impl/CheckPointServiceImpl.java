package cn.shmedo.monitor.monibotbaseapi.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.service.CheckPointService;

@Service
public class CheckPointServiceImpl extends ServiceImpl<TbCheckPointMapper, TbCheckPoint> implements CheckPointService {

}
