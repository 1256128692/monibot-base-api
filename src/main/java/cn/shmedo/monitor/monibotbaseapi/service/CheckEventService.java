package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.AddEventTypeParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CheckEventService extends IService<TbCheckEvent>{


    void addEventType(AddEventTypeParam pa);
}
