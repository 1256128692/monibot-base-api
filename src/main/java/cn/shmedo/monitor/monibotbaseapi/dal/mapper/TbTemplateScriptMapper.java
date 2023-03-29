package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateScript;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbTemplateScriptMapper extends BaseMapper<TbTemplateScript> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTemplateScript record);

    int insertSelective(TbTemplateScript record);

    TbTemplateScript selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTemplateScript record);

    int updateByPrimaryKey(TbTemplateScript record);
}