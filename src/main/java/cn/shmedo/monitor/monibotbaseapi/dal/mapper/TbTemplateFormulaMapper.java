package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateFormula;

public interface TbTemplateFormulaMapper {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbTemplateFormula record);

    int insertSelective(TbTemplateFormula record);

    TbTemplateFormula selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbTemplateFormula record);

    int updateByPrimaryKey(TbTemplateFormula record);
}