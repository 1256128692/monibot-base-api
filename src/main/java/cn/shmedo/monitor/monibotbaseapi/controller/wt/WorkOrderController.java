package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WorkOrderController {
    /**
     * @api {POST} /QueryWorkOrderPage 查询工单分页
     * @apiVersion 1.0.0
     * @apiGroup 在线监测工单模块
     * @apiName QueryWorkOrderPage
     * @apiDescription 查询工单分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiParam (请求参数) {Int} [status] 工单状态,默认0.全部 0.全部 1.待接单 2.处置中 3.已处置 4.审核中 5.已结束 6.已关闭
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询编号、名称、所属组织、姓名
     * @apiParam (请求参数) {Int} [workOrderTypeID] 工单类型ID,由工作流配置产生
     * @apiParam (请求参数) {DateTime} [startTime] 派单开始时间
     * @apiParam (请求参数) {DateTime} [endTime] 派单结束时间,默认是现在
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.workOrderID 工单ID
     * @apiSuccess (返回结果) {String} currentPageData.orderCode 工单编号
     * @apiSuccess (返回结果) {Int} currentPageData.typeID 工单类型ID
     * @apiSuccess (返回结果) {String} currentPageData.typeName 工单类型名称
     * @apiSuccess (返回结果) {Int} currentPageData.organizationID 所属组织ID
     * @apiSuccess (返回结果) {String} currentPageData.organizationName 所属组织名称
     * @apiSuccess (返回结果) {String} currentPageData.dispatcherName 派单人名称
     * @apiSuccess (返回结果) {DateTime} currentPageData.dispatchTime 派单时间
     * @apiSuccess (返回结果) {String} [currentPageData.disposerName] 处置人名称
     * @apiSuccess (返回结果) {DateTime} [currentPageData.disposeTime] 处置完成时间
     * @apiSuccess (返回结果) {Int} currentPageData.status 工单状态,默认0.全部 0.全部 1.待接单 2.处置中 3.已处置 4.审核中 5.已结束 6.已关闭
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @PostMapping(value = "/QueryWorkOrderPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWorkOrderPage(@Valid @RequestBody Object param) {
        return null;
    }

    /**
     * @api {POST} /QueryWarnDetail 查询报警详情记录
     * @apiVersion 1.0.0
     * @apiGroup 在线监测工单模块
     * @apiName QueryWarnDetail
     * @apiDescription 查询报警详情记录
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} workOrderID 工单ID
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {Int} monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} monitorPointLocation 监测点位置
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Int} warnLevel 报警等级
     * @apiSuccess (返回结果) {DateTime} warnTime 报警时间
     * @apiSuccess (返回结果) {String} warnContext 报警内容
     * @apiSampleRequest off
     * @apiPermission 系统权限
     */
    @PostMapping(value = "/QueryWarnDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnDetail(@Valid @RequestBody Object param) {
        return null;
    }

    /**
     * @api {POST} /DeleteWorkOrder 删除工单
     * @apiVersion 1.0.0
     * @apiGroup 在线监测工单模块
     * @apiName DeleteWorkOrder
     * @apiDescription 删除工单
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} workOrderIDList 工单ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限
     */
    @PostMapping(value = "/DeleteWorkOrder", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteWorkOrder(@Valid @RequestBody Object param) {
        return null;
    }

    /**
     * @api {POST} /DispatchWorkOrder 下派工单
     * @apiVersion 1.0.0
     * @apiGroup 在线监测工单模块
     * @apiName DispatchWorkOrder
     * @apiDescription 下派工单
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} warnLogIDList 警报ID列表
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限
     */
    @PostMapping(value = "/DispatchWorkOrder", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object dispatchWorkOrder(@Valid @RequestBody Object param) {
        return null;
    }
}
