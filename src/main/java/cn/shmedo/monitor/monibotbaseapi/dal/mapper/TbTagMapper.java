package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbTagMapper extends BaseMapper<TbTag> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTag record);

    int insertSelective(TbTag record);

    TbTag selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTag record);

    int updateByPrimaryKey(TbTag record);
}