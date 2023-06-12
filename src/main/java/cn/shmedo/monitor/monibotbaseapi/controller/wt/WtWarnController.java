package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.AddWarnLogBindWarnOrderParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtTerminalWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warn.QueryWtWarnLogPageParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnLogService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WtWarnController {
    private final ITbWarnLogService tbWarnLogService;
    /**
     * @api {POST} /QueryWtWarnPage 查询报警分页
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtWarnPage
     * @apiDescription 查询报警分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} queryType 查询类型 1.实时记录 2.历史记录
     * @apiParam (请求参数) {String} [queryCode] 检索关键字<br/>1-在线监测报警，模糊范围: 报警名称、报警内容<br/>2-视频/摄像头报警，模糊范围: 设备SN、报警名称、监测点名称
     * @apiParam (请求参数) {Int} [monitorTypeID] 监测类型ID
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [warnLevel] 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiParam (请求参数) {Int} [orderType] 排序规则 1.按照报警时间降序排序(默认) 2.按照报警时间升序排序
     * @apiParam (请求参数) {Int} [warnType] 报警类型 1-在线监测报警(默认值) 2-视频报警记录
     * @apiParam (请求参数) {DateTime} [beginTime] 报警开始时间，仅在warnType=2时有效 (默认为7天前)
     * @apiParam (请求参数) {DateTime} [endTime] 报警结束时间，仅在warnType=2时有效（默认为当前时间）
     * @apiParam (请求参数) {Int} [status] 工单状态，仅在warnType=2时有效，1待接单/2处置中/3已处置/4审核中/5已结束/6已关闭
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.warnID 报警记录ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} currentPageData.warnLevel 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiSuccess (返回结果) {DateTime} currentPageData.warnTime 报警时间
     * @apiSuccess (返回结果) {String} currentPageData.warnContent 报警内容
     * @apiSuccess (返回结果) {Int} currentPageData.workOrderID 关联工单ID,若为空表示该工单暂未
     * @apiSuccess (返回结果) {String} currentPageData.orderCode 关联工单编号
     * @apiSuccess (返回结果) {Int} currentPageData.orderStatus 处置状态 1.待接单 2.处置中 3.已处置 4.审核中 5.已结束 6.已关闭
     * @apiSuccess (返回结果) {String} currentPageData.deviceToken 设备SN，仅在queryType=2时有效
     * @apiSuccess (返回结果) {String} currentPageData.deviceTypeName 设备型号（对应物联网产品名称），仅在queryType=2时有效
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseWarn
     */
    @Permission(permissionName = "mdmbase:ListBaseWarn")
    @PostMapping(value = "/QueryWtWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtWarnPage(@Valid @RequestBody QueryWtWarnLogPageParam param) {
        return tbWarnLogService.queryByPage(param);
    }

    /**
     * @api {POST} /QueryWtWarnInfoDetail 查询报警详情
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtWarnInfoDetail
     * @apiDescription 查询报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnID 报警记录ID
     * @apiSuccess (返回结果) {Int} warnID 报警记录ID
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {Int} monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} warnLevel 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiSuccess (返回结果) {String} warnContent 报警内容
     * @apiSuccess (返回结果) {Int} monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} monitorPointLocation 监测点位置
     * @apiSuccess (返回结果) {DateTime} warnTime 报警时间
     * @apiSuccess (返回结果) {String} fieldToken 数据源Token
     * @apiSuccess (返回结果) {String} fieldToken 数据源token
     * @apiSuccess (返回结果) {String} compareRule 比较区间json
     * @apiSuccess (返回结果) {String} triggerRule 触发规则json
     * @apiSuccess (返回结果) {Int} workOrderID 工单ID
     * @apiSuccess (返回结果) {String} workOrderCode 工单编号
     * @apiSuccess (返回结果) {String} deviceToken 设备SN
     * @apiSuccess (返回结果) {String} deviceTypeName 设备型号（对应物联网产品名称）
     * @apiSuccess (返回结果) {String} regionArea 行政区划，仅视频/摄像头报警
     * @apiSuccess (返回结果) {String} ruleName 规则名称，仅视频/摄像头报警
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseWarn
     */
    @Permission(permissionName = "mdmbase:DescribeBaseWarn")
    @PostMapping(value = "/QueryWtWarnInfoDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtWarnInfoDetail(@Valid @RequestBody QueryWtWarnDetailParam param) {
        return tbWarnLogService.queryDetail(param);
    }

    /**
     * @api {POST} /QueryWtTerminalWarnPage 查询智能终端报警分页
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtTerminalWarnPage
     * @apiDescription 查询智能终端报警分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} queryType 查询类型 1.实时记录 2.历史记录
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索报警名称、工程名称、设备SN、监测点
     * @apiParam (请求参数) {Int} [monitorTypeID] 监测类型ID
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [warnLevel] 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiParam (请求参数) {Int} [orderType] 排序规则 1.按照报警时间降序排序(默认) 2.按照报警时间升序排序
     * @apiParam (请求参数) {DateTime} [beginTime] 报警开始时间 (默认为7天前)
     * @apiParam (请求参数) {DateTime} [endTime] 报警结束时间（默认为当前时间）
     * @apiParam (请求参数) {Int} [status] 工单状态 1待接单/2处置中/3已处置/4审核中/5已结束/6已关闭
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.warnID 报警记录ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Object[]} currentPageData.projectList 工程列表
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.projectName 工程名称
      * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorPointList.monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} currentPageData.warnLevel 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiSuccess (返回结果) {DateTime} currentPageData.warnTime 报警时间
     * @apiSuccess (返回结果) {String} currentPageData.warnContent 报警内容
     * @apiSuccess (返回结果) {Int} currentPageData.workOrderID 关联工单ID,若为空表示该工单暂未
     * @apiSuccess (返回结果) {String} currentPageData.orderCode 关联工单编号
     * @apiSuccess (返回结果) {Int} currentPageData.orderStatus 处置状态 1.待接单 2.处置中 3.已处置 4.审核中 5.已结束 6.已关闭
     * @apiSuccess (返回结果) {String} currentPageData.deviceToken 设备SN
     * @apiSuccess (返回结果) {String} currentPageData.deviceTypeName 设备型号（对应物联网产品名称）
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseWarn
     */
    @Permission(permissionName = "mdmbase:ListBaseWarn")
    @PostMapping(value = "/QueryWtTerminalWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtTerminalWarnLogPage(@Valid @RequestBody QueryWtTerminalWarnLogPageParam param) {
        return tbWarnLogService.queryTerminalWarnPage(param);
    }

    /**
     * @api {POST} /QueryWtTerminalWarnInfo 查询智能终端报警详情
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtTerminalWarnInfo
     * @apiDescription 查询智能终端报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnID 报警记录ID
     * @apiSuccess (返回结果) {Int} warnID 报警记录ID
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Object[]} projectList 工程列表
     * @apiSuccess (返回结果) {Int} projectList.projectID 工程ID
     * @apiSuccess (返回结果) {String} projectList.projectName 工程名称
     * @apiSuccess (返回结果) {String} projectList.monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} projectList.monitorPointList.monitorTypeID 监测类型ID
     * @apiSuccess (返回结果) {String} projectList.monitorPointList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} projectList.monitorPointList.monitorTypeAlias 监测类型别称
     * @apiSuccess (返回结果) {Int} projectList.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} projectList.monitorPointList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} projectList.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} projectList.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} projectList.monitorPointList.monitorPointLocation 监测点名称
     * @apiSuccess (返回结果) {Int} warnLevel 报警等级 1.Ⅰ级 2.Ⅱ级 3.Ⅲ级 4.Ⅳ级
     * @apiSuccess (返回结果) {String} warnContent 报警内容
     * @apiSuccess (返回结果) {DateTime} warnTime 报警时间
     * @apiSuccess (返回结果) {String} fieldToken 数据源Token
     * @apiSuccess (返回结果) {String} fieldToken 数据源token
     * @apiSuccess (返回结果) {String} compareRule 比较区间json
     * @apiSuccess (返回结果) {String} triggerRule 触发规则json
     * @apiSuccess (返回结果) {Int} workOrderID 工单ID
     * @apiSuccess (返回结果) {String} workOrderCode 工单编号
     * @apiSuccess (返回结果) {String} deviceToken 设备SN
     * @apiSuccess (返回结果) {String} deviceTypeName 设备型号（对应物联网产品名称）
     * @apiSuccess (返回结果) {String} regionArea 行政区划
     * @apiSuccess (返回结果) {String} ruleName 规则名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseWarn
     */
    @Permission(permissionName = "mdmbase:DescribeBaseWarn")
    @PostMapping(value = "/QueryWtTerminalWarnInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtTerminalWarnInfo(@Valid @RequestBody QueryWtWarnDetailParam param) {
        return tbWarnLogService.queryTerminalWarnDetail(param);
    }

    /**
     * @api {POST} /AddWarnLogBindWarnOrder 警报规则绑定工单
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtWarnInfoDetail
     * @apiDescription 警报规则绑定工单
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectID] 工程ID
     * @apiParam (请求参数) {Int} warnID 报警记录ID
     * @apiParam (请求参数) {Int} warnType 工单类型
     * @apiParam (请求参数) {String} warnOrderName 工单名称
     * @apiParam (请求参数) {Int} sourceType 工单来源类型
     * @apiParam (请求参数) {String} exValue json格式的配置信息
     * @apiParam (请求参数) {String} organization 所属组织
     * @apiParam (请求参数) {String} dispatcherName 派单人
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseWarn
     */
    @Permission(permissionName = "mdmbase:DescribeBaseWarn")
    @PostMapping(value = "/AddWarnLogBindWarnOrder", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWarnLogBindWarnOrder(@Valid @RequestBody AddWarnLogBindWarnOrderParam param) {
        tbWarnLogService.addWarnLogBindWarnOrder(param);
        return ResultWrapper.successWithNothing();
    }

}
