package cn.shmedo.monitor.monibotbaseapi.controller.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 水库统计模块
 *
 * @author Chengfs on 2024/1/23
 */
@RestController
@RequiredArgsConstructor
public class WtStatisticsController {

    /**
     * @api {POST} /WarnStatistics 实时报警统计
     * @apiVersion 1.0.0
     * @apiGroup 水库统计模块
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
     * @apiPermission 项目权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryWarnStats")
    public Object queryWarnStats() {
        return null;
    }

    /**
     * @api {POST} /DeviceOnlineStatistics 设备在线统计
     * @apiVersion 1.0.0
     * @apiGroup 水库统计模块
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
     * @apiPermission 项目权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseDashboard")
    @PostMapping("QueryDeviceOnlineStats")
    public Object deviceOnlineStats() {
        return null;
    }
}