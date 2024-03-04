package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak.CheckTaskSimple;
import cn.shmedo.monitor.monibotbaseapi.model.param.checkevent.QueryDailyTaskParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.checktask.QueryCheckTaskListRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.checkevent.TaskInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbCheckTaskMapper extends BasicMapper<TbCheckTask> {


    List<TaskInfo> selectListByCondition(QueryDailyTaskParam param, Integer userID);

    Integer lastSerialNumber(String prefix);

    Page<CheckTaskSimple> page(@Param("param") QueryCheckTaskListRequest param, @Param("page") IPage<CheckTaskSimple> page);

    List<CheckTaskSimple> list(@Param("param") QueryCheckTaskListRequest param);

    List<Tuple<Integer, Long>> statis(@Param("param") QueryCheckTaskListRequest param);
}