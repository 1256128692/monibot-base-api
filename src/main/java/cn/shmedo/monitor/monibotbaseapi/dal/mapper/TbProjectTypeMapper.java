package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbProjectTypeMapper extends BaseMapper<TbProjectType> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectType record);

    int insertSelective(TbProjectType record);

    TbProjectType selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectType record);

    int updateByPrimaryKey(TbProjectType record);

    List<TbProjectType> selectAll();
}