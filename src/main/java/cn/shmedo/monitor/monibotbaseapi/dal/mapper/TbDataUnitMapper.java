package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataUnit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbDataUnitMapper  extends BaseMapper<TbDataUnit> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbDataUnit record);

    int insertSelective(TbDataUnit record);

    TbDataUnit selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbDataUnit record);

    int updateByPrimaryKey(TbDataUnit record);

    List<TbDataUnit> selectAll();
}