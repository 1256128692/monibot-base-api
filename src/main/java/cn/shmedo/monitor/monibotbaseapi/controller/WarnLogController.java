package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-08 15:50
 */
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WarnLogController {
    /**
     * @api {POST} /QueryWarnNotifyPage 报警消息分页
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryWarnNotifyPage
     * @apiDescription 报警消息分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索标题或内容
     * @apiParam (请求参数) {Int} [status] 0.未读 1.已读
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.notifyID 系统通知ID
     * @apiSuccess (返回结果) {Int} currentPageData.type 类型，1.报警 2.事件 3.信息
     * @apiSuccess (返回结果) {String} currentPageData.name 通知名称
     * @apiSuccess (返回结果) {String} currentPageData.content 通知内容
     * @apiSuccess (返回结果) {Int} currentPageData.status 通知状态 0.未读 1.已读 2.待办
     * @apiSuccess (返回结果) {DateTime} currentPageData.time 接收时间
     * @apiSuccess (返回结果) {Int} currentPageData.warnLogID 报警记录ID
     * @apiSuccess (返回结果) {Int} currentPageData.warnType 报警类型,1.数据报警 2.设备报警
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryWarnNotifyPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnNotifyPage(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryWarnLatest 查询最新报警
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryWarnLatest
     * @apiDescription 查询最新报警
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} platform 平台
     * @apiSuccess (返回结果) {Object} deviceWarn 设备报警数据列表
     * @apiSuccess (返回结果) {Int} deviceWarn.id 报警ID
     * @apiSuccess (返回结果) {String} deviceWarn.warnName 报警名称
     * @apiSuccess (返回结果) {DateTime} deviceWarn.warnTime 报警时间
     * @apiSuccess (返回结果) {String} deviceWarn.deviceType 设备类型
     * @apiSuccess (返回结果) {String} deviceWarn.deviceModel 设备型号,对应'物联网设备产品名称'或'视频设备类型/型号'
     * @apiSuccess (返回结果) {String} deviceWarn.deviceToken 设备sn
     * @apiSuccess (返回结果) {Object} dataWarn 数据报警数据
     * @apiSuccess (返回结果) {Int} dataWarn.id 报警ID
     * @apiSuccess (返回结果) {String} dataWarn.warnName 报警名称
     * @apiSuccess (返回结果) {DateTime} dataWarn.warnTime 报警时间
     * @apiSuccess (返回结果) {Int} dataWarn.projectID 工程ID
     * @apiSuccess (返回结果) {String} dataWarn.projectName 工程名称
     * @apiSuccess (返回结果) {String} dataWarn.projectShortName 工程简称
     * @apiSuccess (返回结果) {Int} dataWarn.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} dataWarn.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} dataWarn.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} dataWarn.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataWarn.monitorPointName 监测点名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryWarnLatestList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWarnLatestList(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryDataWarnPage 查询数据报警分页
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDataWarnPage
     * @apiDescription 查询数据报警分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Boolean} [isRealTime] 是否是实时报警,默认true.true:实时报警, false:历史报警
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索监测点名称和报警名称
     * @apiParam (请求参数) {Int} [platform] 所属平台
     * @apiParam (请求参数) {Int} [projectID] 工程项目
     * @apiParam (请求参数) {DateTime} [startTime] 开始时间(报警开始时间筛选)
     * @apiParam (请求参数) {DateTime} [endTime] 结束时间(报警开始时间筛选)
     * @apiParam (请求参数) {Int} [warnLevel] 报警等级枚举
     * @apiParam (请求参数) {Int} [dealStatus] 处理状态 0:未处理 1:已处理 默认全部
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 报警记录ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Int} [currentPageData.workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {Object} currentPageData.warnLevel 报警等级枚举
     * @apiSuccess (返回结果) {Int} currentPageData.warnLevel.key 报报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口
     * @apiSuccess (返回结果) {String} currentPageData.warnLevel.styleName 枚举样式名称(等级样式里对应的前缀+报警标签拼接而成)
     * @apiSuccess (返回结果) {String} currentPageData.warnLevel.alias 别名名称
     * @apiSuccess (返回结果) {Int} currentPageData.dealStatus 处理状态 0:未处理 1:已处理
     * @apiSuccess (返回结果) {DateTime} currentPageData.warnTime 报警开始时间
     * @apiSuccess (返回结果) {DateTime} [currentPageData.warnEndTime] 报警结束时间,仅isRealTime为false历史报警时,字段不为null
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (返回结果) {String} currentPageData.projectShotName 工程简称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} currentPageData.gpsLocation 监测点位置
     * @apiSuccess (返回结果) {String} currentPageData.fieldID 监测属性ID
     * @apiSuccess (返回结果) {String} currentPageData.fieldName 监测属性名称
     * @apiSuccess (返回结果) {String} currentPageData.fieldToken 监测属性token
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDataWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDataWarnPage(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryDeviceWarnPage 查询设备报警分页
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDeviceWarnPage
     * @apiDescription 查询设备报警分页
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Boolean} [isRealTime] 是否是实时报警,默认true.true:实时报警, false:历史报警
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索设备SN号和报警名称
     * @apiParam (请求参数) {Int} [platform] 所属平台
     * @apiParam (请求参数) {Int} [projectID] 工程项目
     * @apiParam (请求参数) {DateTime} [startTime] 开始时间(报警开始时间筛选)
     * @apiParam (请求参数) {DateTime} [endTime] 结束时间(报警开始时间筛选)
     * @apiParam (请求参数) {Int} [dealStatus] 处理状态 0:未处理 1:已处理 默认全部
     * @apiParam (请求参数) {Int} [deviceType] 设备类型 1.物联网设备 2.视频设备
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 报警记录ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Int} [currentPageData.workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {Int} currentPageData.dealStatus 处理状态 0:未处理 1:已处理
     * @apiSuccess (返回结果) {DateTime} currentPageData.warnTime 报警开始时间
     * @apiSuccess (返回结果) {DateTime} [currentPageData.warnEndTime] 报警结束时间,仅isRealTime为false历史报警时,字段不为null
     * @apiSuccess (返回结果) {Object[]} currentPageData.projectList 工程列表
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.projectName 工程名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.projectShortName 工程简称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.regionArea 行政区划
     * @apiSuccess (返回结果) {String} currentPageData.projectList.regionAreaName 行政区划名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.gpsLocation 监测点位置
     * @apiSuccess (返回结果) {String} currentPageData.deviceType 设备类型
     * @apiSuccess (返回结果) {String} currentPageData.deviceToken 设备SN
     * @apiSuccess (返回结果) {Int} currentPageData.productID 产品ID
     * @apiSuccess (返回结果) {String} currentPageData.deviceModel 设备型号,对应'物联网设备产品名称'或'视频设备类型/型号'
     * @apiSuccess (返回结果) {String} currentPageData.gpsLocation 设备位置
     * @apiSuccess (返回结果) {String} currentPageData.firmwareVersion 固件版本
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDeviceWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDeviceWarnPage(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryDataWarnDetail 查询数据报警详情
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDataWarnDetail
     * @apiDescription 查询数据报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Int} [workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {Object} warnLevel 报警等级枚举
     * @apiSuccess (返回结果) {Int} warnLevel.key 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口
     * @apiSuccess (返回结果) {String} warnLevel.styleName 枚举样式名称(等级样式里对应的前缀+报警标签拼接而成)
     * @apiSuccess (返回结果) {String} warnLevel.alias 别名名称
     * @apiSuccess (返回结果) {Int} dealStatus 处理状态 0:未处理 1:已处理
     * @apiSuccess (返回结果) {DateTime} warnTime 报警开始时间
     * @apiSuccess (返回结果) {DateTime} [warnEndTime] 报警结束时间,仅记录为历史报警时,字段不为null
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} projectName 工程名称
     * @apiSuccess (返回结果) {String} projectShotName 工程简称
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} gpsLocation 监测点位置
     * @apiSuccess (返回结果) {String} fieldID 监测属性ID
     * @apiSuccess (返回结果) {String} fieldName 监测属性名称
     * @apiSuccess (返回结果) {String} fieldToken 监测属性token
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDataWarnDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDataWarnDetail(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryDeviceWarnDetail 查询设备报警详情
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDeviceWarnDetail
     * @apiDescription 查询设备报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Int} [workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {Int} dealStatus 处理状态 0:未处理 1:已处理
     * @apiSuccess (返回结果) {DateTime} warnTime 报警开始时间
     * @apiSuccess (返回结果) {DateTime} [warnEndTime] 报警结束时间,仅记录为历史报警,字段不为null
     * @apiSuccess (返回结果) {Object[]} projectList 工程列表
     * @apiSuccess (返回结果) {Int} projectList.projectID 工程ID
     * @apiSuccess (返回结果) {String} projectList.projectName 工程名称
     * @apiSuccess (返回结果) {String} projectList.projectShortName 工程简称
     * @apiSuccess (返回结果) {String} projectList.regionArea 行政区划
     * @apiSuccess (返回结果) {String} projectList.regionAreaName 行政区划名称
     * @apiSuccess (返回结果) {Int} projectList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} projectList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} projectList.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Int} projectList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} projectList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} projectList.gpsLocation 监测点位置
     * @apiSuccess (返回结果) {String} deviceType 设备类型
     * @apiSuccess (返回结果) {String} deviceToken 设备SN
     * @apiSuccess (返回结果) {Int} productID 产品ID
     * @apiSuccess (返回结果) {String} deviceModel 设备型号,对应'物联网设备产品名称'或'视频设备类型/型号'
     * @apiSuccess (返回结果) {String} gpsLocation 设备位置
     * @apiSuccess (返回结果) {String} firmwareVersion 固件版本
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDeviceWarnDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDeviceWarnDetail(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryDataWarnHistory 查询数据报警历史
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDataWarnHistory
     * @apiDescription 查询数据报警历史
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.warnTime 报警时间
     * @apiSuccess (返回结果) {Object} dataList.warnLevel 报警等级枚举
     * @apiSuccess (返回结果) {Int} dataList.warnLevel.key 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnThresholdConfigList">/QueryWarnThresholdConfigList</a>接口
     * @apiSuccess (返回结果) {String} dataList.warnLevel.styleName 枚举样式名称(等级样式里对应的前缀+报警标签拼接而成)
     * @apiSuccess (返回结果) {String} dataList.warnLevel.alias 别名名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDataWarnHistory", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDataWarnHistory(@Valid @RequestBody Object param) {
        // 实时报警只到此刻,且历史和实时都限制(自报警后)最多不超过3天
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryDeviceWarnHistory 查询设备报警历史
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDeviceWarnHistory
     * @apiDescription 查询设备报警历史
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.time 时间
     * @apiSuccess (返回结果) {Boolean} dataList.value 是否在线 true:在线; false:离线
     * @apiSuccess (返回结果) {Object[]} fourGSignalList 4g信号强度列表
     * @apiSuccess (返回结果) {DateTime} fourGSignalList.time 时间
     * @apiSuccess (返回结果) {Double} fourGSignalList.value 4g信号强度值
     * @apiSuccess (返回结果) {Object[]} extPowerVoltList 外接电源电压列表
     * @apiSuccess (返回结果) {DateTime} extPowerVoltList.time 时间
     * @apiSuccess (返回结果) {Double} extPowerVoltList.value 外接电源电压值
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDeviceWarnHistory", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDeviceWarnHistory(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddWarnWorkFlowTask 派发工单
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName AddWarnWorkFlowTask
     * @apiDescription 派发工单
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} templateID 模板ID
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddWarnWorkFlowTask", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWarnWorkFlowTask(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /CancelDataWarn 取消数据报警
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName CancelDataWarn
     * @apiDescription 取消数据报警, 可同时设置沉默周期
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} id 报警记录ID
     * @apiParam (请求参数) {Int} [silenceCycle] 设置的沉默周期(小时),若该项不为null时会根据当前时间设置沉默周期
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/CancelDataWarn", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object cancelDataWarn(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /FillDealOpinion 填写报警处理意见
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName FillDealOpinion
     * @apiDescription 填写报警处理意见
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} id 报警记录ID
     * @apiParam (请求参数) {String} opinion 意见
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/FillDealOpinion", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object fillDealOpinion(@Valid @RequestBody Object param) {
        return ResultWrapper.successWithNothing();
    }
}
