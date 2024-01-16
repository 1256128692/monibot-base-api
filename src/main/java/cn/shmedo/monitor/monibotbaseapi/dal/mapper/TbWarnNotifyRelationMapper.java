package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyRelation;
import org.apache.ibatis.annotations.Param;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:15
 */
public interface TbWarnNotifyRelationMapper extends BasicMapper<TbWarnNotifyRelation> {
    void insertVo(@Param("tbWarnNotifyRelation") TbWarnNotifyRelation tbWarnNotifyRelation);
}
