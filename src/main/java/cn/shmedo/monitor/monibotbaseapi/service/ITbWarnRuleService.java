package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.QueryWtEnginePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtEngineInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbWarnRuleService extends IService<TbWarnRule> {
    PageUtil.Page<WtEngineInfo> queryWtEnginePage(QueryWtEnginePageParam param);
}
