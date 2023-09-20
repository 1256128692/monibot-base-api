package cn.shmedo.monitor.monibotbaseapi.dal.mapper;


import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModelGroup;

/**
* @author 22386
* @description 针对表【tb_property_model_group】的数据库操作Mapper
* @createDate 2023-09-15 17:04:56
* @Entity generator.domain.TbPropertyModelGroup
*/
public interface TbPropertyModelGroupMapper {

    int deleteByPrimaryKey(Long ID);

    int insert(TbPropertyModelGroup record);

    int insertSelective(TbPropertyModelGroup record);

    TbPropertyModelGroup selectByPrimaryKey(Long ID);

    int updateByPrimaryKeySelective(TbPropertyModelGroup record);

    int updateByPrimaryKey(TbPropertyModelGroup record);

}
