package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.IndustryDistributionRes;

import java.util.List;

/**
 * @Author wuxl
 * @Date 2024/1/18 16:31
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.service
 * @ClassName: EnterpriseDashboardService
 * @Description: TODO
 * @Version 1.0
 */
public interface EnterpriseDashboardService {
    List<IndustryDistributionRes> queryIndustryDistribution(QueryIndustryDistributionParam param);

    Object queryProductServices(QueryProductServicesParam param);
}
