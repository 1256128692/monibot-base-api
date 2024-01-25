package cn.shmedo.monitor.monibotbaseapi.controller.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 水库大屏统计模块
 *
 * @author Chengfs on 2024/1/23
 */
@RestController
@RequiredArgsConstructor
public class WtStatisticsController {

    /**
     * @api {POST} /ReservoirWarnStatistics 实时报警统计
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName WarnStatistics
     * @apiDescription 实时报警统计
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} projectID 项目id
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
     * @apiSuccess (返回结果) {Object[]} monitorPoint 监测点分类信息
     * @apiSuccess (返回结果) {Int} monitorPoint.id 监测点id
     * @apiSuccess (返回结果) {String} monitorPoint.name 监测点名称
     * @apiSuccess (返回结果) {Object} monitorPoint.detail 监测点报警统计信息
     * @apiSuccess (返回结果) {Int} monitorPoint.detail.level1 等级1数量
     * @apiSuccess (返回结果) {Int} monitorPoint.detail.level2 等级2数量
     * @apiSuccess (返回结果) {Int} monitorPoint.detail.level3 等级3数量
     * @apiSuccess (返回结果) {Int} monitorPoint.detail.level4 等级4数量
     * @apiSuccess (返回结果) {Int} monitorPoint.detail.offline 离线报警数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirWarnStatistics")
    public Object queryWarnStats() {
        return null;
    }

    /**
     * @api {POST} /DeviceOnlineStatistics 设备在线统计
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName DeviceOnlineStatistics
     * @apiDescription 设备在线统计
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} projectID 项目id
     * @apiSuccess (返回结果) {Int} count 总量
     * @apiSuccess (返回结果) {Int} online 在线数
     * @apiSuccess (返回结果) {Int} offline 离线数
     * @apiSuccess (返回结果) {Object[]} monitorType 监测类型分类
     * @apiSuccess (返回结果) {String} monitorType.name 监测类型名称
     * @apiSuccess (返回结果) {Int} monitorType.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} monitorType.count 总量
     * @apiSuccess (返回结果) {Int} monitorType.online 在线数
     * @apiSuccess (返回结果) {Int} monitorType.offline 离线数
     * @apiSuccessExample {json} Success-Response:
     *                    {"count": 99,"online": 90,"offline": 10,"monitorType": [{"name": "流量","monitorType": 1,"count": 99,"online": 90,"offline": 10}]}
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryDeviceOnlineStats")
    public Object deviceOnlineStats() {
        return null;
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
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirProjectStatistics")
    public Object reservoirProjectStatistics() {
        return null;
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
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirDeviceStatistics")
    public Object reservoirDeviceStatistics() {
        return null;
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
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirMonitorStatistics")
    public Object reservoirMonitorStatistics() {
        return null;
    }

    /**
     * @api {POST} /ReservoirNewSensorData 水库最新传感器数据
     * @apiVersion 1.0.0
     * @apiGroup 水库大屏统计模块
     * @apiName ReservoirNewSensorData
     * @apiDescription 水库最新传感器数据
     * @apiParam (请求参数) {Int} companyID 企业ID
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiSuccess (返回结果) {Int} projectID 工程项目ID
     * @apiSuccess (返回结果) {String} projectName 工程项目名称
     * @apiSuccess (返回结果) {String} shortName 工程项目短名称
     * @apiSuccess (返回结果) {String} reservoirScale 水库规模,1:小(Ⅰ)型水库,2:小(Ⅱ)型水库,3:中型水库,4:大(Ⅰ)型水库,5:大(Ⅱ)型水库
     * @apiSuccess (返回结果) {String} areaCode 行政区划code
     * @apiSuccess (返回结果) {String} areaName 行政区划名称
     * @apiSuccess (返回结果) {DateTime} dataTime 最新数据时间
     * @apiSuccess (返回结果) {Double} waterValue 水位值
     * @apiSuccess (返回结果) {Double} periodRainValue 时段降水量
     * @apiSuccess (返回结果) {Double} currentRainValue 当前降水量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("ReservoirNewSensorData")
    public Object reservoirNewSensorData() {
        return null;
    }



}