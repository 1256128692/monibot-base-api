package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.DataSourceCatalogRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.DataSourceCatalogResponse;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.TypeAndCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorTypeTemplateMapper extends BaseMapper<TbMonitorTypeTemplate> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorTypeTemplate record);

    int insertSelective(TbMonitorTypeTemplate record);

    TbMonitorTypeTemplate selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorTypeTemplate record);

    int updateByPrimaryKey(TbMonitorTypeTemplate record);

    List<TypeAndCount> countGroupByMonitorType(List<Integer> monitorTypeList, List<Integer> companyIDList);

    List<DataSourceCatalogResponse> dataSourceCatalog(DataSourceCatalogRequest request);
}