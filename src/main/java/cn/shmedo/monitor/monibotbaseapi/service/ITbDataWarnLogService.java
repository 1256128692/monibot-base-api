package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.SaveDataWarnParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbDataWarnLogService extends IService<TbDataWarnLog> {

    void saveDataWarnLog(SaveDataWarnParam param);
}
