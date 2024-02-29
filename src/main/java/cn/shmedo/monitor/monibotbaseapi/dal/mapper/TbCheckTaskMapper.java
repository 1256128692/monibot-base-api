package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.QueryDailyTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbCheckTaskMapper extends BasicMapper<TbCheckTask> {


    List<TaskInfo> selectListByCondition(QueryDailyTaskParam param, Integer userID);
}