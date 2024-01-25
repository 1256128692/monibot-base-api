package cn.shmedo.monitor.monibotbaseapi.controller.dashboard;

import cn.shmedo.iot.entity.annotations.Permission;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2024/1/24 15:11
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller.dashboard
 * @ClassName: WorkFlowDashboardController
 * @Description: 工作流大屏统计
 * @Version 1.0
 */
@RestController
@RequiredArgsConstructor
public class WorkFlowDashboardController {
    /**
     * @api {POST} /QueryInspectionTask 查询巡检任务统计
     * @apiVersion 1.0.0
     * @apiGroup 工作流大屏模块
     * @apiName QueryInspectionTask
     * @apiDescription 查询巡检任务统计
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} date 日期，可具体到月份
     * @apiSuccess (返回结果) {Int} total 问题总数
     * @apiSuccess (返回结果) {Int} handledTotal 已处理总数
     * @apiSuccess (返回结果) {Int} pendingTotal 待处理总数
     * @apiSuccess (返回结果) {Object[]} weekList 按周统计列表（一个月最少四周，最多六周）
     * @apiSuccess (返回结果) {String} weekList.startDate 开始时间
     * @apiSuccess (返回结果) {String} weekList.endDate 截止时间
     * @apiSuccess (返回结果) {Int} weekList.handledTotal 已处理总数
     * @apiSuccess (返回结果) {Int} weekList.handledTotal 待处理总数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "DescribeBaseDashboard")
    @PostMapping("QueryInspectionTask")
    public Object queryInspectionTask(@RequestBody @Valid Object object){
        return null;
    }


    /**
     * @api {POST} /QueryProblemHandling 查询问题处置统计
     * @apiVersion 1.0.0
     * @apiGroup 工作流大屏模块
     * @apiName QueryResourceOverview
     * @apiDescription 查询问题处置统计
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} date 日期（具体到月份）
     * @apiSuccess (返回结果) {Int} total 问题总数
     * @apiSuccess (返回结果) {Int} handledTotal 已处理总数
     * @apiSuccess (返回结果) {Int} pendingTotal 待处理总数
     * @apiSuccess (返回结果) {Object[]} weekList 按周统计列表（一个月最少四周，最多六周）
     * @apiSuccess (返回结果) {String} weekList.startDate 开始时间（具体到天）
     * @apiSuccess (返回结果) {String} weekList.endDate 截止时间（具体到天）
     * @apiSuccess (返回结果) {Int} weekList.handledTotal 已处理总数
     * @apiSuccess (返回结果) {Int} weekList.handledTotal 待处理总数
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDashboard
     */
    @Permission(permissionName = "DescribeBaseDashboard")
    @PostMapping("QueryProblemHandling")
    public Object queryProblemHandling(@RequestBody @Valid Object object){
        return null;
    }

}
