package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProvinceProjectDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.dashboard.*;

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

    List<ProductServicesRes> queryProductServices(QueryProductServicesParam param);

    ResourceOverviewRes queryResourceOverview(QueryProductServicesParam param);

    List<ProvinceProjectRes> queryProvinceProject(QueryProductServicesParam param);

    ProvinceProjectDetailRes queryProvinceProjectDetail(QueryProvinceProjectDetailParam param);

    Object queryDataAccess(QueryProductServicesParam param);

    DataManagementRes queryDataManagement(QueryProductServicesParam param);

    DeviceMaintenanceRes queryDeviceMaintenance(QueryProductServicesParam param);
}
