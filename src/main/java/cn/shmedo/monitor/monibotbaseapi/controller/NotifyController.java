package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.notify.QueryNotifyPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.CompanyPlatformParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryNotifyListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.QueryWarnNotifyPageParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.notify.NotifyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author wuxl
 * @Date 2024/3/1 14:25
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.controller
 * @ClassName: NotifyController
 * @Description: 消息通知
 * @Version 1.0
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class NotifyController {
    private final NotifyService notifyService;

    /**
     * @api {POST} /QueryNotifyPage 查询消息通知分页
     * @apiVersion 1.0.0
     * @apiGroup 消息通知模块
     * @apiName QueryNotifyPage
     * @apiDescription 查询消息通知分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索标题或内容
     * @apiParam (请求参数) {Int} [status] 0.未读 1.已读
     * @apiParam (请求参数) {Date} [startTime] 开始时间
     * @apiParam (请求参数) {Date} [endTime] 结束时间
     * @apiParam (请求参数) {Int} [timeOrder] 时间排序 0.降序（默认） 1.升序
     * @apiParam (请求参数) {Int} [serviceID] 平台ID
     * @apiParam (请求参数) {Int} [type] 消息类型 1.报警 2.事件 3.工单
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.notifyID 系统通知ID
     * @apiSuccess (返回结果) {Int} currentPageData.serviceID 平台ID
     * @apiSuccess (返回结果) {Int} currentPageData.serviceName 平台名称
     * @apiSuccess (返回结果) {Int} currentPageData.type 类型，1.报警 2.事件 3.工单
     * @apiSuccess (返回结果) {String} currentPageData.name 通知名称
     * @apiSuccess (返回结果) {String} currentPageData.content 通知内容
     * @apiSuccess (返回结果) {Int} currentPageData.status 通知状态 0.未读 1.已读 2.待办
     * @apiSuccess (返回结果) {DateTime} currentPageData.time 接收时间
     * @apiSuccess (返回结果) {Int} [currentPageData.relationID] 关联ID,如果该项为空则不可跳转
     * @apiSuccess (返回结果) {Int} [currentPageData.relationType] 关联类型, 1.数据报警 2.设备报警 3.事件 4.工单
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeNotify
     */
    @Permission(permissionName = "mdmbase:DescribeNotify")
    @PostMapping(value = "/QueryNotifyPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryNotifyPage(@Valid @RequestBody QueryNotifyPageParam param, HttpServletRequest request) {
        return notifyService.queryNotifyPage(param, request.getHeader("Authorization"));
    }


    /**
     * @api {POST} /QueryNotifyList 查询消息通知列表
     * @apiVersion 1.0.0
     * @apiGroup 消息通知模块
     * @apiName QueryNotifyList
     * @apiDescription 查询消息通知列表
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [status] 0.未读 1.已读
     * @apiParam (请求参数) {Int} [serviceID] 平台ID
     * @apiSuccess (返回结果) {Object[]} dataList 当前页数据
     * @apiSuccess (返回结果) {Int} dataList.notifyID 系统通知ID
     * @apiSuccess (返回结果) {Int} dataList.serviceID 平台ID
     * @apiSuccess (返回结果) {Int} dataList.serviceName 平台名称
     * @apiSuccess (返回结果) {Int} dataList.type 类型，1.报警 2.事件 3.工单
     * @apiSuccess (返回结果) {String} dataList.name 通知名称
     * @apiSuccess (返回结果) {String} dataList.content 通知内容
     * @apiSuccess (返回结果) {Int} dataList.status 通知状态 0.未读 1.已读 2.待办
     * @apiSuccess (返回结果) {DateTime} dataList.time 接收时间
     * @apiSuccess (返回结果) {Int} [dataList.relationID] 关联ID,如果该项为空则不可跳转
     * @apiSuccess (返回结果) {Int} [dataList.relationType] 关联类型, 1.数据报警 2.设备报警 3.事件 4.工单
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeNotify
     */
    @Permission(permissionName = "mdmbase:DescribeNotify")
    @PostMapping(value = "/QueryNotifyList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryNotifyList(@Valid @RequestBody QueryNotifyListParam param, HttpServletRequest request) {
        return notifyService.queryNotifyList(param, request.getHeader("Authorization"));
    }
}
