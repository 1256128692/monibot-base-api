package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.hutool.core.lang.Dict;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO:不要批量修改，添加批量删除API，统一项目权限(projectID校验)
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WtEngineController {
    /**
     * @api {POST} /QueryWtEnginePage 查询规则引擎分页
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryEnginePage
     * @apiDescription 查询规则引擎分页
     * @apiParam (请求参数) {Int} [projectID] 工程项目ID //TODO 必要字段、且可能通过该字段auth
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
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Object[]} currentPageData.dataList 报警状态列表
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.alarmID 报警状态ID
     * @apiSuccess (返回结果) {String} currentPageData.dataList.alarmName 报警名称
     * @apiSuccess (返回结果) {Int} currentPageData.dataList.alarmLevel 报警等级
     * @apiSuccess (返回结果) {String} currentPageData.dataList.action 动作描述json
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseWarnRule
     */
//    @Permission(permissionName = "mdmbase:ListBaseWarnRule")
    @PostMapping(value = "/queryWtEnginePage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtEnginePage(@Valid @RequestBody Object param) {
        //TODO 规则列表
        return null;
    }

    /**
     * @api {POST} /QueryWtEngineDetail 查询规则引擎详情
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName QueryWtEngineDetail
     * @apiDescription 查询规则引擎详情
     * @apiParam (请求参数) {Int} projectID 工程项目ID //TODO auth
     * @apiParam (请求参数) {Int} engineID 引擎ID
     * @apiSuccess (返回结果) {Int} engineID 引擎ID
     * @apiSuccess (返回结果) {String} engineName 引擎名称
     * @apiSuccess (返回结果) {String} ruleDesc 引擎简介
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测点名称
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {Int} createUserID 创建人ID
     * @apiSuccess (返回结果) {String} createUserName 创建人名称
     * @apiSuccess (返回结果) {Object[]} dataList 报警状态列表
     * @apiSuccess (返回结果) {Int} dataList.alarmID 报警状态ID
     * @apiSuccess (返回结果) {String} dataList.alarmName 报警名称
     * @apiSuccess (返回结果) {Int} dataList.alarmLevel 报警等级
     * @apiSuccess (返回结果) {String} dataList.metadata 源数据
     * @apiSuccess (返回结果) {String} dataList.compareRule 比较区间json
     * @apiSuccess (返回结果) {String} dataList.triggerRule 触发规则json
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseWarnRule
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseWarnRule")
    @PostMapping(value = "/queryWtEngineDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtEngineDetail(@Valid @RequestBody Object param) {
        //TODO 查询规则详情
        return null;
    }

    /**
     * @api {POST} /AddWtEngine 新增规则引擎
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName AddWtEngine
     * @apiDescription 新增规则引擎
     * @apiParam (请求参数) {Int} projectID 工程项目ID //TODO 必要字段、且可能通过该字段auth
     * @apiParam (请求参数) {String} engineName 引擎名称
     * @apiParam (请求参数) {String} engineDesc 引擎简介
     * @apiParam (请求参数) {Int} monitorItemID 监测项目ID
     * @apiParam (请求参数) {Int} monitorPointID 监测点ID
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.ID 引擎ID
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseWarnRule
     */
//    @Permission(permissionName = "mdmbase:UpdateBaseWarnRule")
    @PostMapping(value = "/addWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWtEngine(@Valid @RequestBody Object param) {
        //TODO finish create while have been charged and goto next step,the last step should be regarded as modify.
        return Dict.of("id", -1);
    }

    /**
     * @api {POST} /UpdateWtEngine 编辑规则引擎
     * @apiVersion 1.0.0
     * @apiGroup 警报规则引擎模块
     * @apiName UpdateWtEngine
     * @apiDescription 编辑规则引擎
     * @apiParam (请求参数) {Int} projectID 工程项目ID //TODO auth
     * @apiParam (请求参数) {Int} engineID 引擎ID
     * @apiParam (请求参数) {String} [engineName] 引擎名称
     * @apiParam (请求参数) {String} [engineDesc] 引擎简介
     * @apiParam (请求参数) {Object[]} [datalist] 报警状态
     * @apiParam (请求参数) {Int} [datalist.alarmID] 报警状态ID,若没有视为新增
     * @apiParam (请求参数) {String} [datalist.alarmLevel] 报警等级
     * @apiParam (请求参数) {String} [datalist.metadata] 源数据
     * @apiParam (请求参数) {String} [datalist.compareRule] 比较区间json
     * @apiParam (请求参数) {String} [datalist.triggerRule] 触发规则json
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseWarnRule
     */
//    @Permission(permissionName = "mdmbase:UpdateBaseWarnRule")
    @Deprecated
    @PostMapping(value = "/updateWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWtEngine(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateWtEngineEnable 规则引擎批量启用、停用
     * @apiVersion 1.0.0
     * @apiGroup 水利在线监测管理模块
     * @apiName UpdateWtEngineEnable
     * @apiDescription 规则引擎批量启用、停用
     * @apiParam (请求参数) {Int} projectID 工程项目ID //TODO auth
     * @apiParam (请求参数) {Boolean} enable 启用状态
     * @apiParam (请求参数) {Object[]} dataList 引擎list
     * @apiParam (请求参数) {Int} dataList.ID 需要操作的引擎id
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
    @PostMapping(value = "/updateWtEngineEnable", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWtEngineEnable(@Valid @RequestBody Object param) {
        //TODO ps.ensure all engines owned alarm status.
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteWtEngine 规则引擎批量删除
     * @apiVersion 1.0.0
     * @apiGroup 水利在线监测管理模块
     * @apiName DeleteWtEngine
     * @apiDescription 规则引擎批量删除
     * @apiParam (请求参数) {Int} projectID 工程项目ID //TODO auth
     * @apiParam (请求参数) {Object[]} dataList 引擎list
     * @apiParam (请求参数) {Int} dataList.ID 需要操作的引擎id
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
    @PostMapping(value = "/deleteWtEngine", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteWtEngine(@Valid @RequestBody Object param) {
        //TODO clear engines and owned alarm status.
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteWtAlarmStatus 删除报警状态
     * @apiVersion 1.0.0
     * @apiGroup 水利在线监测管理模块
     * @apiName DeleteWtAlarmStatus
     * @apiDescription 删除报警状态
     * @apiParam (请求参数) {Int}  projectID 工程项目ID //TODO auth
     * @apiParam (请求参数) {Int} alarmID 删除的报警状态ID
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @PostMapping(value = "/deleteWtAlarmStatus")
    public Object deleteWtAlarmStatus(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }
}
