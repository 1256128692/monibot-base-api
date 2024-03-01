package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEvent;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskDataResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskDateAndStatisticsInfo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CheckEventService extends IService<TbCheckEvent>{


    void addEventType(AddEventTypeParam pa);

    void deleteEventType(DeleteEventTypeParam pa);

    void updateEventType(UpdateEventTypeParam pa);

    Object queryEventTypeList(QueryEventTypeParam pa);

    TaskDateAndStatisticsInfo queryDailyTaskList(QueryDailyTaskParam pa, Integer subjectID);

    void addEventInfo(AddEventInfoParam pa);

    void deleteEventInfo(DeleteEventInfoParam pa);
}
