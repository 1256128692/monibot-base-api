package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItemField;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.FieldItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface TbMonitorItemFieldMapper extends BaseMapper<TbMonitorItemField> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorItemField record);

    int insertSelective(TbMonitorItemField record);

    TbMonitorItemField selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorItemField record);

    int updateByPrimaryKey(TbMonitorItemField record);

    void insertBatch(Integer monitorItemID, List<FieldItem> fieldItemList);

    void deleteByMonitorItemIDList(List<Integer> monitorItemIDList);

    List<Integer> queryItemListByFieldTokenAndName(String monitorFieldName, String monitorFieldToken, String queryCode);

    void insertEntityBatch(Collection<TbMonitorItemField> collect);

    List<FieldBaseInfo> selectListByMonitorItemID(@Param("monitorItemID") Integer monitorItemID);
}