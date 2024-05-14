package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.eigenValue.EigenValueInfoV1;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicEigenValueData;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicEigenValueInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.EigenBaseInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbEigenValueMapper extends BasicMapper<TbEigenValue> {

    List<EigenValueInfoV1> selectListByCondition(Integer monitorItemID, Integer projectID, List<Integer> monitorPointIDList, List<String> fieldTokenList, ScopeType scope);

    void deleteByEigenValueIDList(List<Integer> eigenValueIDList);

    List<ThematicEigenValueData> selectBaseInfoByIDList(List<Integer> eigenValueIDList, List<Integer> monitorPointIDList);

    List<ThematicEigenValueInfo> selectFieldInfoByPointIDList(List<Integer> monitorPointIDList);

    List<EigenBaseInfo> selectByIDs(List<Integer> eigenValueIDList);

    int insertBatchSelective(@Param("list") List<AddEigenValueParam> tbEigenValues);

    boolean selectExist(@Param("projectID") Integer projectID,
                        @Param("excludeEigenValueID") Integer excludeEigenValueID,
                        @Param("list") List<AddEigenValueParam> list);
}