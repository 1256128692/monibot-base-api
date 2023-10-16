package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.QueryPropertyValueParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbProjectPropertyMapper extends BaseMapper<TbProjectProperty> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbProjectProperty record);

    int insertSelective(TbProjectProperty record);

    TbProjectProperty selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbProjectProperty record);

    int updateByPrimaryKey(TbProjectProperty record);

    void updateBatch(Integer projectID, List<TbProjectProperty> projectPropertyList, Integer propertySubjectType);

    void insertBatch(List<TbProjectProperty> projectPropertyList);

    List<String> getPropertyValue(QueryPropertyValueParam param);

    List<PropertyDto> queryPropertyByProjectID(@Param("list") List<Integer> list,
                                               @Param("createType") Integer createType,
                                               @Param("propertySubjectType") Integer propertySubjectType
    );

    int deleteProjectPropertyList(List idList, Integer propertySubjectType);
}