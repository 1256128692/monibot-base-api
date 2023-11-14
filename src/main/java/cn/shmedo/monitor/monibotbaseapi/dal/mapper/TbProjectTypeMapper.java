package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbProjectTypeMapper extends BaseMapper<TbProjectType> {
    int deleteByPrimaryKey(Byte ID);

    int insert(TbProjectType record);

    int insertSelective(TbProjectType record);

    TbProjectType selectByPrimaryKey(Byte ID);

    int updateByPrimaryKeySelective(TbProjectType record);

    int updateByPrimaryKey(TbProjectType record);

    List<TbProjectType> selectAll();
}