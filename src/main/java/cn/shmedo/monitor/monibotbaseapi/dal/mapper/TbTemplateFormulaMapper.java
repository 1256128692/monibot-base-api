package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateFormula;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface TbTemplateFormulaMapper extends BaseMapper<TbTemplateFormula> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTemplateFormula record);

    int insertSelective(TbTemplateFormula record);

    TbTemplateFormula selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTemplateFormula record);

    int updateByPrimaryKey(TbTemplateFormula record);
}