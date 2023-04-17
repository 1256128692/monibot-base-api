package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectMonitorClass;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateWtMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.ProjectMonitorClassBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbProjectMonitorClassMapper extends BaseMapper<TbProjectMonitorClass> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectMonitorClass record);

    int insertSelective(TbProjectMonitorClass record);

    TbProjectMonitorClass selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectMonitorClass record);

    int updateByPrimaryKey(TbProjectMonitorClass record);

    void insertByCondition(UpdateWtMonitorClassParam request);

    void updateByCondition(UpdateWtMonitorClassParam request);

    List<ProjectMonitorClassBaseInfo> selectListByProjectIDAndEnable(QueryMonitorClassParam request);
}