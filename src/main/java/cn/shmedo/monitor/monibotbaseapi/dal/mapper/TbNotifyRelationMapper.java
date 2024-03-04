package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbNotifyRelation;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryNotifyListParam;
import org.apache.ibatis.annotations.Param;

/**
 * @author wuxl
 * @date 2024-01-11 15:15
 */
public interface TbNotifyRelationMapper extends BasicMapper<TbNotifyRelation> {
    TbNotifyRelation selectNotifyList(@Param("param") QueryNotifyListParam param);
}
