package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.QueryWtEnginePageParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TbWarnRuleMapper extends BaseMapper<TbWarnRule> {
    IPage<TbWarnRule> selectWarnRuleByPage(IPage<?> page, @Param("param") QueryWtEnginePageParam param, @Param("projectIDList") List<Integer> projectIDList);

    List<Integer> selectRuleWarnIDListByRuleIDList(List<Integer> engineIDList);
}
