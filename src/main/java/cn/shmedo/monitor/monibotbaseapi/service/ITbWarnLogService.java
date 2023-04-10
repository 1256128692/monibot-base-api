package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.alarm.QueryWarnLogPageParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ITbWarnLogService extends IService<TbWarnLog> {
    List<TbWarnLog> selectByPage(QueryWarnLogPageParam param);
}
