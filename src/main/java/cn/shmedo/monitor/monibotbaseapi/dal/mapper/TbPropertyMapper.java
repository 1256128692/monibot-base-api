package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbPropertyMapper extends BaseMapper<TbProperty> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProperty record);

    int insertSelective(TbProperty record);

    TbProperty selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProperty record);

    int updateByPrimaryKey(TbProperty record);

    List<TbProperty> queryByPID(Integer projectID);

    int countByPIDAndNames(Integer projectID, List<String> nameList);

    void insertBatch(List<TbProperty> properties);

    int countByMIDAndNames(Integer modelID, List<String> nameList);

    List<TbProperty> queryByMID(Integer modelID);

}