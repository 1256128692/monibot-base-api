package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.notify.NotifyListByProjectID;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuxl
 * @date 2024-01-11 15:15
 */
public interface TbNotifyRelationMapper extends BasicMapper<TbNotifyRelation> {
    TbNotifyRelation selectNotifyList(@Param("param") QueryNotifyListParam param);

    List<NotifyListByProjectID> selectNotifyByProjectID(Integer projectID);

    List<Integer> selectNotifyIdListMore(@Param("idList") List<Integer> idList);
}
