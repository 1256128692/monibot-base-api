package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.PointIDAndLocation;

import java.util.List;

public interface TbMonitorGroupPointMapper extends BasicMapper<TbMonitorGroupPoint>{
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorGroupPoint record);

    int insertSelective(TbMonitorGroupPoint record);

    TbMonitorGroupPoint selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorGroupPoint record);

    int updateByPrimaryKey(TbMonitorGroupPoint record);

    List<Integer> queryPointIDByGroupID(Integer groupID);

    void updateImgLocation(List<PointIDAndLocation> pointLocationList, Integer groupID);

    void updateLocationByGroupID(String location, Integer groupID);
}