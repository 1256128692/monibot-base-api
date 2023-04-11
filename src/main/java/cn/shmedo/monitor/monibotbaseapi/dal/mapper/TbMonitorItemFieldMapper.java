package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItemField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorItemFieldMapper extends BaseMapper<TbMonitorItemField> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorItemField record);

    int insertSelective(TbMonitorItemField record);

    TbMonitorItemField selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorItemField record);

    int updateByPrimaryKey(TbMonitorItemField record);

    void insertBatch(Integer monitorItemID, List<Integer> fieldIDList);

    void deleteByMonitorItemIDList(List<Integer> monitorItemIDList);

}