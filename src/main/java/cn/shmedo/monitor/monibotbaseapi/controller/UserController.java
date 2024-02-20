package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.model.param.userLog.QueryUserOperationLogParameter;
import cn.shmedo.monitor.monibotbaseapi.service.ITbUserLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户操作日志
 *
 * @author Chengfs on 2024/2/20
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final ITbUserLogService userLogService;

    /**
     * @api {POST} /QueryUserOperationLog 分页根据日期分组查询用户操作日志
     * @apiDescription 分页查询用户操作日志(日志仅保存一个月)，根据日期分组
     * @apiVersion 1.0.0
     * @apiGroup 用户日志模块
     * @apiName QueryUserOperationLog
     * @apiParam {Int} companyID 公司ID
     * @apiParam {String} [operationType] 操作类型
     * @apiParam {Int} [userID] 操作用户ID
     * @apiParam {String} [modelName] 模块名称，支持模糊查询
     * @apiParam {String} [operationName] 操作名称，支持模糊查询
     * @apiParam {DateTime} begin 开始时间 (yyyy-MM-dd HH:mm:ss)
     * @apiParam {DateTime} end 结束时间 (yyyy-MM-dd HH:mm:ss)
     * @apiParam {Int} pageSize 页大小，范围为[1-100]
     * @apiParam {Int} currentPage 当前页，从1开始
     * @apiSuccess (返回结果) {Int} totalCount 查询总数
     * @apiSuccess (返回结果) {Int} totalPage 分页总数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {String} currentPageData.date 操作日期，yyyy-mm-dd
     * @apiSuccess (返回结果) {Object[]} currentPageData.dataList 操作日期下日志列表
     * @apiSuccess (返回结果) {Int} currentPageData.dateList.id 操作日志编号
     * @apiSuccess (返回结果) {Int} currentPageData.dateList.companyID 公司ID
     * @apiSuccess (返回结果) {Int} currentPageData.dateList.userID 用户ID
     * @apiSuccess (返回结果) {String} currentPageData.dateList.userName 用户名
     * @apiSuccess (返回结果) {DataTime} currentPageData.dateList.operationDate 操作日期
     * @apiSuccess (返回结果) {String} currentPageData.dateList.operationIP 操作IP
     * @apiSuccess (返回结果) {String} currentPageData.dateList.modelName 模块名称
     * @apiSuccess (返回结果) {String} currentPageData.dateList.operationName 操作名称
     * @apiSuccess (返回结果) {String} currentPageData.dateList.operationProperty 操作类型
     * @apiSuccess (返回结果) {String} currentPageData.dateList.operationPath 操作接口名称
     * @apiSuccess (返回结果) {String} currentPageData.dateList.operationParams 操作接口参数信息
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseUserLog
     */
    @Permission(permissionName = "mdmbase:ListBaseUserLog")
    @RequestMapping(value = "/QueryUserOperationLog", method = RequestMethod.POST, consumes = CommonVariable.JSON, produces = CommonVariable.JSON)
    public Object queryUserOperationLog(@RequestBody @Valid @NotNull QueryUserOperationLogParameter pa) {
        return userLogService.queryUserOperationLog(pa);
    }
}