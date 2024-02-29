package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.AddEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.DeleteEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.QueryEventTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.UpdateEventTypeParam;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CheckEventService extends IService<TbCheckEvent>{


    void addEventType(AddEventTypeParam pa);

    void deleteEventType(DeleteEventTypeParam pa);

    void updateEventType(UpdateEventTypeParam pa);

    Object queryEventTypeList(QueryEventTypeParam pa);
}
