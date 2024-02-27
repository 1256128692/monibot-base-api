package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.AddMonitorGroupParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorGroupService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CheckEventController {

    private final MonitorGroupService monitorGroupService;

    /**
     * @api {POST} /AddEventType 新建事件类型
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName AddEventType
     * @apiDescription 新建事件类型
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} name 事件名称
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddEvent
     */
    @LogParam(moduleName = "事件模块", operationName = "新建事件类型", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddEventType")
    @PostMapping(value = "/AddEventType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addEventType(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /DeleteEventType 删除事件类型
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName DeleteEventType
     * @apiDescription 删除事件类型,有未处理事件的时候,不可删除,事件类型擅长后,同类型的事件要同步删除
     * @apiParam (请求体) {Int} id 事件id
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteEvent
     */
    @LogParam(moduleName = "事件模块", operationName = "删除事件类型", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteEventType")
    @PostMapping(value = "/DeleteEventType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteEventType(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /UpdateEventType 编辑事件类型
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName UpdateEventType
     * @apiDescription 编辑事件类型
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} id 事件id
     * @apiParam (请求体) {String} name 事件名称
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateEvent
     */
    @LogParam(moduleName = "事件模块", operationName = "更新事件类型", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateEventType")
    @PostMapping(value = "/UpdateEventType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateEventType(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryEventTypeList 查询事件类型列表
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName QueryEventTypeList
     * @apiDescription 查询事件类型列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [name] 事件名称,模糊匹配
     * @apiSuccess (响应结果) {Object[]} data   事件类型列表
     * @apiSuccess (响应结果) {Int} data.id  事件类型ID
     * @apiSuccess (响应结果) {String} data.name   名称
     * @apiSuccess (响应结果) {String} data.exValue 备注
     * @apiSuccess (响应结果) {Date} data.createTime 创建时间
     * @apiSuccess (响应结果) {Int} data.createUserID  创建人ID
     * @apiSuccess (响应结果) {Date} data.updateTime 最后更新时间
     * @apiSuccess (响应结果) {Int} data.updateUserID  最后更新人ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListEventInfo
     */
    @Permission(permissionName = "mdmbase:ListEventInfo")
    @PostMapping(value = "/QueryEventTypeList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryEventTypeList(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /AddEventInfo 新增事件信息
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName AddEventInfo
     * @apiDescription 新增事件信息,事件编码 形如 E2024020200001,最后00001要逐次+1,ReportUserID提报人ID就是当前用户ID
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 关联工程ID
     * @apiParam (请求体) {Int} [taskID] 关联巡检任务ID
     * @apiParam (请求体) {Int} [orderID] 工单ID
     * @apiParam (请求体) {Int} typeID 事件类型ID
     * @apiParam (请求体) {String} address 事件位置
     * @apiParam (请求体) {String} location 事件经纬度
     * @apiParam (请求体) {String} describe 事件描述
     * @apiParam (请求体) {String} [annexes] 附件文件osskey,json格式的["ali-oss-e2bd4f11-8b07-45ef-9e96-9005665b2148"]
     * @apiParam (请求体) {Date}   handleTime 处理时间
     * @apiParam (请求体) {Int} status 事件状态 0-未处理 1-已处理
     * @apiParam (请求体) {String} comment 结束批注
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddEvent
     */
    @LogParam(moduleName = "事件模块", operationName = "新增事件", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:AddEvent")
    @PostMapping(value = "/AddEventInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addEventInfo(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /DeleteEventInfo 删除事件信息
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName DeleteEventInfo
     * @apiDescription 删除事件信息
     * @apiParam (请求体) {Int[]} eventIDList 事件ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteEvent
     */
    @LogParam(moduleName = "事件模块", operationName = "删除事件", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteEvent")
    @PostMapping(value = "/DeleteEventInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteEventInfo(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /UpdateEventInfo 编辑事件信息
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName UpdateEventInfo
     * @apiDescription 编辑事件信息,ReportUserID提报人ID就是当前用户ID
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [orderID] 事件ID
     * @apiParam (请求体) {Int} [eventID] 事件ID
     * @apiParam (请求体) {Int} [projectID] 关联工程ID
     * @apiParam (请求体) {Int} [taskID] 关联巡检任务ID
     * @apiParam (请求体) {Int} [typeID] 事件类型ID
     * @apiParam (请求体) {String} [address] 事件位置
     * @apiParam (请求体) {String} [location] 事件经纬度
     * @apiParam (请求体) {String} [describe] 事件描述
     * @apiParam (请求体) {String} [annexes] 附件文件osskey,json格式的["ali-oss-e2bd4f11-8b07-45ef-9e96-9005665b2148"]
     * @apiParam (请求体) {Date}   [handleTime] 处理时间
     * @apiParam (请求体) {Int} [status] 事件状态 0-未处理 1-已处理
     * @apiParam (请求体) {String} [comment] 结束批注
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateEvent
     */
    @LogParam(moduleName = "事件模块", operationName = "编辑事件", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateEvent")
    @PostMapping(value = "/UpdateEventInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateEventInfo(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }



    /**
     * @api {POST} /QueryEventInfoPage 查询事件分页
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName QueryEventInfoPage
     * @apiDescription 查询事件分页
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {String} [queryContent] 超级搜索(提报人/事件编码)
     * @apiParam (请求体) {Int} [serviceID] 所属平台ID
     * @apiParam (请求体) {Int} [eventTypeID] 事件类型
     * @apiParam (请求体) {Int} [eventStatus] 事件状态 0-未处理 1-已处理
     * @apiParam (请求体) {Int} [projectID] 工程ID
     * @apiParam (请求体) {Date} [begin] 开始时间
     * @apiParam (请求体) {Date} [end] 结束时间
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (响应结果) {Int} totalCount 总数量
     * @apiSuccess (响应结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (响应结果) {Int} currentPageData.eventID  事件ID
     * @apiSuccess (响应结果) {Int} currentPageData.status  事件状态 0-未处理 1-已处理
     * @apiSuccess (响应结果) {String} currentPageData.eventSerialNumber 事件编码
     * @apiSuccess (响应结果) {Int} currentPageData.projectID   工程ID
     * @apiSuccess (响应结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (响应结果) {Int} currentPageData.taskID 关联巡检任务ID
     * @apiSuccess (响应结果) {String} currentPageData.taskName  关联巡检任务名称
     * @apiSuccess (响应结果) {String} currentPageData.taskSerialNumber  任务编码
     * @apiSuccess (响应结果) {Int} currentPageData.typeID 事件类型ID
     * @apiSuccess (响应结果) {String} currentPageData.typeName  事件名称
     * @apiSuccess (响应结果) {String} currentPageData.address   事件位置
     * @apiSuccess (响应结果) {String} currentPageData.location 事件经纬度
     * @apiSuccess (响应结果) {String} currentPageData.describe 事件描述
     * @apiSuccess (响应结果) {Int}  currentPageData.reportUserID  提报人ID
     * @apiSuccess (响应结果) {String}  currentPageData.reportUserName  提报人姓名
     * @apiSuccess (响应结果) {Date} currentPageData.reportTime 提报时间
     * @apiSuccess (响应结果) {Date} currentPageData.handleTime 处理时间
     * @apiSuccess (响应结果) {String} currentPageData.comment  结束批注
     * @apiSuccess (响应结果) {String} currentPageData.exValue  备注
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListEventInfo
     */
    @Permission(permissionName = "mdmbase:ListEventInfo")
    @PostMapping(value = "/QueryEventInfoPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryEventInfoPage(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryEventInfoDetail 查询事件详情
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName QueryEventInfoDetail
     * @apiDescription 查询事件详情
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} eventID 事件ID
     * @apiSuccess (响应结果) {Int}  eventID  事件ID
     * @apiSuccess (响应结果) {Int}  status  事件状态 0-未处理 1-已处理
     * @apiSuccess (响应结果) {String}  eventSerialNumber 事件编码
     * @apiSuccess (响应结果) {Int}  projectID   工程ID
     * @apiSuccess (响应结果) {String}  projectName 工程名称
     * @apiSuccess (响应结果) {Int}  taskID 关联巡检任务ID
     * @apiSuccess (响应结果) {String}  taskName  关联巡检任务名称
     * @apiSuccess (响应结果) {String}  taskSerialNumber  任务编码
     * @apiSuccess (响应结果) {Int}  checkerID  关联巡检任务巡检人ID
     * @apiSuccess (响应结果) {String}  checkerName  关联巡检任务巡检人名称
     * @apiSuccess (响应结果) {String}  taskBeginTime  关联巡检任务开始时间
     * @apiSuccess (响应结果) {String}  taskEndTime  关联巡检任务结束时间
     * @apiSuccess (响应结果) {Int}  orderID  工单ID
     * @apiSuccess (响应结果) {Int}  typeID 事件类型ID
     * @apiSuccess (响应结果) {String}  typeName  事件名称
     * @apiSuccess (响应结果) {String}  address   事件位置
     * @apiSuccess (响应结果) {String}  location 事件经纬度
     * @apiSuccess (响应结果) {String}  describe 事件描述
     * @apiSuccess (响应结果) {Date}  handleTime 处理时间
     * @apiSuccess (响应结果) {String}  comment  结束批注
     * @apiSuccess (响应结果) {String}  exValue  备注
     * @apiSuccess (响应结果) {Int}  reportUserID  提报人ID
     * @apiSuccess (响应结果) {String}  reportUserName  提报人姓名
     * @apiSuccess (响应结果) {Date}  reportTime  提报时间
     * @apiSuccess (响应结果) {Object[]}  fileInfoList  文件列表
     * @apiSuccess (响应结果) {String}  fileInfoList.filePath  文件地址
     * @apiSuccess (响应结果) {String}  fileInfoList.fileName  文件名称
     * @apiSuccess (响应结果) {String}  fileInfoList.fileType  文件类型
     * @apiSuccess (响应结果) {String}  fileInfoList.fileSize  文件大小
     * @apiSuccess (响应结果) {String}  fileInfoList.createTime  文件上传时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListEventInfo
     */
    @Permission(permissionName = "mdmbase:ListEventInfo")
    @PostMapping(value = "/QueryEventInfoDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryEventInfoDetail(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryDailyTaskList 查询日巡检任务列表
     * @apiVersion 1.0.0
     * @apiGroup 事件模块
     * @apiName QueryDailyTaskList
     * @apiDescription 查询日巡检任务列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [serviceID] 所属平台ID
     * @apiParam (请求体) {Int} [projectID] 工程ID
     * @apiParam (请求体) {Int} queryType 查询方式,(全部日程:0 个人视角:1)
     * @apiParam (请求体) {Date} begin 开始时间
     * @apiParam (请求体) {Date} end 结束时间
     * @apiSuccess (响应结果) {Object[]} data 数据
     * @apiSuccess (响应结果) {Int} data.taskID  任务ID
     * @apiSuccess (响应结果) {String} data.taskName  任务名称
     * @apiSuccess (响应结果) {String} data.taskSerialNumber 任务编号
     * @apiSuccess (响应结果) {Int} data.checkType   任务类型
     * @apiSuccess (响应结果) {Int} data.status 任务状态
     * @apiSuccess (响应结果) {Date} data.taskDate 任务日期
     * @apiSuccess (响应结果) {String} data.projectID  关联项目ID
     * @apiSuccess (响应结果) {Object} data.taskStatusInfo  任务状态统计
     * @apiSuccess (响应结果) {Int} data.taskStatusInfo.unpreparedCount  未开始任务数量
     * @apiSuccess (响应结果) {Int} data.taskStatusInfo.underwayCount  进行中任务数量
     * @apiSuccess (响应结果) {Int} data.taskStatusInfo.expiredCount  已过期任务数量
     * @apiSuccess (响应结果) {Int} data.taskStatusInfo.finishedCount  已结束任务数量
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListEventInfo
     */
    @Permission(permissionName = "mdmbase:ListEventInfo")
    @PostMapping(value = "/QueryDailyTaskList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDailyTaskList(@RequestBody @Validated Object pa) {
//        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }
}
