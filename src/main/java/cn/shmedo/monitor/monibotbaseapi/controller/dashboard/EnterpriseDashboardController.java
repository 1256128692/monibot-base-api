package cn.shmedo.monitor.monibotbaseapi.controller.dashboard;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryIndustryDistributionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.QueryProductServicesParam;
import cn.shmedo.monitor.monibotbaseapi.service.EnterpriseDashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2024/1/16 9:40
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller.dashboard
 * @ClassName: EnterpriseController
 * @Description: 大屏-米度企业级别权限
 * @Version 1.0
 */
@RestController
@RequiredArgsConstructor
public class EnterpriseDashboardController {
    private final EnterpriseDashboardService dashboardService;

    /**
     * @api {POST} /QueryIndustryDistribution 查询行业分布
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryIndustryDistribution
     * @apiDescription 查询行业分布统计，MDNET平台中项目现有量与近1年增长量
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiSuccess (返回结果) {Object[]} industryList 行业列表
     * @apiSuccess (返回结果) {Int} industryList.projectMainType 项目一级分类
     * @apiSuccess (返回结果) {String} industryList.projectMainTypeName 项目一级分类名称
     * @apiSuccess (返回结果) {Int} industryList.projectCount 项目现有量
     * @apiSuccess (返回结果) {Int} industryList.growthInThePastYear 项目近一年增长量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryIndustryDistribution")
    public Object queryIndustryDistribution(@RequestBody @Valid QueryIndustryDistributionParam param) {
        return ResultWrapper.success(dashboardService.queryIndustryDistribution(param));
    }

    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryMonitoringCapability")
    public Object queryMonitoringCapability() {
        return null;
    }

    /**
     * @api {POST} /QueryProductServices 查询产品服务
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryProductServices
     * @apiDescription 查询产品服务。统计所选行业各监测类型下的设备使用量。
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiParam (请求参数) {Int} [monitorType] 监测类型（为空时，productList返回空；非空时，productList返回非空）
     * @apiSuccess (返回结果) {Object[]} monitorList 监测列表
     * @apiSuccess (返回结果) {Int} monitorList.monitorType 监测类型
     * @apiSuccess (返回结果) {String} monitorList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} monitorList.deviceCount 设备使用量（已推到工程项目内的）
     * @apiSuccess (返回结果) {Object[]} [monitorList.productList] 产品服务列表
     * @apiSuccess (返回结果) {Int} monitorList.productList.productID 产品ID
     * @apiSuccess (返回结果) {String} monitorList.productList.productName 产品名称
     * @apiSuccess (返回结果) {Int} monitorList.productList.deviceCount 设备总数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryProductServices")
    public Object queryProductServices(@RequestBody @Valid QueryProductServicesParam param) {
        return dashboardService.queryProductServices(param);
    }

    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryProductServiceDetail")
    public Object queryProductServiceDetail() {
        return null;
    }

    /**
     * @api {POST} /QueryResourceOverview 查询资源总览
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryResourceOverview
     * @apiDescription 查询资源总览。包括服务客户，覆盖区域，管理项目
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiSuccess (返回结果) {Int} companyCount 服务客户
     * @apiSuccess (返回结果) {Int} areaCount 覆盖区域
     * @apiSuccess (返回结果) {Int} projectCount 管理项目
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryResourceOverview")
    public Object queryResourceOverview() {
        return null;
    }

    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryProvinceHasProject")
    public Object queryProvinceHasProject() {
        return null;
    }

    /**
     * @api {POST} /QueryProvinceProject 查询企业工程分布
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryProvinceProject
     * @apiDescription 查询企业工程分布
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiParam (请求参数) {Int} [provinceCode] 省份code码
     * @apiSuccess (返回结果) {Object[]} provinceList 省份列表
     * @apiSuccess (返回结果) {Int} provinceList.provinceCode 省份code码
     * @apiSuccess (返回结果) {String} provinceList.provinceName 省份名称（shortName）
     * @apiSuccess (返回结果) {String} provinceList.provincialCapital 省会
     * @apiSuccess (返回结果) {Double} provinceList.longitude 经度
     * @apiSuccess (返回结果) {Double} provinceList.latitude 纬度
     * @apiSuccess (返回结果) {Int} provinceList.projectCount 项目统计
     * @apiSuccess (返回结果) {Object[]} provinceList.cityList 城市列表
     * @apiSuccess (返回结果) {Int} provinceList.cityList.cityCode 城市code
     * @apiSuccess (返回结果) {String} provinceList.cityList.cityName 城市名称
     * @apiSuccess (返回结果) {Int} provinceList.cityList.projectCount 项目统计
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryProvinceProject")
    public Object queryProvinceProject() {
        return null;
    }

    /**
     * @api {POST} /QueryProvinceProjectDetail 查询省份项目详情
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryProvinceProjectDetail
     * @apiDescription 查询省份项目详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiParam (请求参数) {Int} [provinceCode] 省份code码
     * @apiSuccess (返回结果) {String} provinceName 省份名称
     * @apiSuccess (返回结果) {Int} projectCount 项目统计
     * @apiSuccess (返回结果) {Object[]} projectList 项目列表
     * @apiSuccess (返回结果) {Int} projectList.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} projectList.projectName 项目名称
     * @apiSuccess (返回结果) {Int} projectList.projectType 项目类型
     * @apiSuccess (返回结果) {String} projectList.projectTypeName 项目类型名称
     * @apiSuccess (返回结果) {Double} projectList.longitude 项目经度
     * @apiSuccess (返回结果) {Double} projectList.latitude 项目纬度
     * @apiSuccess (返回结果) {String} projectList.projectAddress 项目地址
     * @apiSuccess (返回结果) {Object[]} projectList.locationInfo 行政区划
     * @apiSuccess (返回结果) {Int} projectList.locationInfo.province 省份code
     * @apiSuccess (返回结果) {String} projectList.locationInfo.provinceName 省份名称
     * @apiSuccess (返回结果) {Int} projectList.locationInfo.city 城市code
     * @apiSuccess (返回结果) {String} projectList.locationInfo.cityName 城市名称
     * @apiSuccess (返回结果) {Int} projectList.locationInfo.area 区code
     * @apiSuccess (返回结果) {String} projectList.locationInfo.areaName 区名称
     * @apiSuccess (返回结果) {Int} projectList.locationInfo.town 镇code
     * @apiSuccess (返回结果) {String} projectList.locationInfo.townName 镇名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryProvinceProjectDetail")
    public Object queryProvinceProjectDetail() {
        return null;
    }

    /**
     * @api {POST} /QueryDataAccess 查询数据接入
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryDataAccess
     * @apiDescription 查询数据接入。静态数据（数据共享、协议支持）；动态数据（设备管理，总台、本年、本月）
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiSuccess (返回结果) {String[]} platformNameList 共享平台名称列表
     * @apiSuccess (返回结果) {String[]} protocolNameList 协议名称列表
     * @apiSuccess (返回结果) {Int} deviceCount 设备总数
     * @apiSuccess (返回结果) {Int} increaseInThePastYear 近一年新增
     * @apiSuccess (返回结果) {Int} increaseInThePastMonth 近一个月新增
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryDataAccess")
    public Object queryDataAccess() {
        return null;
    }

    /**
     * @api {POST} /QueryDataManagement 查询数据管理
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryDataManagement
     * @apiDescription 查询数据管理
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiSuccess (返回结果) {Int} total 数据总量
     * @apiSuccess (返回结果) {Int} governanceTotal 治理条数
     * @apiSuccess (返回结果) {Int} systemGovernanceTotal 系统治理条数
     * @apiSuccess (返回结果) {Int} artificialGovernanceTotal 人工治理条数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryDataManagement")
    public Object queryDataManagement() {
        return null;
    }

    /**
     * @api {POST} /QueryDeviceMaintenance 查询设备运维统计
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryDeviceMaintenance
     * @apiDescription 查询设备运维统计
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiSuccess (返回结果) {Double} onlineRate 在线率（设备实时在线率，点击可查看7天在线率，每日在线率取最接近当日8：00的在线率）
     * @apiSuccess (返回结果) {Object[]} commandList 指令列表
     * @apiSuccess (返回结果) {Int} commandList.commandType 指令类型：0-指令交互；1-远程升级；2-规则执行
     * @apiSuccess (返回结果) {Int} commandList.count 累计次数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryDeviceMaintenance")
    public Object queryDeviceMaintenance() {
        return null;
    }

    /**
     * @api {POST} /QueryDeviceOnlineRate 查询设备在线率
     * @apiVersion 1.0.0
     * @apiGroup 监测企业大屏
     * @apiName QueryDeviceOnlineRate
     * @apiDescription 查询设备在线率，返回近七天在线率
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectMainType] 项目一级分类（1-水文水利；2-矿山；3-国土地灾；4-城市基建）
     * @apiSuccess (返回结果) {Int} activeCount 指令交互次数
     * @apiSuccess (返回结果) {Int} activeAccessCount 指令交互成功次数
     * @apiSuccess (返回结果) {Int} otaCount 远程升级次数
     * @apiSuccess (返回结果) {Int} otaAccessCount 远程升级成功次数
     * @apiSuccess (返回结果) {Int} execCount 规则执行次数
     * @apiSuccess (返回结果) {Int} execAccessCount 规则执行成功次数
     * @apiSuccess (返回结果) {Int} onlineCount 设备在线数
     * @apiSuccess (返回结果) {Int} offlineCount 设备离线数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryDeviceOnlineRate")
    public Object queryDeviceOnlineRate() {
        return null;
    }
}
