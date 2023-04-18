package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbProjectConfigMapper extends BaseMapper<TbProjectConfig> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectConfig record);

    int insertSelective(TbProjectConfig record);

    TbProjectConfig selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectConfig record);

    int updateByPrimaryKey(TbProjectConfig record);
}