package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WtEngineController {
    /**
     * @api {POST} /QueryWtEnginePage 查询规则引擎分页
     * @apiVersion 1.0.0
     * @apiGroup 水利平台在线监测管理模块
     * @apiName QueryEnginePage
     * @apiDescription 查询规则引擎分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [engineName] 规则名称,支持模糊查询
     * @apiParam (请求参数) {Boolean} [enable] 启用状态
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
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 测点名称
     * @apiSuccess (返回结果) {Object[]} currentPageData.dataList 报警状态列表
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.alarmID 报警状态ID
     * @apiSuccess (返回结果) {String} currentPageData.dataList.alarmName 报警名称
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.alarmLevel 报警等级
     * @apiSuccess (返回结果) {String} currentPageData.dataList.action 动作描述json
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @Permission(permissionName = "")
    @PostMapping(value = "/queryWtEnginePage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtEnginePage(@Valid @RequestBody Object param) {
        //TODO 规则列表
        return null;
    }

    /**
     * @api {POST} /QueryWtEngineDetail 查询规则引擎详情
     * @apiVersion 1.0.0
     * @apiGroup 水利平台在线监测管理模块
     * @apiName QueryWtEngineDetail
     * @apiDescription 查询规则引擎详情
     * @apiParam (请求参数) {Int} engineID 引擎ID
     * @apiSuccess (返回结果) {String} engineName 引擎名称
     * @apiSuccess (返回结果) {String} ruleIntroduce 引擎简介
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} monitorPointID 测点ID
     * @apiSuccess (返回结果) {String} monitorPointName 测点名称
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {Int} createUserID 创建人ID
     * @apiSuccess (返回结果) {String} createUserName 创建人名称
     * @apiSuccess (返回结果) {Object[]} dataList 报警状态列表
     * @apiSuccess (返回结果) {Int} dataList.alarmID 报警状态ID
     * @apiSuccess (返回结果) {String} dataList.alarmName 报警名称
     * @apiSuccess (返回结果) {Int} dataList.alarmLevel 报警等级
     * @apiSuccess (返回结果) {String} dataList.metadata 源数据
     * @apiSuccess (返回结果) {String} dataList.desc 规则描述json
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @Permission(permissionName = "")
    @PostMapping(value = "/queryWtEngineDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtEngineDetail(@Valid @RequestBody Object param) {
        //TODO 查询规则详情
        return null;
    }

    /**
     * @api {POST} /QueryWtAlarmStatus 查询报警状态详情
     * @apiVersion 1.0.0
     * @apiGroup 水利平台在线监测管理模块
     * @apiName QueryWtAlarmStatus
     * @apiDescription 查询报警状态详情
     * @apiParam (请求参数) {Int} alarmID 报警状态ID
     * @apiSuccess (返回结果) {String} dataList.alarmName 报警名称
     * @apiSuccess (返回结果) {Int} dataList.alarmLevel 报警等级
     * @apiSuccess (返回结果) {String} dataList.metadata 源数据
     * @apiSuccess (返回结果) {String} dataList.desc 规则描述json
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @Permission(permissionName = "")
    @PostMapping(value = "/queryWtAlarmStatus", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtAlarmStatus(@Valid @RequestBody Object param) {
        //TODO 报警状态详情
        return null;
    }

    /**
     * @api {POST} /CreateWtEngine 新增规则引擎
     * @apiVersion 1.0.0
     * @apiGroup 水利平台在线监测管理模块
     * @apiName CreateWtEngine
     * @apiDescription 新增规则引擎
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} engineName 引擎名称
     * @apiParam (请求参数) {Boolean} enable 启用状态
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiParam (请求参数) {Int} monitorPointID 测点ID
     * @apiParam (请求参数) {Object[]} dataList 报警状态List
     * @apiParam (请求参数) {String} dataList.alarmName 报警名称
     * @apiParam (请求参数) {Int} dataList.alarmLevel 报警等级
     * @apiParam (请求参数) {String} dataList.metadata 源数据
     * @apiParam (请求参数) {String} dataList.desc 引擎描述json
     * @apiParam (请求参数) {String} dataList.action 动作描述json
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @Permission(permissionName = "")
    @PostMapping(value = "/createWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object createWtEngine(@Valid @RequestBody Object param) {
        //TODO 新增规则引擎
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWtEngine 编辑规则引擎
     * @apiVersion 1.0.0
     * @apiGroup 水利在线监测管理模块
     * @apiName UpdateWtEngine
     * @apiDescription 编辑规则引擎
     * @apiParam (请求参数) {Object[]} dataList 引擎list
     * @apiParam (请求参数) {Int} dataList.engineID 引擎ID
     * @apiParam (请求参数) {Boolean} [dataList.enable] 启用状态
     * @apiParam (请求参数) {String} [dataList.engineName] 引擎名称
     * @apiParam (请求参数) {String} [dataList.engineIntroduce] 引擎介绍
     * @apiParam (请求参数) {Object[]} [dataList.alarmStatusList] 报警状态list
     * @apiParam (请求参数) {Int} [dataList.alarmStatusList.alarmID] 报警状态ID
     * @apiParam (请求参数) {String} [dataList.alarmStatusList.alarmName] 报警名称
     * @apiParam (请求参数) {Int} [dataList.alarmStatusList.alarmLevel] 报警等级
     * @apiParam (请求参数) {String} [dataList.alarmStatusList.metadata] 源数据
     * @apiParam (请求参数) {String} [dataList.alarmStatusList.desc] 规则描述json
     * @apiParam (请求参数) {String} [dataList.alarmStatusList.action] 动作描述json
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @Permission(permissionName = "")
    @PostMapping(value = "/updateWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWtEngine(@Valid @RequestBody Object param) {
        //TODO 批量停用、启用、编辑、删除规则
        //TODO 遍历入参中的所有规则,如果各个规则json中仅有id,则将这些规则及其对应的报警模板都删除
        //TODO 如果engineName和engineIntroduce不为空,更新这些规则的名称跟简介;如果enable启用状态不为null,更新这些规则的启用状态
        //TODO 如果alarmStatusList不为null,遍历->
        //TODO      如果json中缺失id,将这些报警模板添加到新增List;
        //TODO      如果json中仅有id,将这些报警模板添加到删除List;
        //TODO      否则认为是更新的报警模板,将这些报警模板添加到更新List;
        //TODO 处理报警模板
        return ResultWrapper.successWithNothing();
    }
}
