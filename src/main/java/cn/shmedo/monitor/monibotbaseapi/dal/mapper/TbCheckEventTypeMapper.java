package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckEventType;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.UpdateEventTypeParam;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCheckEventTypeMapper extends BasicMapper<TbCheckEventType> {
    void updateSelectFieldById(UpdateEventTypeParam pa);
}