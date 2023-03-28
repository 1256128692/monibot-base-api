package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.TypeAndCount;

import java.util.List;

public interface TbMonitorTypeTemplateMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorTypeTemplate record);

    int insertSelective(TbMonitorTypeTemplate record);

    TbMonitorTypeTemplate selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorTypeTemplate record);

    int updateByPrimaryKey(TbMonitorTypeTemplate record);

    List<TypeAndCount> countGroupByMonitorType(List<Integer> monitorTypeList);
}