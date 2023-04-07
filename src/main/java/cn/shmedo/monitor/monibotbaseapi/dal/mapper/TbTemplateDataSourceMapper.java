package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateDataSource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbTemplateDataSourceMapper extends BaseMapper<TbTemplateDataSource> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTemplateDataSource record);

    int insertSelective(TbTemplateDataSource record);

    TbTemplateDataSource selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTemplateDataSource record);

    int updateByPrimaryKey(TbTemplateDataSource record);

    void insertBatch(List<TbTemplateDataSource> list);

    void deleteByTemplateIDList(List<Integer> templateIDList);
}