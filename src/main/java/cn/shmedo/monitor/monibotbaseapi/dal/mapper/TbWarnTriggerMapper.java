package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnTrigger;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtTriggerActionInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbWarnTriggerMapper extends BaseMapper<TbWarnTrigger> {
    List<WtTriggerActionInfo> queryWarnStatusByEngineIds(List<Integer> engineIds);
}
