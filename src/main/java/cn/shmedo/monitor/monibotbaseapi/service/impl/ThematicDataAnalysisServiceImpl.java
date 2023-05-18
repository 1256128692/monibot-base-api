package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.QueryRealDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.DmThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.StThematicAnalysisInfo;
import cn.shmedo.monitor.monibotbaseapi.service.IThematicDataAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:59
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ThematicDataAnalysisServiceImpl implements IThematicDataAnalysisService {
    @Override
    public StThematicAnalysisInfo queryStGroupRealData(QueryRealDataParam param) {
        return null;
    }

    @Override
    public DmThematicAnalysisInfo queryDmAnalysisData(QueryRealDataParam param) {
        return null;
    }
}
