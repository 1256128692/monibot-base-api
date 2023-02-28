package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryPropertyValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.PropertyList;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbProjectPropertyMapper extends BaseMapper<TbProjectProperty> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectProperty record);

    int insertSelective(TbProjectProperty record);

    TbProjectProperty selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectProperty record);

    int updateByPrimaryKey(TbProjectProperty record);

    void updateBatch(Integer projectID, List<TbProjectProperty> projectPropertyList);

    void insertBatch(List<TbProjectProperty> projectPropertyList);

    List<PropertyList> getPropertyList(Integer id);

    List<String> getPropertyValue(QueryPropertyValueParam param);
}