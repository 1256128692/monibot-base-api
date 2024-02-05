package cn.shmedo.monitor.monibotbaseapi.controller.dashboard;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.dashboard.*;
import cn.shmedo.monitor.monibotbaseapi.service.WtStatisticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 水库大屏统计模块
 *
 * @author Chengfs on 2024/1/23
 */
@RestController
@RequiredArgsConstructor
public class WtStatisticsController {

    private final WtStatisticsService wtStatisticsService;

    /**
     * @api {POST} /ReservoirWarnStatistics 实时报警统计(按监测类型分类)
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirWarnStatistics
     * @apiDescription 实时报警统计，统计监测类型下各等级报警监测点数量
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} [projectID] 项目id
     * @apiParam (请求参数) {Int} [platform] 平台id
     * @apiSuccess (返回结果) {Object} dict 等级字典
     * @apiSuccess (返回结果) {String} dict.level1 等级1名称
     * @apiSuccess (返回结果) {String} dict.level2 等级2名称
     * @apiSuccess (返回结果) {String} dict.level3 等级3名称
     * @apiSuccess (返回结果) {String} dict.level4 等级4名称
     * @apiSuccess (返回结果) {String} dict.offline 离线报警
     * @apiSuccess (返回结果) {Object} overview 总览
     * @apiSuccess (返回结果) {Int} overview.level1 等级1数量
     * @apiSuccess (返回结果) {Int} overview.level2 等级2数量
     * @apiSuccess (返回结果) {Int} overview.level3 等级3数量
     * @apiSuccess (返回结果) {Int} overview.level4 等级4数量
     * @apiSuccess (返回结果) {Int} overview.offline 离线报警数量
     * @apiSuccess (返回结果) {Object[]} monitorType 监测类型分类信息
     * @apiSuccess (返回结果) {Int} monitorType.monitorType 监测类型
     * @apiSuccess (返回结果) {String} monitorType.typeName 监测类型名称
     * @apiSuccess (返回结果) {Object} monitorType.detail 报警统计信息
     * @apiSuccess (返回结果) {Int} monitorType.detail.level1 等级1数量
     * @apiSuccess (返回结果) {Int} monitorType.detail.level2 等级2数量
     * @apiSuccess (返回结果) {Int} monitorType.detail.level3 等级3数量
     * @apiSuccess (返回结果) {Int} monitorType.detail.level4 等级4数量
     * @apiSuccess (返回结果) {Int} monitorType.detail.offline 离线报警数量
     * @apiSuccessExample {json} Success-Response
     * {"dict": {"level1": "红色报警","level2": "橙色报警","level3": "黄色报警","level4": "蓝色报警","offline": "离线报警"},"overview": {"level1": 12,"level2": 1,"level3": 1,"level4": 0,"offline": 47},"monitorType": [{"monitorType": 2,"typeName": "水位","detail": {"level1": 0,"level2": 0,"level3": 0,"level4": 0,"offline": 5}}]}
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeWtStatistics
     */
    @Permission(permissionName = "mdmbase:DescribeWtStatistics")
    @PostMapping("ReservoirWarnStatistics")
    public Object queryWarnStatsByMonitorType(@Valid @RequestBody QueryReservoirWarnStatsParam param) {
        return wtStatisticsService.queryWarnStats(param);
    }

    /**
     * @api {POST} /ReservoirWarnStatisticsByProject 实时报警统计(按工程项目分类)
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirWarnStatisticsByProject
     * @apiDescription 实时报警统计，统计项目下各等级报警监测点数量
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} [platform] 平台id
     * @apiSuccess (返回结果) {Object} dict 等级字典
     * @apiSuccess (返回结果) {String} dict.level1 等级1名称
     * @apiSuccess (返回结果) {String} dict.level2 等级2名称
     * @apiSuccess (返回结果) {String} dict.level3 等级3名称
     * @apiSuccess (返回结果) {String} dict.level4 等级4名称
     * @apiSuccess (返回结果) {String} dict.offline 离线报警
     * @apiSuccess (返回结果) {Object} overview 总览
     * @apiSuccess (返回结果) {Int} overview.level1 等级1数量
     * @apiSuccess (返回结果) {Int} overview.level2 等级2数量
     * @apiSuccess (返回结果) {Int} overview.level3 等级3数量
     * @apiSuccess (返回结果) {Int} overview.level4 等级4数量
     * @apiSuccess (返回结果) {Int} overview.offline 离线报警数量
     * @apiSuccess (返回结果) {Object[]} project 工程项目分类信息
     * @apiSuccess (返回结果) {Int} project.id 工程id
     * @apiSuccess (返回结果) {String} project.projectName 工程名称
     * @apiSuccess (返回结果) {Object} project.detail 报警统计信息
     * @apiSuccess (返回结果) {Int} project.detail.level1 等级1数量
     * @apiSuccess (返回结果) {Int} project.detail.level2 等级2数量
     * @apiSuccess (返回结果) {Int} project.detail.level3 等级3数量
     * @apiSuccess (返回结果) {Int} project.detail.level4 等级4数量
     * @apiSuccess (返回结果) {Int} project.detail.offline 离线报警数量
     * @apiSuccessExample {json} Success-Response
     * {"dict": {"level1": "红色报警","level2": "橙色报警","level3": "黄色报警","level4": "蓝色报警","offline": "离线报警"},"overview": {"level1": 12,"level2": 1,"level3": 1,"level4": 0,"offline": 47},"project": [{"id": 2,"projectName": "测试项目","detail": {"level1": 0,"level2": 0,"level3": 0,"level4": 0,"offline": 5}}]}
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeWtStatistics
     */
    @Permission(permissionName = "mdmbase:DescribeWtStatistics")
    @PostMapping("ReservoirWarnStatisticsByProject")
    public Object queryWarnStatsByProject(@Valid @RequestBody QueryReservoirWarnStatsByProjectParam param) {
        return wtStatisticsService.queryWarnStatsByProject(param);
    }

    /**
     * @api {Get} /CacheReservoirWarnStatistics 缓存实时报警统计数据
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName CacheReservoirWarnStatistics
     * @apiDescription 缓存实时报警统计数据 (仅限服务内部调用)
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 应用权限 mdmbase:UpdateWtStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateWtStatistics", allowApplication = true, allowUser = false)
    @GetMapping("CacheReservoirWarnStatistics")
    public Object cacheWarnStats() {
        wtStatisticsService.cacheWarnStats();
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {Get} /CacheDeviceOnlineStatistics 缓存设备在线统计数据
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName CacheDeviceOnlineStatistics
     * @apiDescription 缓存设备在线统计数据 (仅限服务内部调用)
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 应用权限 mdmbase:UpdateWtStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateWtStatistics", allowApplication = true, allowUser = false)
    @GetMapping("CacheDeviceOnlineStatistics")
    public Object cacheDeviceOnlineStats() {
        wtStatisticsService.cacheDeviceOnlineStats();
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeviceOnlineStatistics 设备在线统计
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName DeviceOnlineStatistics
     * @apiDescription 设备在线统计
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} [projectID] 项目id
     * @apiSuccess (返回结果) {Int} count 总量
     * @apiSuccess (返回结果) {Int} online 在线数
     * @apiSuccess (返回结果) {Int} offline 离线数
     * @apiSuccess (返回结果) {Object[]} monitorType 监测类型分类
     * @apiSuccess (返回结果) {String} monitorType.typeName 监测类型名称
     * @apiSuccess (返回结果) {Int} monitorType.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} monitorType.count 总量
     * @apiSuccess (返回结果) {Int} monitorType.online 在线数
     * @apiSuccess (返回结果) {Int} monitorType.offline 离线数
     * @apiSuccessExample {json} Success-Response:
     * {"count": 99,"online": 90,"offline": 10,"monitorType": [{"name": "流量","monitorType": 1,"count": 99,"online": 90,"offline": 10}]}
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeWtStatistics
     */
    @Permission(permissionName = "mdmbase:DescribeWtStatistics")
    @PostMapping("DeviceOnlineStatistics")
    public Object deviceOnlineStats(@Valid @RequestBody QueryDeviceOnlineStatsParam param) {
        return wtStatisticsService.queryDeviceOnlineStats(param);
    }

    /**
     * @api {POST} /ReservoirProjectStatistics 水库概况统计
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirProjectStatistics
     * @apiDescription 水库概况统计
     * @apiParam (请求参数) {Int} companyID 企业ID
     * @apiSuccess (返回结果) {Int} reservoirCount 水库总量
     * @apiSuccess (返回结果) {Int} typeOneCount 小(Ⅰ)型水库总量
     * @apiSuccess (返回结果) {Int} typeTwoCount 小(Ⅱ)型水库总量
     * @apiSuccess (返回结果) {Int} typeThreeCount 中型水库总量
     * @apiSuccess (返回结果) {Int} typeFourCount 大(Ⅰ)型水库总量
     * @apiSuccess (返回结果) {Int} typeFiveCount 大(Ⅱ)型水库总量
     * @apiSuccess (返回结果) {Object[]} areaStatisticsList 行政区划统计列表(四级)
     * @apiSuccess (返回结果) {String} areaStatisticsList.areaCode 行政区划code
     * @apiSuccess (返回结果) {String} areaStatisticsList.areaName 行政区划名称
     * @apiSuccess (返回结果) {Int} areaStatisticsList.reservoirCount 水库总量
     * @apiSuccess (返回结果) {Int} areaStatisticsList.typeOneCount 小(Ⅰ)型水库总量
     * @apiSuccess (返回结果) {Int} areaStatisticsList.typeTwoCount 小(Ⅱ)型水库总量
     * @apiSuccess (返回结果) {Int} areaStatisticsList.typeThreeCount 中型水库总量
     * @apiSuccess (返回结果) {Int} areaStatisticsList.typeFourCount 大(Ⅰ)型水库总量
     * @apiSuccess (返回结果) {Int} areaStatisticsList.typeFiveCount 大(Ⅱ)型水库总量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirProjectStatistics")
    public Object reservoirProjectStatistics(@Valid @RequestBody ReservoirProjectStatisticsParam pa) {
        return wtStatisticsService.reservoirProjectStatistics(pa.getCompanyID(), pa.getHavePermissionProjectList());
    }

    /**
     * @api {POST} /ReservoirDeviceStatistics 水库设备概况统计
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirDeviceStatistics
     * @apiDescription 水库设备概况统计
     * @apiParam (请求参数) {Int} companyID 企业ID
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID
     * @apiSuccess (返回结果) {Int} videoDeviceCount 视频设备总量
     * @apiSuccess (返回结果) {Int} iotDeviceCount 智能设备总量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirDeviceStatistics")
    public Object reservoirDeviceStatistics(@Valid @RequestBody ReservoirDeviceStatisticsParam pa) {
        return wtStatisticsService.reservoirDeviceStatistics(pa.getCompanyID(), pa.getHavePermissionProjectList());
    }

    /**
     * @api {POST} /ReservoirMonitorStatistics 水库测点概况统计
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirMonitorStatistics
     * @apiDescription 水库测点概况统计
     * @apiParam (请求参数) {Int} companyID 企业ID
     * @apiSuccess (返回结果) {Int} monitorPointCount 测点总数
     * @apiSuccess (返回结果) {Object[]} monitorTypeStatisticsList 监测类型统计列表
     * @apiSuccess (返回结果) {Int} monitorTypeStatisticsList.monitorType 监测类型
     * @apiSuccess (返回结果) {String} monitorTypeStatisticsList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} monitorTypeStatisticsList.count 测点总数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirMonitorStatistics")
    public Object reservoirMonitorStatistics(@Valid @RequestBody ReservoirMonitorStatisticsParam pa) {
        return wtStatisticsService.reservoirMonitorStatistics(pa.getCompanyID(), pa.getHavePermissionProjectList());
    }

    /**
     * @api {POST} /ReservoirNewSensorData 水库最新传感器数据
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirNewSensorData
     * @apiDescription 水库最新传感器数据
     * @apiParam (请求参数) {Int} companyID 企业ID
     * @apiParam (请求参数) {Int[]} projectIDList 工程项目ID
     * @apiSuccess (返回结果) {Object[]} data 数据列表
     * @apiSuccess (返回结果) {Int} data.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} data.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} data.shortName 工程项目短名称
     * @apiSuccess (返回结果) {Int} data.reservoirScale 水库规模,1:小(Ⅰ)型水库,2:小(Ⅱ)型水库,3:中型水库,4:大(Ⅰ)型水库,5:大(Ⅱ)型水库
     * @apiSuccess (返回结果) {String} data.areaCode 行政区划code
     * @apiSuccess (返回结果) {String} data.areaName 行政区划名称
     * @apiSuccess (返回结果) {DateTime} data.dataTime 最新数据时间
     * @apiSuccess (返回结果) {Double} data.waterValue 水位值
     * @apiSuccess (返回结果) {Double} data.periodRainValue 时段降水量
     * @apiSuccess (返回结果) {Double} data.currentRainValue 当前降水量
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @RequestMapping(value = "/ReservoirNewSensorData", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object reservoirNewSensorData(@Valid @RequestBody ReservoirNewSensorDataParam pa) {
        return wtStatisticsService.queryReservoirNewSensorData(pa);
    }

    /**
     * @api {POST} /ReservoirVideoMonitorPoint 水库视频监测点列表
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirVideoMonitorPoint
     * @apiDescription 水库视频监测点列表
     * @apiParam (请求参数) {Int} companyID 企业ID
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID
     * @apiSuccess (返回结果) {Json[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} dataList.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} dataList.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} dataList.shortName 工程项目短名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirVideoMonitorPoint")
    public Object reservoirVideoMonitorPoint(@Valid @RequestBody ReservoirVideoMonitorPointParam pa) {
        return wtStatisticsService.reservoirVideoMonitorPoint(pa.getCompanyID(), pa.getHavePermissionProjectList());
    }

    /**
     * @api {POST} /ReservoirProjectDetail 水库详情信息
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirProjectDetail
     * @apiDescription 水库详情信息
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiSuccess (返回结果) {Int} projectID 工程项目ID
     * @apiSuccess (返回结果) {String} projectName 工程项目名称
     * @apiSuccess (返回结果) {String} shortName 工程项目短名称
     * @apiSuccess (返回结果) {String} reservoirScale 水库规模,1:小(Ⅰ)型水库,2:小(Ⅱ)型水库,3:中型水库,4:大(Ⅰ)型水库,5:大(Ⅱ)型水库
     * @apiSuccess (返回结果) {Double} checkFloodWater 校核洪水位
     * @apiSuccess (返回结果) {Double} designFloodWater 设计洪水位
     * @apiSuccess (返回结果) {Double} normalStorageWater 正常蓄水位
     * @apiSuccess (返回结果) {Double} periodLimitWater 期限制水位
     * @apiSuccess (返回结果) {Double} totalCapacity 总库容
     * @apiSuccess (返回结果) {String} manageUnit 管理单位
     * @apiSuccess (返回结果) {String} contactsPhone 联系电话
     * @apiSuccess (返回结果) {String} administrationDirector 行政负责人
     * @apiSuccess (返回结果) {String} mainManagementDirector 主管负责人
     * @apiSuccess (返回结果) {String} managementDirector 管理负责人
     * @apiSuccess (返回结果) {String} patrolDirector 巡查负责人
     * @apiSuccess (返回结果) {String} technicalDirector 技术负责人
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirProjectDetail")
    public Object reservoirProjectDetail(@Valid @RequestBody ReservoirProjectDetailParam pa) {
        return wtStatisticsService.reservoirProjectDetail(pa.getTbProjectInfo());
    }


    /**
     * @api {Get} /CacheTypePointStatistics 缓存类型下点数量统计数据
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName CacheTypePointStatistics
     * @apiDescription 缓存类型下点数量统计数据 (仅限服务内部调用)
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 应用权限 mdmbase:UpdateWtStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateWtStatistics", allowApplication = true, allowUser = false)
    @GetMapping("CacheTypePointStatistics")
    public Object cacheTypePointStatistics() {
        wtStatisticsService.cacheTypePointStatistics();
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {Get} /CacheVideoPointIDStatistics 缓存视频点ID
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName CacheVideoPointIDStatistics
     * @apiDescription 缓存视频点ID，仅有传感器的点 (仅限服务内部调用)
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 应用权限 mdmbase:UpdateWtStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateWtStatistics", allowApplication = true, allowUser = false)
    @GetMapping("CacheVideoPointIDStatistics")
    public Object cacheVideoPointIDStatistics() {
        wtStatisticsService.cacheVideoPointIDStatistics();
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {Get} /CachedIntelDeviceStatistics 缓存智能设备统计个数
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName CacheVideoPointIDStatistics
     * @apiDescription 缓缓存智能设备统计个数，视频和IOT设备 (仅限服务内部调用)
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 应用权限 mdmbase:UpdateWtStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateWtStatistics", allowApplication = true, allowUser = false)
    @GetMapping("CachedIntelDeviceStatistics")
    public Object cachedIntelDeviceStatistics() {
        wtStatisticsService.cachedIntelDeviceStatistics();
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {Get} /CachedReservoirDetail 缓存水库详情
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName CachedReservoirDetail
     * @apiDescription 缓存水库详情 (仅限服务内部调用)
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 应用权限 mdmbase:UpdateWtStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateWtStatistics", allowApplication = true, allowUser = false)
    @GetMapping("CachedReservoirDetail")
    public Object CachedReservoirDetail() {
        wtStatisticsService.cachedReservoirDetail();
        return ResultWrapper.successWithNothing();
    }
}