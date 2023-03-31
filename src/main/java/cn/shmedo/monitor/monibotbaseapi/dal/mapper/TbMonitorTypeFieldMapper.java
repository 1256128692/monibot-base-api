package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.UpdateFieldTime;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeFieldWithFormula;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbMonitorTypeFieldMapper extends BaseMapper<TbMonitorTypeField> {
    int deleteByPrimaryKey(Integer ID);

    int insert(TbMonitorTypeField record);

    int insertSelective(TbMonitorTypeField record);

    TbMonitorTypeField selectByPrimaryKey(Integer ID);

    int updateByPrimaryKeySelective(TbMonitorTypeField record);

    int updateByPrimaryKey(TbMonitorTypeField record);

    List<Integer> queryMonitorTypeByFuzzyNameAndFuzzyToken(String fuzzyFieldName, String fuzzyFieldToken);

    List<TbMonitorTypeField> queryByMonitorTypes(List<Integer> monitorTypes, Boolean allFiled);

    void insertBatch(List<TbMonitorTypeField> list);

    List<MonitorTypeFieldWithFormula> queryMonitorTypeFieldWithFormula(Integer monitorType, Integer templateID);

    void updateBatch(List<UpdateFieldTime> fieldList);

    void deleteByMonitorTypeList(List<Integer> monitorTypeList);
}