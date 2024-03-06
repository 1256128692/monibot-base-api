package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.interceptor.CurrentSubjectFilter;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnlog.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDataWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbDeviceWarnLogService;
import cn.shmedo.monitor.monibotbaseapi.service.IWarnLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-08 15:50
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarnLogController {
    private final IWarnLogService warnLogService;
    private final ITbDataWarnLogService dataWarnLogService;
    private final ITbDeviceWarnLogService deviceWarnLogService;

    /**
     * @api {POST} /QueryDataWarnPage 查询数据报警分页
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDataWarnPage
     * @apiDescription 查询数据报警分页(包含实时报警和历史报警)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} platform 所属平台
     * @apiParam (请求参数) {Boolean} [isRealTime] 是否是实时报警,默认true.true:实时报警, false:历史报警
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索监测点名称和报警名称
     * @apiParam (请求参数) {Int} [projectID] 工程项目
     * @apiParam (请求参数) {DateTime} [startTime] 开始时间(报警开始时间筛选)
     * @apiParam (请求参数) {DateTime} [endTime] 结束时间(报警开始时间筛选)
     * @apiParam (请求参数) {Int} [warnLevel] 报警等级枚举key
     * @apiParam (请求参数) {Int} [dealStatus] 处理状态 0:未处理 1:已处理 2:取消 默认全部
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object} map 包含报警标签等数据,格式为:{"warnTag":1,"warnLevelType":1,"warnLevelStyle":1}
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 报警记录ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Int} [currentPageData.workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {String} [currentPageData.dealContent] 处理意见,若未填报为null
     * @apiSuccess (返回结果) {Int} [currentPageData.dealUserID] 处理人ID,若未填报为null
     * @apiSuccess (返回结果) {String} [currentPageData.dealUserName] 处理人名称,若未填报为null
     * @apiSuccess (返回结果) {DateTime} [currentPageData.dealTime] 处理时间,若未填报为null
     * @apiSuccess (返回结果) {Object} currentPageData.aliasConfig 报警等级枚举
     * @apiSuccess (返回结果) {Int} currentPageData.aliasConfig.warnLevel 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnNotifyConfigList">/QueryWarnNotifyConfigList</a>接口
     * @apiSuccess (返回结果) {String} [currentPageData.aliasConfig.alias] 别名名称
     * @apiSuccess (返回结果) {Int} currentPageData.dealStatus 处理状态 0:未处理 1:已处理 2:取消
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
     * @apiSuccess (返回结果) {Int} currentPageData.fieldID 监测属性ID
     * @apiSuccess (返回结果) {String} currentPageData.fieldName 监测属性名称
     * @apiSuccess (返回结果) {String} currentPageData.fieldToken 监测属性token
     * @apiSuccess (返回结果) {Int} currentPageData.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} currentPageData.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} currentPageData.sensorAlias 传感器别称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDataWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDataWarnPage(@Valid @RequestBody QueryDataWarnPageParam param) {
        return dataWarnLogService.queryDataWarnPage(param);
    }

    /**
     * @api {POST} /QueryDeviceWarnPage 查询设备报警分页
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDeviceWarnPage
     * @apiDescription 查询设备报警分页(包含实时报警和历史报警)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} platform 所属平台
     * @apiParam (请求参数) {Boolean} [isRealTime] 是否是实时报警,默认true.true:实时报警, false:历史报警
     * @apiParam (请求参数) {String} [queryCode] 关键字,支持模糊搜索设备SN号和报警名称
     * @apiParam (请求参数) {Int} [projectID] 工程项目
     * @apiParam (请求参数) {DateTime} [startTime] 开始时间(报警开始时间筛选)
     * @apiParam (请求参数) {DateTime} [endTime] 结束时间(报警开始时间筛选)
     * @apiParam (请求参数) {Int} [dealStatus] 处理状态 0:未处理 1:已处理 2:取消 默认全部
     * @apiParam (请求参数) {Int} [deviceType] 设备类型 1.物联网设备 2.视频设备
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object} map 包含报警标签等数据,格式为:{"warnTag":1,"warnLevelType":1,"warnLevelStyle":1}
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 报警记录ID
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (返回结果) {String} currentPageData.warnName 报警名称
     * @apiSuccess (返回结果) {Int} [currentPageData.workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {String} [currentPageData.dealContent] 处理意见,若未填报为null
     * @apiSuccess (返回结果) {Int} [currentPageData.dealUserID] 处理人ID,若未填报为null
     * @apiSuccess (返回结果) {String} [currentPageData.dealUserName] 处理人名称,若未填报为null
     * @apiSuccess (返回结果) {DateTime} [currentPageData.dealTime] 处理时间,若未填报为null
     * @apiSuccess (返回结果) {Int} currentPageData.dealStatus 处理状态 0:未处理 1:已处理 2:取消
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
     * @apiSuccess (返回结果) {Int} currentPageData.deviceType 设备类型 1.物联网设备 2.视频设备
     * @apiSuccess (返回结果) {String} currentPageData.deviceSerial 设备SN
     * @apiSuccess (返回结果) {Int} currentPageData.productID 产品ID
     * @apiSuccess (返回结果) {String} currentPageData.deviceModel 设备型号,对应'物联网设备产品名称'或'视频设备类型/型号'
     * @apiSuccess (返回结果) {String} currentPageData.gpsLocation 设备位置
     * @apiSuccess (返回结果) {String} currentPageData.firmwareVersion 固件版本
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDeviceWarnPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDeviceWarnPage(@Valid @RequestBody QueryDeviceWarnPageParam param) {
        return deviceWarnLogService.queryDeviceWarnPage(param);
    }

    /**
     * @api {POST} /QueryDataWarnDetail 查询数据报警详情
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDataWarnDetail
     * @apiDescription 查询数据报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiSuccess (返回结果) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Int} [workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {String} [dealContent] 处理意见,若未填报为null
     * @apiSuccess (返回结果) {Int} [dealUserID] 处理人ID,若未填报为null
     * @apiSuccess (返回结果) {String} [dealUserName] 处理人名称,若未派发为null
     * @apiSuccess (返回结果) {DateTime} [dealTime] 处理时间,若未填报为null
     * @apiSuccess (返回结果) {Int} warnTag 报警标签枚举 1.报警 2.告警 3.预警
     * @apiSuccess (返回结果) {Int} warnLevelType 报警等级类型枚举 1: 4级 2: 3级(配置阈值前可修改)
     * @apiSuccess (返回结果) {Int} warnLevelStyle 等级样式枚举 1: 红橙黄蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSuccess (返回结果) {Object} aliasConfig 报警等级枚举
     * @apiSuccess (返回结果) {Int} aliasConfig.warnLevel 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnNotifyConfigList">/QueryWarnNotifyConfigList</a>接口
     * @apiSuccess (返回结果) {String} [aliasConfig.alias] 别名名称
     * @apiSuccess (返回结果) {Int} dealStatus 处理状态 0:未处理 1:已处理 2:取消
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
     * @apiSuccess (返回结果) {Int} fieldID 监测属性ID
     * @apiSuccess (返回结果) {String} fieldName 监测属性名称
     * @apiSuccess (返回结果) {String} fieldToken 监测属性token
     * @apiSuccess (返回结果) {Int} sensorID 传感器ID
     * @apiSuccess (返回结果) {String} sensorName 传感器名称
     * @apiSuccess (返回结果) {String} sensorAlias 传感器别称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDataWarnDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDataWarnDetail(@Valid @RequestBody QueryDataWarnDetailParam param) {
        return dataWarnLogService.queryDataWarnDetail(param);
    }

    /**
     * @api {POST} /QueryDeviceWarnDetail 查询设备报警详情
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDeviceWarnDetail
     * @apiDescription 查询设备报警详情
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiSuccess (返回结果) {Int} id 报警记录ID
     * @apiSuccess (返回结果) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {String} warnName 报警名称
     * @apiSuccess (返回结果) {Int} [workOrderID] 工单ID,若未派发为null
     * @apiSuccess (返回结果) {String} [dealContent] 处理意见,若未填报为null
     * @apiSuccess (返回结果) {Int} [dealUserID] 处理人ID,若未填报为null
     * @apiSuccess (返回结果) {String} [dealUserName] 处理人名称,若未派发为null
     * @apiSuccess (返回结果) {DateTime} [dealTime] 处理时间,若未填报为null
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
     * @apiSuccess (返回结果) {Int} deviceType 设备类型 1.物联网设备 2.视频设备
     * @apiSuccess (返回结果) {String} deviceSerial 设备SN
     * @apiSuccess (返回结果) {Int} productID 产品ID
     * @apiSuccess (返回结果) {String} deviceModel 设备型号,对应'物联网设备产品名称'或'视频设备类型/型号'
     * @apiSuccess (返回结果) {String} gpsLocation 设备位置
     * @apiSuccess (返回结果) {String} firmwareVersion 固件版本
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDeviceWarnDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDeviceWarnDetail(@Valid @RequestBody QueryDeviceWarnDetailParam param) {
        return deviceWarnLogService.queryDeviceWarnDetail(param);
    }

    /**
     * @api {POST} /QueryDataWarnHistory 查询数据报警历史
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDataWarnHistory
     * @apiDescription 查询数据报警历史(报警等级变化)
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiSuccess (返回结果) {Int} warnTag 报警标签枚举 1.报警 2.告警 3.预警
     * @apiSuccess (返回结果) {Int} warnLevelType 报警等级类型枚举 1: 4级 2: 3级(配置阈值前可修改)
     * @apiSuccess (返回结果) {Int} warnLevelStyle 等级样式枚举 1: 红橙黄蓝 2: 1,2,3,4级 3: Ⅰ,Ⅱ,Ⅲ,Ⅳ级
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.warnTime 报警时间
     * @apiSuccess (返回结果) {Int} dataList.compareMode 比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于
     * @apiSuccess (返回结果) {String} dataList.threshold 阈值,格式{"upper":2.0,"lower":1.0}(json字符串),如果对应的配置被更改导致查不到时会返回"{}"
     * @apiSuccess (返回结果) {Object} dataList.aliasConfig 报警等级枚举
     * @apiSuccess (返回结果) {Int} dataList.aliasConfig.warnLevel 报警等级枚举key,枚举值参考<a href="#api-报警配置模块-QueryWarnNotifyConfigList">/QueryWarnNotifyConfigList</a>接口
     * @apiSuccess (返回结果) {String} [dataList.aliasConfig.alias] 别名名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryDataWarnHistory", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDataWarnHistory(@Valid @RequestBody QueryDataWarnDetailParam param) {
        // 实时报警只到此刻,且历史和实时都限制(自报警后)最多不超过3天
        return dataWarnLogService.queryDataWarnHistory(param);
    }

    /**
     * @api {POST} /QueryDeviceWarnHistory 查询设备报警历史
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName QueryDeviceWarnHistory
     * @apiDescription 查询设备报警历史
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiSuccess (返回结果) {DateTime} warnTime 报警时间
     * @apiSuccess (返回结果) {DateTime} [warnEndTime] 报警结束时间
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
    public Object queryDeviceWarnHistory(@Valid @RequestBody QueryDeviceWarnDetailParam param) {
        return deviceWarnLogService.queryDeviceWarnHistory(param.getTbDeviceWarnLog());
    }

    /**
     * @apiDeprecated use now (#报警管理模块:AddWarnLogBindWorkFlowTask).
     * @api {POST} /AddWarnWorkFlowTask 派发工单
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName AddWarnWorkFlowTask
     * @apiDescription 派发工单
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} templateID 模板ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiParam (请求参数) {Int} warnType 报警类型,1.数据报警 2.设备报警
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddWarnWorkFlowTask", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWarnWorkFlowTask(@Valid @RequestBody AddWarnWorkFlowTaskParam param) {
//        final Integer userID = Optional.ofNullable(CurrentSubjectHolder.getCurrentSubject()).map(CurrentSubject::getSubjectID).orElse(null);
//        if (Objects.isNull(userID)) {
//            return ResultWrapper.withCode(ResultCode.SERVICE_NOT_AUTHENTICATION);
//        }
//        // TODO 加上权限校验注解后将上文替换成本注解
//        // final Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
//        warnLogService.addWarnWorkFlowTask(userID, param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddWarnLogBindWorkFlowTask 派发工单
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName AddWarnLogBindWorkFlowTask
     * @apiDescription 派发工单
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} workOrderID 工单ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiParam (请求参数) {Int} warnType 报警类型,1.数据报警 2.设备报警
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddWarnLogBindWorkFlowTask", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addWarnLogBindWorkFlowTask(@Valid @RequestBody AddWarnLogBindWorkFlowTaskParam param) {
        final Integer userID = Optional.ofNullable(CurrentSubjectHolder.getCurrentSubject()).map(CurrentSubject::getSubjectID).orElse(null);
        if (Objects.isNull(userID)) {
            return ResultWrapper.withCode(ResultCode.SERVICE_NOT_AUTHENTICATION);
        }
//        // TODO 加上权限校验注解后将上文替换成本注解
//        // final Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        warnLogService.addWarnLogBindWorkFlowTask(userID, param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /CancelDataWarn 取消数据报警
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName CancelDataWarn
     * @apiDescription 取消数据报警, 可同时设置沉默周期
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiParam (请求参数) {Int} [silenceCycle] 设置的沉默周期(小时),若该项不为null时会根据当前时间设置沉默周期
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/CancelDataWarn", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object cancelDataWarn(@Valid @RequestBody CancelDataWarnParam param) {
        final Integer userID = Optional.ofNullable(CurrentSubjectHolder.getCurrentSubject()).map(CurrentSubject::getSubjectID).orElse(null);
        if (Objects.isNull(userID)) {
            return ResultWrapper.withCode(ResultCode.SERVICE_NOT_AUTHENTICATION);
        }
        // TODO 加上权限校验注解后将上文替换成本注解
        // final Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        dataWarnLogService.cancelDataWarn(userID, param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /FillDealOpinion 填写报警处理意见
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName FillDealOpinion
     * @apiDescription 填写报警处理意见
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} warnLogID 报警记录ID
     * @apiParam (请求参数) {Int} warnType 报警类型,1.数据报警 2.设备报警
     * @apiParam (请求参数) {String} [opinion] 意见
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/FillDealOpinion", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object fillDealOpinion(@Valid @RequestBody FillDealOpinionParam param) {
        final Integer userID = Optional.ofNullable(CurrentSubjectHolder.getCurrentSubject()).map(CurrentSubject::getSubjectID).orElse(null);
        if (Objects.isNull(userID)) {
            return ResultWrapper.withCode(ResultCode.SERVICE_NOT_AUTHENTICATION);
        }
        // TODO 加上权限校验注解后将上文替换成本注解
        // final Integer userID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        warnLogService.fillDealOpinion(userID, param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateDeviceGroupSenderEvent 更新设备数据推送配置回调
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName UpdateDeviceGroupSenderEvent
     * @apiDescription 更新设备数据推送配置后, 需要调用该接口 (仅限服务内部调用)
     * @apiParam (请求参数) {Object[]} dataList 数据列表
     * @apiParam (请求参数) {String} dataList.deviceToken 设备sn
     * @apiParam (请求参数) {Int[]} dataList.projectIDList 更新后推送工程的IDList(删除设备时,该项为空list)
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 应用权限, 不允许用户调用 mdmbase:WriteBaseWarn
     */
    @Permission(permissionName = "mdmbase:WriteBaseWarn", allowUser = false, allowApplication = true)
    @PostMapping(value = "/UpdateDeviceGroupSenderEvent", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateDeviceGroupSenderEvent(@Valid @NotNull @RequestBody List<UpdateDeviceGroupSenderEventParam> param, HttpServletRequest request) {
        deviceWarnLogService.updateDeviceGroupSenderEvent(param, request.getHeader(CurrentSubjectFilter.TOKEN_HEADER), CurrentSubjectHolder.getCurrentSubject());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /SaveDataWarn 写入数据报警
     * @apiVersion 1.0.0
     * @apiGroup 报警管理模块
     * @apiName SaveDataWarn
     * @apiDescription 写入数据报警 (仅限服务内部调用)
     * @apiParam (请求参数) {Int} thresholdID 阈值配置id
     * @apiParam (请求参数) {Int} warnLevel 报警等级(0-4 0表示未触发预警，数据已恢复正常)
     * @apiParam (请求参数) {Double} warnValue 报警数据值
     * @apiParam (请求参数) {DateTime} warnTime 报警时间(yyyy-MM-dd HH:mm:ss)
     * @apiSuccess (返回结果) {String} none 无
     * @apiParamExample {json} Request-Example:
     * {"thresholdID": 1,"warnLevel": 1,"warnValue": 0.1,"warnTime": "2024-01-01 00:00:00"}
     * @apiSampleRequest off
     * @apiPermission 应用权限, 不允许用户调用 mdmbase:WriteBaseWarn
     */
    @Permission(permissionName = "mdmbase:WriteBaseWarn", allowUser = false, allowApplication = true)
    @PostMapping(value = "/SaveDataWarn", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object saveDataWarn(@Valid @NotNull @RequestBody SaveDataWarnParam param) {
        dataWarnLogService.saveDataWarnLog(param);
        return ResultWrapper.successWithNothing();
    }
}
