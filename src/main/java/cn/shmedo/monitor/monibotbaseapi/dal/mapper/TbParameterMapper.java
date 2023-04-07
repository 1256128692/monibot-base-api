package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbParameterMapper extends BaseMapper<TbParameter> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbParameter record);

    int insertSelective(TbParameter record);

    TbParameter selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbParameter record);

    int updateByPrimaryKey(TbParameter record);

    void deleteBatchByRecords(List<TbParameter> parameters);

    void insertBatch(List<TbParameter> parameters);
}