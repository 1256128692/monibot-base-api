package cn.shmedo.monitor.monibotbaseapi.controller.reservoir;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.checktask.*;
import cn.shmedo.monitor.monibotbaseapi.service.CheckTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 水库巡检任务
 *
 * @author Chengfs on 2024/2/27
 */
@RestController
@RequiredArgsConstructor
public class CheckTaskController {

    private final CheckTaskService service;

    /**
     * @api {POST} /AddCheckTask 新增巡检任务
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName AddCheckTask
     * @apiDescription 新增巡检任务
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {Int} projectID 项目id
     * @apiParam (请求参数) {Int} checkType 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiParam (请求参数) {String} [name] 任务名称
     * @apiParam (请求参数) {Date} [taskDate] 任务日期(yyyy-MM-dd, 默认为当天)
     * @apiParam (请求参数) {String} [checkerID] 巡检人id(默认为当前用户)
     * @apiParam (请求参数) {Int[]} pointIDList 关联巡检点id列表(巡检点必须为启用状态)
     * @apiParam (请求参数) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {Int} data 巡检点id
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckTask
     */
    @Permission(permissionName = "mdmbase:UpdateCheckTask")
    @LogParam(moduleName = "巡检管理", operationName = "新建巡检任务", operationProperty = OperationProperty.ADD)
    @PostMapping(value = "AddCheckTask", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object add(@Valid @RequestBody AddCheckTaskRequest body) {
        return service.save(body);
    }

    /**
     * @api {POST} /UpdateCheckTask 更新巡检任务
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName UpdateCheckTask
     * @apiDescription 更新巡检任务 (!只能更新未开始的任务, 为空的字段不会更新)
     * @apiParam (请求参数) {Int} id 巡检任务id
     * @apiParam (请求参数) {Int} checkType 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiParam (请求参数) {String} name 任务名称
     * @apiParam (请求参数) {Date} [taskDate] 任务日期(yyyy-MM-dd)
     * @apiParam (请求参数) {String} [checkerID] 巡检人id
     * @apiParam (请求参数) {Int[]} pointIDList 关联巡检点id列表(巡检点必须为启用状态)
     * @apiParam (请求参数) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckTask
     */
    @Permission(permissionName = "mdmbase:UpdateCheckPoint")
    @LogParam(moduleName = "巡检管理", operationName = "修改巡检任务", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "UpdateCheckTask", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object update(@Valid @RequestBody UpdateCheckTaskRequest body) {
        service.update(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteCheckTask 删除巡检任务
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName DeleteCheckPoint
     * @apiDescription 删除巡检任务 (!只能删除未开始的任务)
     * @apiParam (请求参数) {Int[]} idList 巡检任务id集合
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteCheckTask
     */
    @Permission(permissionName = "mdmbase:DeleteCheckTask")
    @LogParam(moduleName = "巡检管理", operationName = "删除巡检任务", operationProperty = OperationProperty.DELETE)
    @PostMapping(value = "DeleteCheckTask", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object delete(@Valid @RequestBody DeleteCheckTaskRequest body) {
        service.delete(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryCheckTaskPage 查询巡检任务分页
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName QueryCheckTaskPage
     * @apiDescription 查询巡检任务分页
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {String} [keyword] 模糊搜索关键字(任务编码/巡检人员/任务名称)
     * @apiParam (请求参数) {String} [serviceID] 所属平台id
     * @apiParam (请求参数) {Int} [projectID] 项目id
     * @apiParam (请求参数) {Int} [checkType] 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiParam (请求参数) {Int} [status] 任务状态(0-未开始 1-进行中 2-已过期 3-已结束)
     * @apiParam (请求参数) {Int} currentPage 当前页(>0)
     * @apiParam (请求参数) {Int} pageSize 记录条数(1-100)
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 巡检任务id
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 项目id
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {String} currentPageData.serialNumber 巡检任务编号
     * @apiSuccess (返回结果) {Int} currentPageData.checkType 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiSuccess (返回结果) {String} currentPageData.name 巡检任务名称
     * @apiSuccess (返回结果) {Int} currentPageData.status 任务状态 0-未开始 1-进行中 2-已过期 3-已结束
     * @apiSuccess (返回结果) {Date} currentPageData.taskDate 任务日期
     * @apiSuccess (返回结果) {DateTime} [currentPageData.beginTime] 任务开始时间
     * @apiSuccess (返回结果) {DateTime} [currentPageData.endTime] 任务结束时间
     * @apiSuccess (返回结果) {Int} currentPageData.checkerID 巡检人id
     * @apiSuccess (返回结果) {String} currentPageData.checkerName 巡检人员名称
     * @apiSuccess (返回结果) {String} [currentPageData.exValue] 扩展字段
     * @apiSuccess (返回结果) {Object} statis 统计数据
     * @apiSuccess (返回结果) {Int} statis.totalCount 总数
     * @apiSuccess (返回结果) {Int} statis.notStartCount 未开始
     * @apiSuccess (返回结果) {Int} statis.endedCount 已结束
     * @apiSuccess (返回结果) {Int} statis.ongoingCount 进行中
     * @apiSuccess (返回结果) {Int} statis.expiredCount 已过期
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListCheckTask
     */
    @Permission(permissionName = "mdmbase:ListCheckTask")
    @PostMapping(value = "/QueryCheckTaskPage", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object page(@Valid @RequestBody QueryCheckTaskPageRequest body) {
        return service.page(body);
    }

    /**
     * @api {POST} /QueryCheckTaskList 查询巡检任务列表
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName QueryCheckTaskList
     * @apiDescription 查询巡检任务列表
     * @apiParam (请求参数) {Int} companyID 公司id
     * @apiParam (请求参数) {String} [keyword] 模糊搜索关键字(任务编码/巡检人员名称)
     * @apiParam (请求参数) {String} [serviceID] 所属平台id
     * @apiParam (请求参数) {Int} [projectID] 项目id
     * @apiParam (请求参数) {Int} [checkType] 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiParam (请求参数) {Int} [status] 任务状态(0-未开始 1-进行中 2-已过期 3-已结束)
     * @apiSuccess (返回结果) {Object[]} data 数据集
     * @apiSuccess (返回结果) {Int} data.id 巡检任务id
     * @apiSuccess (返回结果) {Int} data.projectID 项目id
     * @apiSuccess (返回结果) {String} data.projectName 工程名称
     * @apiSuccess (返回结果) {String} data.serialNumber 巡检任务编号
     * @apiSuccess (返回结果) {Int} data.checkType 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiSuccess (返回结果) {String} data.name 巡检任务名称
     * @apiSuccess (返回结果) {Int} data.status 任务状态 0-未开始 1-进行中 2-已过期 3-已结束
     * @apiSuccess (返回结果) {Date} taskDate 任务日期
     * @apiSuccess (返回结果) {DateTime} [data.beginTime] 任务开始时间
     * @apiSuccess (返回结果) {DateTime} [data.endTime] 任务结束时间
     * @apiSuccess (返回结果) {Int} data.checkerID 巡检人id
     * @apiSuccess (返回结果) {String} data.checkerName 巡检人员名称
     * @apiSuccess (返回结果) {String} [data.exValue] 扩展字段
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListCheckTask
     */
    @Permission(permissionName = "mdmbase:ListCheckTask")
    @PostMapping(value = "/QueryCheckTaskList", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object list(@Valid @RequestBody QueryCheckTaskListRequest body) {
        return service.list(body);
    }

    /**
     * @api {POST} /QueryCheckTask 查询巡检任务详情
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName QueryCheckTask
     * @apiDescription 查询巡检任务详情
     * @apiParam (请求参数) {Int} id 巡检任务id
     * @apiSuccess (返回结果) {Int} id 巡检任务id
     * @apiSuccess (返回结果) {Int} projectID 项目id
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {String} serialNumber 巡检任务编号
     * @apiSuccess (返回结果) {Int} checkType 巡检类型(0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查)
     * @apiSuccess (返回结果) {String} name 巡检任务名称
     * @apiSuccess (返回结果) {Int} status 任务状态 0-未开始 1-进行中 2-已过期 3-已结束
     * @apiSuccess (返回结果) {Date} taskDate 任务日期
     * @apiSuccess (返回结果) {DateTime} [beginTime] 任务开始时间
     * @apiSuccess (返回结果) {DateTime} [endTime] 任务结束时间
     * @apiSuccess (返回结果) {Int} checkerID 巡检人id
     * @apiSuccess (返回结果) {String} checkerName 巡检人员名称
     * @apiSuccess (返回结果) {String} [trajectory] 巡检轨迹
     * @apiSuccess (返回结果) {Int} [evaluate] 巡检评价  0-正常 1-异常
     * @apiSuccess (返回结果) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} createUserName 创建人
     * @apiSuccess (返回结果) {Int} createUserID 创建人id
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {String} updateUserName 更新人
     * @apiSuccess (返回结果) {Int} updateUserID 更新人id
     * @apiSuccess (返回结果) {DateTime} updateTime 更新时间
     * @apiSuccess (返回结果) {Object[]} notes 巡检记录
     * @apiSuccess (返回结果) {Int} notes.id 巡检点id
     * @apiSuccess (返回结果) {String} notes.name 巡检点名称
     * @apiSuccess (返回结果) {Object[]} notes.annexes 巡检记录附件
     * @apiSuccess (返回结果) {String} notes.annexes.name 附件名称
     * @apiSuccess (返回结果) {String} notes.annexes.url 附件地址
     * @apiSuccess (返回结果) {String} notes.remark 记录备注
     * @apiSuccess (返回结果) {Object[]} events 巡检事件
     * @apiSuccess (返回结果) {Int} events.id 事件id
     * @apiSuccess (返回结果) {Int} events.typeID 事件类型id
     * @apiSuccess (返回结果) {String} events.typeName 事件类型名称
     * @apiSuccess (返回结果) {String} events.address 事件地址
     * @apiSuccess (返回结果) {String} events.location 事件地址经纬度
     * @apiSuccess (返回结果) {String} events.describe 事件描述
     * @apiSuccess (返回结果) {Object[]} events.annexes 巡检记录附件
     * @apiSuccess (返回结果) {String} events.annexes.name 附件名称
     * @apiSuccess (返回结果) {String} events.annexes.url 附件地址
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeCheckTask
     */
    @Permission(permissionName = "mdmbase:DescribeCheckTask")
    @PostMapping(value = "/QueryCheckTask", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object single(@Valid @RequestBody QueryCheckTaskRequest body) {
        return service.single(body);
    }

    /**
     * @api {POST} /StartCheckTask 开始巡检任务 (App)
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName StartCheckTask
     * @apiDescription 开始巡检任务 (!仅能开始当天属于自己且未开始的任务)
     * @apiParam (请求参数) {Int} taskID 巡检任务id
     * @apiParam (请求参数) {DateTime} [startTime] 任务开始时间(yyyy-MM-dd HH:mm:ss, 默认为当前时间)
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckTask
     */
    @Permission(permissionName = "mdmbase:UpdateCheckTask")
    @LogParam(moduleName = "巡检管理", operationName = "开始巡检任务", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "StartCheckTask", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object startTask(@Valid @RequestBody StartTaskRequest body) {
        service.startTask(body);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /EndCheckTask 结束巡检任务 (App)
     * @apiVersion 1.0.0
     * @apiGroup 水库-巡检任务模块
     * @apiName EndCheckTask
     * @apiDescription 结束巡检任务 (!仅能结束自己正在进行中的任务)
     * @apiParam (请求参数) {Int} taskID 巡检任务id
     * @apiParam (请求参数) {Object[]} notes 巡检记录
     * @apiParam (请求参数) {Int} notes.pointID 关联巡检点id
     * @apiParam (请求参数) {String[]} notes.annexes 巡检记录附件集合(osskey, 最多9)
     * @apiParam (请求参数) {String} notes.remark 巡检记录备注
     * @apiParam (请求参数) {Int} evaluate 巡检评价  0-正常 1-异常
     * @apiParam (请求参数) {String} [trajectory] 巡检轨迹
     * @apiParam (请求参数) {DateTime} endTime 任务结束时间(yyyy-MM-dd HH:mm:ss)
     * @apiParam (请求参数) {String} [exValue] 扩展字段
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateCheckTask
     */
    @Permission(permissionName = "mdmbase:UpdateCheckTask")
    @LogParam(moduleName = "巡检管理", operationName = "结束巡检任务", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "EndCheckTask", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object endTask(@Valid @RequestBody EndTaskRequest body) {
        service.endTask(body);
        return ResultWrapper.successWithNothing();
    }
}