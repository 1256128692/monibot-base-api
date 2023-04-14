package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TbWarnActionMapper extends BaseMapper<TbWarnAction> {
    Integer selectTriggerActionCount(List<Tuple<Integer, Integer>> list);
}
