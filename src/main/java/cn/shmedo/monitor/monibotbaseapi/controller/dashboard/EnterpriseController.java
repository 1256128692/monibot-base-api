package cn.shmedo.monitor.monibotbaseapi.controller.dashboard;

import cn.shmedo.iot.entity.annotations.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
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
public class EnterpriseController {
    /**
     * @api {POST} /QueryIndustryDistribution 查询行业分布
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryIndustryDistribution
     * @apiDescription 查询行业分布统计，MDNET平台中项目现有量与近1年增长量
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {ObjectList} industryList 行业列表
     * @apiSuccess (返回结果) {String} industryList.industryName 行业名称
     * @apiSuccess (返回结果) {Int} industryList.projectCount 项目现有量
     * @apiSuccess (返回结果) {Int} industryList.growthInThePastYear 项目近一年增长量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterpriseInfo
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterpriseInfo")
    @PostMapping("QueryIndustryDistribution")
    public Object queryIndustryDistribution() {
        // todo 条形图按照数量大小排列，数量大的放前面
        return null;
    }

    /**
     * @api {POST} /QueryMonitoringCapability 查询监测能力（过时）
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryMonitoringCapability
     * @apiDescription 查询监测能力
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Object[]} monitorList 监测列表
     * @apiSuccess (返回结果) {String} monitorList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Double} [monitorList.weight] 权重（待确认）
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryMonitoringCapability")
    public Object queryMonitoringCapability() {
        return null;
    }

    /**
     * @api {POST} /QueryProductServices 查询产品服务
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryProductServices
     * @apiDescription 查询产品服务。统计所选行业各监测类型下的设备使用量。
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Object[]} monitorList 监测列表
     * @apiSuccess (返回结果) {String} monitorList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} monitorList.deviceCount 设备使用量（已推到工程项目内的）
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryProductServices")
    public Object queryProductServices() {
        // todo 条形图按照数量的倒序排列
        return null;
    }

    /**
     * @api {POST} /QueryProductServiceDetail 查询产品服务详情
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryProductServiceDetail
     * @apiDescription 查询产品服务详情。单监测类别下按产品的设备分布。
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {Object[]} productList 产品服务列表
     * @apiSuccess (返回结果) {String} productList.productName 产品名称
     * @apiSuccess (返回结果) {Int} productList.deviceCount 设备总数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryProductServiceDetail")
    public Object queryProductServiceDetail() {
        return null;
    }

    /**
     * @api {POST} /QueryResourceOverview 查询资源总览
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryResourceOverview
     * @apiDescription 查询资源总览。包括服务客户，覆盖区域，管理项目
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Int} companyCount 服务客户
     * @apiSuccess (返回结果) {Int} areaCount 覆盖区域
     * @apiSuccess (返回结果) {Int} projectCount 管理项目
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryResourceOverview")
    public Object queryResourceOverview() {
        return null;
    }

    /**
     * @api {POST} /QueryProvinceHaveProject 查询有项目的省份
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryProvinceHaveProject
     * @apiDescription 查询有项目的省份。供地图点击省份展示资源详情时调用
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Object[]} provinceList 省份列表
     * @apiSuccess (返回结果) {Int} provinceCode 省份code码
     * @apiSuccess (返回结果) {String} provinceName 省份名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryProvinceHaveProject")
    public Object queryProvinceHaveProject() {
        return null;
    }

    /**
     * @api {POST} /QueryProvinceProject 查询国内工程分布
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryProvinceProject
     * @apiDescription 查询国内工程分布。默认带出三个米度标签信息（是直接返回名称吗？还是包括下面的项目统计）
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiParam (请求参数) {Int} [provinceCode] 省份code码
     * @apiSuccess (返回结果) {String} provinceName 省份名称
     * @apiSuccess (返回结果) {Int} projectCount 项目统计
     * @apiSuccess (返回结果) {Object[]} cityList 城市列表
     * @apiSuccess (返回结果) {String} cityList.cityName 城市名称
     * @apiSuccess (返回结果) {Int} cityList.projectCount 项目统计
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryProvinceProject")
    public Object queryProvinceProject() {
        return null;
    }

    /**
     * @api {POST} /QueryProvinceProjectDetail 查询省份项目详情
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryProvinceProjectDetail
     * @apiDescription 查询省份项目详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiParam (请求参数) {Int} [provinceCode] 省份code码
     * @apiSuccess (返回结果) {String} provinceName 省份名称
     * @apiSuccess (返回结果) {Int} projectCount 项目统计
     * @apiSuccess (返回结果) {Object[]} projectList 项目列表
     * @apiSuccess (返回结果) {String} projectList.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} projectList.projectSubType 工程项目子类型
     * @apiSuccess (返回结果) {String} projectList.projectSubTypeName 工程项目子类型名称
     * @apiSuccess (返回结果) {String} projectList.projectAddress 项目地址
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryProvinceProjectDetail")
    public Object queryProvinceProjectDetail() {
        return null;
    }

    /**
     * @api {POST} /QueryDataAccess 查询数据接入
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryDataAccess
     * @apiDescription 查询数据接入。静态数据（数据共享、协议支持）；动态数据（设备管理，总台、本年、本月）
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {String[]} platformNameList 共享平台名称列表
     * @apiSuccess (返回结果) {String[]} protocolNameList 协议名称列表
     * @apiSuccess (返回结果) {Int} deviceCount 设备总数
     * @apiSuccess (返回结果) {Int} increaseInThePastYear 近一年新增
     * @apiSuccess (返回结果) {Int} increaseInThePastMonth 近一个月新增
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryDataAccess")
    public Object queryDataAccess() {
        return null;
    }

    /**
     * @api {POST} /QueryDataManagement 查询数据管理
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryDataManagement
     * @apiDescription 查询数据管理
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Int} total 数据总量
     * @apiSuccess (返回结果) {Int} governanceTotal 治理条数
     * @apiSuccess (返回结果) {Int} systemGovernanceTotal 系统治理条数
     * @apiSuccess (返回结果) {Int} artificialGovernanceTotal 人工治理条数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryDataManagement")
    public Object queryDataManagement() {
        return null;
    }

    /**
     * @api {POST} /QueryDeviceMaintenance 查询设备运维统计
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryDeviceMaintenance
     * @apiDescription 查询设备运维统计
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Double} onlineRate 在线率（设备实时在线率，点击可查看7天在线率，每日在线率取最接近当日8：00的在线率）
     * @apiSuccess (返回结果) {Object[]} commandList 指令列表
     * @apiSuccess (返回结果) {Int} commandList.commandType 指令类型：0-指令交互；1-远程升级；2-规则执行
     * @apiSuccess (返回结果) {Int} commandList.count 累计次数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryDeviceMaintenance")
    public Object queryDeviceMaintenance() {
        return null;
    }

    /**
     * @api {POST} /QueryDeviceOnlineRate 查询设备在线率
     * @apiVersion 1.0.0
     * @apiGroup 企业级（米度企业）
     * @apiName QueryDeviceOnlineRate
     * @apiDescription 查询设备在线率，返回近七天在线率
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [industryName] 行业名称
     * @apiSuccess (返回结果) {Object[]} onlineRateList 在线率列表
     * @apiSuccess (返回结果) {String} onlineRateList.date 日期
     * @apiSuccess (返回结果) {Double} onlineRateList.onlineRate 在线率
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:QueryDashboardEnterprise
     */
    @Permission(permissionName = "mdmbase:QueryDashboardEnterprise")
    @PostMapping("QueryDeviceOnlineRate")
    public Object queryDeviceOnlineRate() {
        return null;
    }
}
