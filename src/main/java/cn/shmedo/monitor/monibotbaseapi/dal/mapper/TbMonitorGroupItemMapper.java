package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupItem;

import java.util.List;

public interface TbMonitorGroupItemMapper extends BasicMapper<TbMonitorGroupItem>{
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorGroupItem record);

    int insertSelective(TbMonitorGroupItem record);

    TbMonitorGroupItem selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorGroupItem record);

    int updateByPrimaryKey(TbMonitorGroupItem record);

    List<Integer> queryMonitorItemIDByGroupIDs(List<Integer> groupIDList);
}