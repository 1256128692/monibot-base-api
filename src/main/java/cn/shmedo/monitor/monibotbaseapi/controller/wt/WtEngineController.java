package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnLevel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import cn.shmedo.monitor.monibotbaseapi.model.param.engine.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnActionService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnRuleService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnTriggerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WtEngineController {
    private final ITbWarnRuleService tbWarnRuleService;
    private final ITbWarnTriggerService tbWarnTriggerService;
    private final ITbWarnActionService tbWarnActionService;

    /**
     * @api {POST} /QueryWtEnginePage 查询规则引擎分页
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryEnginePage
     * @apiDescription 查询规则引擎分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [ruleType] 规则类型, 1:报警规则 2:视频规则 3:智能终端规则
     * @apiParam (请求参数) {Int} [productID] 产品ID
     * @apiParam (请求参数) {String} [videoTypeName] 视频设备型号
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID
     * @apiParam (请求参数) {String} [engineName] 规则名称,支持模糊查询
     * @apiParam (请求参数) {String} [queryCode] 关键字,报警规则:模糊搜索规则名称、报警名称、工程项目; 视频规则:
     * @apiParam (请求参数) {Boolean} [enable] 启用状态
     * @apiParam (请求参数) {Int} [orderType] 排序规则 1.按照创建时间降序排序(默认) 2.按照创建时间升序排序
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.engineID 引擎ID
     * @apiSuccess (返回结果) {String} currentPageData.engineName 引擎名称
     * @apiSuccess (返回结果) {Boolean} currentPageData.enable 启用状态
     * @apiSuccess (返回结果) {String} currentPageData.videoTypeName 视频设备型号名称
     * @apiSuccess (返回结果) {Int} currentPageData.productID 产品ID
     * @apiSuccess (返回结果) {String} [currentPageData.productName] 产品名称
     * @apiSuccess (返回结果) {String} [currentPageData.exValue] 应用范围json
     * @apiSuccess (返回结果) {DateTime} [currentPageData.createTime] 创建时间
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} [currentPageData.projectName] 工程名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} [currentPageData.monitorItemName] 监测项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} [currentPageData.monitorPointName] 监测点名称
     * @apiSuccess (返回结果) {Object[]} [currentPageData.dataList] 报警状态列表
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.warnID 报警状态ID
     * @apiSuccess (返回结果) {String} currentPageData.dataList.warnName 报警名称
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.warnLevel 报警等级
     * @apiSuccess (返回结果) {String} currentPageData.dataList.compareRule 比较区间json
     * @apiSuccess (返回结果) {Object[]} [currentPageData.dataList.action] 动作描述list
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.action.ID 动作ID
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.action.triggerID 触发报警ID
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.action.actionType 动作类型 1:生成通知 2.事件 3.短信 4.钉钉
     * @apiSuccess (返回结果) {String} currentPageData.dataList.action.actionTarget 动作目标json,推送的企业通讯录信息
     * @apiSuccess (返回结果) {String} currentPageData.dataList.action.desc 描述,一般是生成报警记录的解决方案说明(200字)
     * @apiSuccess (返回结果) {Boolean} currentPageData.dataList.action.enable 是否开启
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:ListBaseRuleEngine")
    @PostMapping(value = "/QueryWtEnginePage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtEnginePage(@Valid @RequestBody QueryWtEnginePageParam param) {
        return tbWarnRuleService.queryWtEnginePage(param);
    }

    /**
     * @api {POST} /QueryWtEngineDetail 查询规则引擎详情
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtEngineDetail
     * @apiDescription 查询规则引擎详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} engineID 引擎ID
     * @apiSuccess (返回结果) {Int} engineID 引擎ID
     * @apiSuccess (返回结果) {String} engineName 引擎名称
     * @apiSuccess (返回结果) {String} engineDesc 引擎简介
     * @apiSuccess (返回结果) {Boolean} enable 启用状态
     * @apiSuccess (返回结果) {String} [videoType] 视频设备型号
     * @apiSuccess (返回结果) {Int} [productID] 产品ID
     * @apiSuccess (返回结果) {String} [productName] 产品名称
     * @apiSuccess (返回结果) {Int} [projectID] 工程ID
     * @apiSuccess (返回结果) {String} [projectName] 工程名称
     * @apiSuccess (返回结果) {Int} [monitorItemID] 监测项目ID
     * @apiSuccess (返回结果) {String} [monitorItemName] 监测项目名称
     * @apiSuccess (返回结果) {String} [monitorItemAlias] 监测项目别称
     * @apiSuccess (返回结果) {Int} [monitorTypeID] 监测类型ID
     * @apiSuccess (返回结果) {String} [monitorTypeName] 监测类型名称
     * @apiSuccess (返回结果) {String} [monitorTypeAlias] 监测类型别称
     * @apiSuccess (返回结果) {Int} [monitorPointID] 监测点ID
     * @apiSuccess (返回结果) {String} [monitorPointName] 监测点名称
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {Int} createUserID 创建人ID
     * @apiSuccess (返回结果) {Object[]} [dataList] 报警状态列表
     * @apiSuccess (返回结果) {Int} dataList.warnID 报警状态ID
     * @apiSuccess (返回结果) {String} dataList.warnName 报警名称
     * @apiSuccess (返回结果) {Int} dataList.warnLevel 报警等级/报警类型枚举值
     * @apiSuccess (返回结果) {String} [dataList.warnTypeName] 报警类型描述
     * @apiSuccess (返回结果) {String} dataList.fieldToken 源数据Token
     * @apiSuccess (返回结果) {String} dataList.fieldName 源数据名称
     * @apiSuccess (返回结果) {String} [dataList.compareRule] 比较区间json
     * @apiSuccess (返回结果) {String} [dataList.triggerRule] 触发规则json
     * @apiSuccess (返回结果) {Object[]} [dataList.action] 动作描述list
     * @apiSuccess (返回结果) {Int} dataList.action.ID 动作ID
     * @apiSuccess (返回结果) {Int} dataList.action.triggerID 触发报警ID
     * @apiSuccess (返回结果) {Int} dataList.action.actionType 动作类型 1:生成通知 2.事件 3.短信 4.钉钉
     * @apiSuccess (返回结果) {String} dataList.action.actionTarget 动作目标json,推送的企业通讯录信息
     * @apiSuccess (返回结果) {String} dataList.action.desc 描述,一般是生成报警记录的解决方案说明(200字)
     * @apiSuccess (返回结果) {Boolean} dataList.action.enable 是否开启
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:DescribeBaseRuleEngine")
    @PostMapping(value = "/QueryWtEngineDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtEngineDetail(@Valid @RequestBody QueryWtEngineDetailParam param) {
        return tbWarnRuleService.queryWtEngineDetail(param);
    }

    /**
     * @api {POST} /AddWtEngine 新增规则引擎
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName AddWtEngine
     * @apiDescription 新增规则引擎
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {String} engineName 引擎名称(30字)
     * @apiParam (请求参数) {String} engineDesc 引擎简介(200字)
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiParam (请求参数) {Int} monitorPointID 监测点ID
     * @apiSuccess (响应结果) Int engineID 引擎ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:AddBaseRuleEngine")
    @PostMapping(value = "/AddWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWtEngine(@Valid @RequestBody AddWtEngineParam param) {
        // finish create while have been charged and goto next step,the last step should be regarded as modify.
        return tbWarnRuleService.addWtEngine(param, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
    }

    /**
     * @api {POST} /UpdateWtEngine 编辑规则引擎
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName UpdateWtEngine
     * @apiDescription 编辑规则引擎
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} engineID 引擎ID
     * @apiParam (请求参数) {String} [engineName] 引擎名称(30字)
     * @apiParam (请求参数) {String} [engineDesc] 引擎简介(200字)
     * @apiParam (请求参数) {String} [productID] 设备型号 对应产品ID或视频类型
     * @apiParam (请求参数) {String} [DeviceCSV] "all" 或者设备ID，视频SN的CSV
     * @apiParam (请求参数) {Int} [projectID] 项目ID
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {Int} [monitorPointID] 监测点位ID
     * @apiParam (请求参数) {Object[]} [dataList] 报警状态列表
     * @apiParam (请求参数) {Int} [dataList.warnID] 报警状态ID,若没有视为新增
     * @apiParam (请求参数) {String} dataList.warnName 报警名称
     * @apiParam (请求参数) {Int} dataList.warnLevel 报警等级/报警类型枚举值
     * @apiParam (请求参数) {Int} dataList.fieldToken 源数据Token
     * @apiParam (请求参数) {String} [dataList.compareRule] 比较区间json
     * @apiParam (请求参数) {String} [dataList.triggerRule] 触发规则json
     * @apiParam (请求参数) {Object[]} [dataList.action] 动作描述list
     * @apiParam (请求参数) {Int} [dataList.action.ID] 动作ID,若没有视为新增
     * @apiParam (请求参数) {Int} dataList.action.actionType 动作类型 1:生成通知 2.事件 3.短信 4.钉钉
     * @apiParam (请求参数) {String} dataList.action.actionTarget 动作目标json,推送的企业通讯录信息
     * @apiParam (请求参数) {String} dataList.action.desc 描述,一般是生成报警记录的解决方案说明(200字)
     * @apiParam (请求参数) {Boolean} dataList.action.enable 是否开启
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseRuleEngine
     */

    @Permission(permissionName = "mdmbase:UpdateBaseRuleEngine")
    @PostMapping(value = "/UpdateWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWtEngine(@Valid @RequestBody UpdateWtEngineParam param) {
        tbWarnRuleService.updateWtEngine(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWtEngineEnable 规则引擎批量启用、停用
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName UpdateWtEngineEnable
     * @apiDescription 规则引擎批量启用、停用
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Boolean} enable 启用状态
     * @apiParam (请求参数) {Int[]} engineIDList 引擎ID list
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:UpdateBaseRuleEngine")
    @PostMapping(value = "/UpdateWtEngineEnable", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWtEngineEnable(@Valid @RequestBody BatchUpdateWtEngineEnableParam param) {
        tbWarnRuleService.updateWtEngineEnable(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteWtEngine 规则引擎批量删除
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName DeleteWtEngine
     * @apiDescription 规则引擎批量删除
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} engineIDList 引擎ID list
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:DeleteBaseRuleEngine")
    @PostMapping(value = "/DeleteWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteWtEngine(@Valid @RequestBody BatchDeleteWtEngineParam param) {
        tbWarnRuleService.deleteWtEngine(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteWtWarnStatus 删除报警状态
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName DeleteWtWarnStatus
     * @apiDescription 删除报警状态
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} warnIDList 删除的报警状态ID list
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:UpdateBaseRuleEngine")
    @PostMapping(value = "/DeleteWtWarnStatus")
    public Object deleteWtWarnStatus(@Valid @RequestBody DeleteWtWarnStatusParam param) {
        tbWarnTriggerService.deleteWarnStatusByIDList(param.getWarnIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteWtAction 删除动作
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName DeleteWtWarnStatus
     * @apiDescription 删除动作
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} actionIDList 删除的动作ID list
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:UpdateBaseRuleEngine")
    @PostMapping(value = "/DeleteWtAction", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteWtAction(@Valid @RequestBody DeleteWtActionParam param) {
        tbWarnActionService.removeBatchByIds(param.getActionIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /WarnTypeList 报警类型枚举
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName WarnTypeList
     * @apiDescription 报警类型枚举
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} ruleType 规则类型, 1:报警规则 2:视频规则 3:智能终端规则
     * @apiSuccess (返回结果) {Object[]} dataList 报警类型枚举
     * @apiSuccess (返回结果) {Int} dataList.warnLevel 报警类型枚举值
     * @apiSuccess (返回结果) {String} dataList.warnTypeName 报警类型名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:DescribeBaseRuleEngine")
    @PostMapping(value = "/WarnTypeList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object warnTypeList(@Valid @RequestBody WarnTypeListParam param) {
        return WarnLevel.getEnum(WarnType.formCode(param.getRuleType()));
    }

    /**
     * @api {POST} /AddWtDeviceWarnRule 新增预警规则
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName AddWtDeviceWarnRule
     * @apiDescription 新增预警规则
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} [ruleType] 123 报警规则，视频规则 ， 智能终端规则, 不设置默认为1
     * @apiParam (请求参数) {Int} monitorType 监测类型，可为-1
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID，可为-1
     * @apiParam (请求参数) {Int} [pointID] 监测点ID
     * @apiParam (请求参数) {String} name 规则名称(100)
     * @apiParam (请求参数) {Boolean} enable 是否启用
     * @apiParam (请求参数) {String} [desc] 描述 1000
     * @apiParam (请求参数) {String} [exValue] 额外属性，json字符串 1000
     * @apiParam (请求参数) {String} [productID] 产品ID（设备型号对应ID）,该项为字符串或数字字符串
     * @apiParam (请求参数) {String} [deviceCSV] 该项为"all"或者设备ID的CSV字符串
     * @apiSuccess (返回结果) {Int} ruleID 规则ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:UpdateBaseRuleEngine")
    @LogParam(moduleName = "警报规则引擎模块", operationName = "新增预警规则", operationProperty = OperationProperty.ADD)
    @RequestMapping(value = "AddWtDeviceWarnRule", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWtDeviceWarnRule(@Valid @RequestBody AddWtDeviceWarnRuleParam pa) {
        return tbWarnRuleService.addWtDeviceWarnRule(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
    }

    /**
     * @api {POST} /MutateWarnRuleDevice 修改规则关联设备
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName MutateWarnRuleDevice
     * @apiDescription 修改规则关联设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} ruleID  规则ID
     * @apiParam (请求参数) {String} sign 符号，支持 + - 两种
     * @apiParam (请求参数) {String} deviceCSV 该项为"all"或者设备ID的CSV字符串
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateBaseRuleEngine
     */
    @Permission(permissionName = "mdmbase:UpdateBaseRuleEngine")
    @LogParam(moduleName = "警报规则引擎模块", operationName = "修改规则关联设备", operationProperty = OperationProperty.UPDATE)
    @RequestMapping(value = "MutateWarnRuleDevice", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object mutateWarnRuleDevice(@Valid @RequestBody MutateWarnRuleDeviceParam pa) {
        tbWarnRuleService.mutateWarnRuleDevice(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }
}
