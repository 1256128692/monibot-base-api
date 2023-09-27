package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface TbProjectRelationMapper extends BaseMapper<TbProjectRelation> {
    void insertBatch(Integer projectID, List<Integer> nextLevelPIDList, Byte type);

    List<TbProjectRelation> queryByCompanyID(Integer companyID);
}