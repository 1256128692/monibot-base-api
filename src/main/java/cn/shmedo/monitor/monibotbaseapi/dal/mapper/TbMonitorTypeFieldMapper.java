package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.UpdateFieldItem;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeFieldWithFormula;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorTypeFieldV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.TbMonitorTypeFieldWithItemID;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorTypeFieldMapper extends BaseMapper<TbMonitorTypeField> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorTypeField record);

    int insertSelective(TbMonitorTypeField record);

    TbMonitorTypeField selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorTypeField record);

    int updateByPrimaryKey(TbMonitorTypeField record);

    List<Integer> queryMonitorTypeByFuzzyNameAndFuzzyToken(String fuzzyFieldName, String fuzzyFieldToken, String queryCode, Boolean allFiled);

    List<TbMonitorTypeField> queryByMonitorTypes(List<Integer> monitorTypes, Boolean allFiled);

    void insertBatch(List<TbMonitorTypeField> list);

    List<MonitorTypeFieldWithFormula> queryMonitorTypeFieldWithFormula(Integer monitorType, Integer templateID);

    void updateBatch(List<UpdateFieldItem> fieldList);

    void deleteByMonitorTypeList(List<Integer> monitorTypeList);

    List<TbMonitorTypeFieldWithItemID> queryByMonitorItemIDs(List<Integer> monitorItemIDList);

    List<MonitorTypeFieldV1> queryMonitorTypeFieldV1ByMonitorItems(List<Integer> monitorItemIDList);

    List<TbMonitorTypeField> selectListByMonitorID(Integer monitorPointID);
}