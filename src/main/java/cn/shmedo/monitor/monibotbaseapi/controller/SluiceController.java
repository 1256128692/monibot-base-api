package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.sluice.*;
import cn.shmedo.monitor.monibotbaseapi.service.SluiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 闸泵控制
 *
 * @author Chengfs on 2023/11/16
 */
@RestController
@RequiredArgsConstructor
public class SluiceController {

    private final SluiceService service;

    /**
     * @api {POST} /QuerySluiceControlRecordPage 分页查询水闸控制记录
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName QuerySluiceControlRecordPage
     * @apiDescription 分页查询水闸控制记录 (内部调用接口，不允许用户调用)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [keyword] 水闸名称/渠道名称 模糊查询
     * @apiParam (请求参数) {Int} [controlType] 控制类型 (1远程控制 2手动控制 3现地控制)
     * @apiParam (请求参数) {DateTime} [begin] 开始时间 (yyyy-MM-dd HH:mm:ss)
     * @apiParam (请求参数) {DateTime} [end] 结束时间 (yyyy-MM-dd HH:mm:ss)
     * @apiParam (请求参数) {Int} currentPage 当前页(>=1)
     * @apiParam (请求参数) {Int} pageSize 页大小(1-100)
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 记录id
     * @apiSuccess (返回结果) {Int} currentPageData.companyID 公司ID
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 项目id
     * @apiSuccess (返回结果) {String} currentPageData.projectName 项目名称 (水闸名称)
     * @apiSuccess (返回结果) {Int} currentPageData.gateID 闸门id (监测传感器id)
     * @apiSuccess (返回结果) {String} currentPageData.gateName 闸门名称 (监测传感器别名)
     * @apiSuccess (返回结果) {String} currentPageData.canal 所属渠道
     * @apiSuccess (返回结果) {String} currentPageData.sluiceType 水闸类型 (进水闸、退水闸、船闸、节制闸、挡潮闸、其他)
     * @apiSuccess (返回结果) {String} currentPageData.mmUnit 管理单位
     * @apiSuccess (返回结果) {Int} currentPageData.controlType 控制类型 1远程控制 2手动控制 3现地控制
     * @apiSuccess (返回结果) {Int} [currentPageData.actionType] 操作类型 0恒定闸位(开合度)、1恒定水位、2总量控制(累计流量)、3时段控制、4时长控制、5远程手动控制（上、下、停）、6自动校准 7恒定流量(瞬时流量)
     * @apiSuccess (返回结果) {Int} currentPageData.runningState 电机运行状态 0上、1下、2停
     * @apiSuccess (返回结果) {DateTime} currentPageData.operationTime 操作时间 (yyyy-MM-dd HH:mm:ss)
     * @apiSuccess (返回结果) {Int} currentPageData.operationUserID 操作人id
     * @apiSuccess (返回结果) {String} currentPageData.operationUser 操作人名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:BaseSluiceControlRecord (不允许用户调用)
     */
    @Permission(permissionName = "mdmbase:BaseSluiceControlRecord", allowApplication = true, allowUser = false)
    @PostMapping(value = "/QuerySluiceControlRecordPage", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object controlRecordPage(@Validated @RequestBody BaseSluiceQuery request) {
        return service.controlRecordPage(request);
    }

    /**
     * @api {POST} /QuerySluiceControlRecord 查询水闸控制记录详情
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName QuerySluiceControlRecord
     * @apiDescription 查询水闸控制记录详情 (内部调用接口，不允许用户调用)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Long} id 记录id
     * @apiSuccess (返回结果) {Int} id 记录id
     * @apiSuccess (返回结果) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Int} projectID 项目id
     * @apiSuccess (返回结果) {String} projectName 项目名称 (水闸名称)
     * @apiSuccess (返回结果) {Int} gateID 闸门id (监测传感器id)
     * @apiSuccess (返回结果) {String} gateName 闸门名称 (监测传感器别名)
     * @apiSuccess (返回结果) {String} canal 所属渠道
     * @apiSuccess (返回结果) {String} sluiceType 水闸类型 (进水闸、退水闸、船闸、节制闸、挡潮闸、其他)
     * @apiSuccess (返回结果) {String} mmUnit 管理单位
     * @apiSuccess (返回结果) {Int} controlType 控制类型 1远程控制 2手动控制 3现地控制
     * @apiSuccess (返回结果) {Int} [actionType] 操作类型 0恒定闸位(开合度)、1恒定水位、2总量控制(累计流量)、3时段控制、4时长控制、5远程手动控制（上、下、停）、6自动校准 7恒定流量(瞬时流量)
     * @apiSuccess (返回结果) {Int} runningSta 电机运行状态 0上、1下、2停
     * @apiSuccess (返回结果) {DateTime} operationTime 操作时间 (yyyy-MM-dd HH:mm:ss)
     * @apiSuccess (返回结果) {Int} operationUserID 操作人id
     * @apiSuccess (返回结果) {String} operationUser 操作人名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:BaseSluiceControlRecord (不允许用户调用)
     */
    @Permission(permissionName = "mdmbase:BaseSluiceControlRecord", allowApplication = true, allowUser = false)
    @PostMapping(value = "/QuerySluiceControlRecord", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object singleSluiceControlRecord(@Validated @RequestBody SingleControlRecordRequest request) {
        return service.singleControlRecord(request);
    }

    /**
     * @api {POST} /AddSluiceControlRecord 新增水闸控制记录
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName AddSluiceControlRecord
     * @apiDescription 新增水闸控制记录 (内部调用接口，不允许用户调用)
     * @apiParam (请求体) {Int} sid 传感器id
     * @apiParam (请求体) {DateTime} time 时间 (yyyy-MM-dd HH:mm:ss)
     * @apiParam (请求体) {Int} [runningSta] 运行状态
     * @apiParam (请求体) {Int} software   软件模式
     * @apiParam (请求体) {Int} hardware  硬件模式
     * @apiParam (请求体) {String} [msg]   消息
     * @apiParam (请求体) {String} [logLevel]  日志级别
     * @apiSampleRequest off
     * @apiSuccess (响应结果) {Int[]} data id集合
     * @apiPermission 项目权限 mdmbase:BaseSluiceControlRecord (不允许用户调用)
     */
    @Permission(permissionName = "mdmbase:BaseSluiceControlRecord", allowApplication = true, allowUser = false)
    @PostMapping(value = "/AddSluiceControlRecord", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object addSluiceControlRecord(@Validated @RequestBody AddControlRecordRequest request) {
        return service.addSluiceControlRecord(request);
    }

    /**
     * @api {POST} /QuerySluicePage 分页查询水闸列表
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName QuerySluicePage
     * @apiDescription 分页查询水闸列表
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} [keyword] 水闸名称/渠道名称 模糊查询
     * @apiParam (请求参数) {String} [sluiceType] 水闸类型 (进水闸、退水闸、船闸、节制闸、挡潮闸、其他)
     * @apiParam (请求参数) {Int} [controlType] 控制类型 1远程控制 2手动控制 3现地控制
     * @apiParam (请求参数) {String} [manageUnit] 管理单位
     * @apiParam (请求参数) {Int} currentPage 当前页(>=1)
     * @apiParam (请求参数) {Int} pageSize 页大小(1-100)
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 项目id
     * @apiSuccess (返回结果) {String} currentPageData.projectName 水闸名称
     * @apiSuccess (返回结果) {String} currentPageData.canal 所属渠道
     * @apiSuccess (返回结果) {Int} currentPageData.sluiceType 水闸类型 (进水闸、退水闸、船闸、节制闸、挡潮闸、其他)
     * @apiSuccess (返回结果) {String} currentPageData.manageUnit 管理单位
     * @apiSuccess (返回结果) {Int} currentPageData.sluiceHoleNum 闸孔数量
     * @apiSuccess (返回结果) {Int} currentPageData.openStatus 启闭状态 0.关闭 1.开启 (任一闸门开启该水闸即为开启)
     * @apiSuccess (返回结果) {Object[]} currentPageData.gates 闸门信息
     * @apiSuccess (返回结果) {Int} currentPageData.gates.id 闸门id (唯一, 对应监测传感器id)
     * @apiSuccess (返回结果) {Int} currentPageData.gates.controlType 控制类型 1远程控制 2手动控制 3现地控制
     * @apiSuccess (返回结果) {Int} currentPageData.gates.openStatus 启闭状态 0.关闭 1.开启
     * @apiSuccess (返回结果) {Int} currentPageData.gates.alias 闸门名称(传感器别名)
     * @apiSuccess (返回结果) {Double} currentPageData.frontWaterLevel 闸前水位(m)
     * @apiSuccess (返回结果) {Double} currentPageData.backWaterLevel 闸后水位(m)
     * @apiSuccess (返回结果) {Double} currentPageData.flowRate 瞬时流量(m³/s)
     * @apiSuccess (返回结果) {Object[]} currentPageData.waterData 水情数据 (用于查询采集数据)
     * @apiSuccess (返回结果) {Int} currentPageData.waterData.monitorType 水情数据监测类型
     * @apiSuccess (返回结果) {Int} currentPageData.waterData.monitorItemID 水情数据监测项id
     * @apiSuccess (返回结果) {Int[]} currentPageData.waterData.monitorPointIDList 水情数据监测点id列表
     * @apiSuccess (返回结果) {Int} currentPageData.videoMonitorGroupID 视频监测组id (用于查询视频监控)
     * @apiSuccess (返回结果) {DateTime} currentPageData.lastCollectTime 最后采集时间(yyyy-MM-dd HH:mm:ss)
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListSluice
     */
    @Permission(permissionName = "mdmbase:ListSluice")
    @PostMapping(value = "/QuerySluicePage", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sluicePage(@Validated @RequestBody QuerySluicePageRequest request) {
        return service.sluicePage(request);
    }

    /**
     * @api {POST} /SluiceControl 水闸远程控制
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName SluiceControl
     * @apiDescription 闸门远程控制
     * @apiParam (请求参数) {Int} projectID 项目(水闸) id
     * @apiParam (请求参数) {Int} [gateID] 闸门id (为空时控制所有闸门, 仅允许 1停止/5开启/6关闭)
     * @apiParam (请求参数) {Int} actionKind 操作种类 (1停止 2上升 3下降 4自动控制 5开启 6关闭)
     * @apiParam (请求参数) {Int} [actionType] 操作类型 (仅actionKind=4有效: 1恒定水位 2恒定流量 3恒定闸位 4时长控制 5时段控制 6总量控制)
     * @apiParam (请求参数) {Object} [target] 控制目标 (仅actionKind=4有效, 不同actionType下具有不同结构)
     * @apiParam (请求参数) {Double} [target.waterLevel] 目标水位 (1恒定水位)
     * @apiParam (请求参数) {Double} [target.flowRate] 目标流量 (2恒定流量)
     * @apiParam (请求参数) {Double} [target.gateDegree] 目标开度 (3恒定闸位)
     * @apiParam (请求参数) {DateTime} [target.openTime] 开闸时间 (4时长控制)
     * @apiParam (请求参数) {Long} [target.duration] 运行时长(分钟) (4时长控制)
     * @apiParam (请求参数) {DateTime} [target.beginTime] 开始时间 (5时段控制)
     * @apiParam (请求参数) {DateTime} [target.endTime] 结束时间 (5时段控制)
     * @apiParam (请求参数) {Double} [target.totalFlow] 目标累计流量 (6总量控制)
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ControlSluice
     */
    @Permission(permissionName = "mdmbase:ControlSluice")
    @PostMapping(value = "/SluiceControl", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sluiceControl(@Validated @RequestBody SluiceControlRequest request) {
        service.sluiceControl(request);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QuerySluiceDetail 水闸详情
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName QuerySluiceDetail
     * @apiDescription 闸门详情
     * @apiParam (请求参数) {Int} projectID 项目(水闸) id
     * @apiSuccess (返回结果) {Object[]} gates 闸门列表
     * @apiSuccess (返回结果) {Int} gates.id 闸门id (唯一, 对应监测传感器id)
     * @apiSuccess (返回结果) {String} gates.alias 闸门名称 (监测传感器别名)
     * @apiSuccess (返回结果) {Int} gates.openStatus 启闭状态 0.关闭 1.开启
     * @apiSuccess (返回结果) {Double} gates.openDegree 闸门开度(m)
     * @apiSuccess (返回结果) {Double} gates.powerCurrent 电源电流 (A)
     * @apiSuccess (返回结果) {Double} gates.powerVoltage 电源电压 (V)
     * @apiSuccess (返回结果) {Int} gates.controlType 控制类型 1远程控制 2手动控制 3现地控制
     * @apiSuccess (返回结果) {Double} gates.maxOpenDegree 闸门最大开度
     * @apiSuccess (返回结果) {Int} gates.runningState 电机运行状态 0上、1下、2停
     * @apiSuccess (返回结果) {Int} gates.limitSwSta 限位开关状态（0：上下限位均未触发；1：上限位触发；2：下限位触发；3：上下均触发）
     * @apiSuccess (返回结果) {Double} maxFlowRate 最大过闸流量
     * @apiSuccess (返回结果) {Double} maxBackWaterLevel 最大闸后水位
     * @apiSuccess (返回结果) {Double} frontWaterLevel 闸前水位(m)
     * @apiSuccess (返回结果) {Double} backWaterLevel 闸后水位(m)
     * @apiSuccess (返回结果) {Double} flowRate 瞬时流量(m³/s)
     * @apiSuccess (返回结果) {Double} flowTotal 累计流量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeSluice
     */
    @Permission(permissionName = "mdmbase:DescribeSluice")
    @PostMapping(value = "/QuerySluiceDetail", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sluiceDetail(@Validated @RequestBody SingleSluiceRequest request) {
        return service.sluiceSingle(request);
    }

    /**
     * @api {POST} /ListSluice 水闸列表
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName ListSluice
     * @apiDescription 水闸列表 (下拉)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectID] 项目(水闸) id
     * @apiParam (请求参数) {Int} [controlType] 控制类型 (1远程控制 2手动控制 3现地控制)
     * @apiSuccess (返回结果) {Object[]} data 水闸列表
     * @apiSuccess (返回结果) {Int} data.projectID 项目(水闸) id
     * @apiSuccess (返回结果) {String} data.projectName 项目(水闸) 名称
     * @apiSuccess (返回结果) {String} data.canal 所属渠道
     * @apiSuccess (返回结果) {String} data.sluiceType 水闸类型 (进水闸、退水闸、船闸、节制闸、挡潮闸、其他)
     * @apiSuccess (返回结果) {String} data.manageUnit 管理单位
     * @apiSuccess (返回结果) {Int} data.sluiceHoleNum 闸孔数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListSluice
     */
    @Permission(permissionName = "mdmbase:ListSluice")
    @PostMapping(value = "/ListSluice", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object listSluiceSimple(@Validated @RequestBody ListSluiceRequest request) {
        return service.listSluiceSimple(request);
    }

    /**
     * @api {POST} /ListSluiceManageUnit 水闸管理单位列表
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName ListSluiceManageUnit
     * @apiDescription 水闸管理单位列表 (下拉)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectID] 项目(水闸) id
     * @apiSuccess (返回结果) {String[]} data 管理单位列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeSluice
     */
    @Permission(permissionName = "mdmbase:DescribeSluice")
    @PostMapping(value = "/ListSluiceManageUnit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object listSluiceManageUnit(@Validated @RequestBody ListSluiceManageUnitRequest request) {
        return service.listSluiceManageUnit(request);
    }

    /**
     * @api {POST} /ListSluiceGate 水闸闸门列表
     * @apiVersion 1.0.0
     * @apiGroup 闸泵控制模块
     * @apiName ListSluiceGate
     * @apiDescription 水闸闸门列表 (下拉)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} [projectID] 项目(水闸) id
     * @apiParam (请求参数) {Int} [gateID] 传感器(闸门) id
     * @apiSuccess (返回结果) {Object[]} data 闸门列表
     * @apiSuccess (返回结果) {Int} data.id 监测传感器(闸门) id
     * @apiSuccess (返回结果) {String} data.alias 闸门名称 (监测传感器别名)
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeSluice
     */
    @Permission(permissionName = "mdmbase:DescribeSluice")
    @PostMapping(value = "/ListSluiceGate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object listSluiceGate(@Validated @RequestBody ListSluiceGateRequest request) {
        return service.listSluiceGate(request);
    }
}