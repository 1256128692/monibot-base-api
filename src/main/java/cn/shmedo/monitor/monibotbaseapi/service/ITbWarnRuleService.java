package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtEngineDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.WtEngineInfo;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ITbWarnRuleService extends IService<TbWarnRule> {
    PageUtil.Page<WtEngineInfo> queryWtEnginePage(QueryWtEnginePageParam param);

    WtEngineDetail queryWtEngineDetail(QueryWtEngineDetailParam param, Tuple<String, String> appKeySecret);

    Integer addWtEngine(AddWtEngineParam param, Integer userID);

    void updateWtEngine(UpdateWtEngineParam param);

    void updateWtEngineEnable(BatchUpdateWtEngineEnableParam param);

    void deleteWtEngine(BatchDeleteWtEngineParam param);
}
