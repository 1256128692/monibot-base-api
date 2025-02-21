package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.wtdevice.*;
import cn.shmedo.monitor.monibotbaseapi.service.WtDeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WtDeviceController {

    private final WtDeviceService wtDeviceService;

    /**
     * @api {POST} /QueryWtVideoPageList 分页查询视频设备
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtVideoPageList
     * @apiDescription 分页查询视频设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询设备SN/工程名称/监测点名称
     * @apiParam (请求参数) {String} [videoType] 视频设备型号
     * @apiParam (请求参数) {Bool} [online] 在线状态 在线:true 离线:false
     * @apiParam (请求参数) {Int} [status] 设备状态 0.正常 1.异常
     * @apiParam (请求参数) {String} [areaCode] 行政区划编号
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {String} [monitorItemName] 监测项目名称, 支持模糊查询
     * @apiParam (请求参数) {Int} [ruleID] 规则引擎ID
     * @apiParam (请求参数) {Bool} [select] 是否选中 true:选中 false:未选中
     * @apiParam (请求参数) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 记录条数
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object} map  其他的数据，包含normalBefore 和warnBefore
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {String} currentPageData.videoSN 设备SN
     * @apiSuccess (返回结果) {String} currentPageData.videoType 设备型号
     * @apiSuccess (返回结果) {Bool} currentPageData.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} currentPageData.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} currentPageData.projectShortName 工程项目简称
     * @apiSuccess (返回结果) {String} currentPageData.location 工程项目行政区划
     * @apiSuccess (返回结果) {String} currentPageData.projectAddress 工程项目地址
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} currentPageData.pointGpsLocation 监测点GPS位置
     * @apiSuccess (返回结果) {String} currentPageData.[pointImageLocation] 监测点底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseVideo
     */
    @Permission(permissionName = "mdmbase:ListBaseVideo")
    @PostMapping(value = "/QueryWtVideoPageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtVideoPageList(@Valid @RequestBody QueryWtVideoPageParam param) {
        return wtDeviceService.queryWtVideoPageList(param);
    }

    /**
     * @api {POST} /ExportWtVideo  导出视频设备
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName ExportWtVideo
     * @apiDescription 导出视频设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询设备SN/工程名称/监测点名称
     * @apiParam (请求参数) {String} [videoType] 视频设备型号
     * @apiParam (请求参数) {Bool} [online] 在线状态 在线:true 离线:false
     * @apiParam (请求参数) {Int} [status] 设备状态 0.正常 1.异常
     * @apiParam (请求参数) {String} [areaCode] 行政区划编号
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {String} [monitorItemName] 监测项目名称, 支持模糊查询
     * @apiParam (请求参数) {Int} [ruleID] 规则引擎ID
     * @apiParam (请求参数) {Bool} [select] 是否选中 true:选中 false:未选中
     * @apiSuccess (返回结果) {Object[]} list 数据
     * @apiSuccess (返回结果) {String} list.videoSN 设备SN
     * @apiSuccess (返回结果) {String} list.videoType 设备型号
     * @apiSuccess (返回结果) {Bool} list.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} list.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {DateTime} list.lastActiveTime 最后活跃时间
     * @apiSuccess (返回结果) {DateTime} list.createTime 设备创建时间
     * @apiSuccess (返回结果) {Int} list.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} list.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} list.projectShortName 工程项目简称
     * @apiSuccess (返回结果) {String} list.location 工程项目行政区划
     * @apiSuccess (返回结果) {String} list.projectAddress 工程项目地址
     * @apiSuccess (返回结果) {Int} list.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} list.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} list.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} list.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} list.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} list.pointGpsLocation 监测点GPS位置
     * @apiSuccess (返回结果) {String} list.[pointImageLocation] 监测点底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ExportBaseDevice
     */
    @Permission(permissionName = "mdmbase:ExportBaseDevice")
    @PostMapping(value = "/ExportWtVideo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object exportWtVideo(@Valid @RequestBody ExportWtVideoParam param) {
        return wtDeviceService.exportWtVideo(param);
    }

    /**
     * @api {POST} /QueryWtVideoTypeList 查询视频型号列表
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtVideoTypeList
     * @apiDescription 查询视频型号列表
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表
     * @apiSuccess (返回结果) {String[]} videoTypeList 视频型号列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseVideo
     */
    @Permission(permissionName = "mdmbase:ListBaseVideo")
    @PostMapping(value = "/QueryWtVideoTypeList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtVideoTypeList(@Valid @RequestBody QueryWtVideoTypeParam param) {
        return wtDeviceService.queryWtVideoTypeList(param);
    }

    /**
     * @api {POST} /QueryWtDevicePageList 分页查询物联网设备
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryWtDevicePageList
     * @apiDescription 分页查询物联网设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表(100)
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询设备SN/工程名称/监测点名称
     * @apiParam (请求参数) {Int} [productID] 产品ID(设备型号)
     * @apiParam (请求参数) {Bool} [online] 在线状态 在线:true 离线:false
     * @apiParam (请求参数) {Int} [status] 设备状态 0.正常 1.异常
     * @apiParam (请求参数) {String} [areaCode] 行政区划编号
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {String} [monitorItemName] 监测项目名称, 支持模糊查询
     * @apiParam (请求参数) {Int} [ruleID] 规则引擎ID
     * @apiParam (请求参数) {Bool} [select] 是否选中 true:选中 false:未选中
     * @apiParam (请求参数) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求参数) {Int} currentPage 当前页
     * @apiParam (请求参数) {Int} pageSize 页大小
     * @apiSuccess (返回结果) {Int} totalCount 总条数
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object} map  其他的数据，包含normalBefore 和warnBefore
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.deviceID 设备ID
     * @apiSuccess (返回结果) {String} currentPageData.deviceSN 设备SN
     * @apiSuccess (返回结果) {String} [currentPageData.firewallVersion] 固件版本
     * @apiSuccess (返回结果) {String} currentPageData.productID 产品ID
     * @apiSuccess (返回结果) {String} currentPageData.productName 产品名称
     * @apiSuccess (返回结果) {Bool} currentPageData.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {DateTime} currentPageData.lastActiveTime 最后活跃时间
     * @apiSuccess (返回结果) {DateTime} currentPageData.createTime 设备创建时间
     * @apiSuccess (返回结果) {Int} currentPageData.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {Object[]} currentPageData.projectList 工程项目信息
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.projectShortName 工程项目简称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.location 工程项目行政区划
     * @apiSuccess (返回结果) {String} currentPageData.projectList.projectAddress 工程项目地址
     * @apiSuccess (返回结果) {Object[]} currentPageData.projectList.monitorPointList 监测点信息
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} currentPageData.projectList.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} currentPageData.projectList.monitorPointList.pointGpsLocation 监测点GPS位置
     * @apiSuccess (返回结果) {String} [currentPageData.projectList.monitorPointList.pointImageLocation] 监测点底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseDevice")
    @PostMapping(value = "/QueryWtDevicePageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWtDevicePageList(@Valid @RequestBody QueryWtDevicePageListParam pa) {
        return wtDeviceService.queryWtDevicePageList(pa);
    }

    /**
     * @api {POST} /ExportWtDevice 导出物联网设备
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName ExportWtDevice
     * @apiDescription 导出物联网设备
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程项目ID列表(100)
     * @apiParam (请求参数) {String} [queryCode] 关键词,支持模糊查询设备SN/工程名称/监测点名称
     * @apiParam (请求参数) {Int} [productID] 产品ID(设备型号)
     * @apiParam (请求参数) {Bool} [online] 在线状态 在线:true 离线:false
     * @apiParam (请求参数) {Int} [status] 设备状态 0.正常 1.异常
     * @apiParam (请求参数) {String} [areaCode] 行政区划编号
     * @apiParam (请求参数) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求参数) {String} [monitorItemName] 监测项目名称, 支持模糊查询
     * @apiParam (请求参数) {Int} [ruleID] 规则引擎ID
     * @apiParam (请求参数) {Bool} [select] 是否选中 true:选中 false:未选中
     * @apiSuccess (返回结果) {Object[]} list 数据
     * @apiSuccess (返回结果) {String} list.deviceSN 设备SN
     * @apiSuccess (返回结果) {Int} list.deviceID 设备ID
     * @apiSuccess (返回结果) {String} [list.firewallVersion] 固件版本
     * @apiSuccess (返回结果) {String} list.productID 产品ID
     * @apiSuccess (返回结果) {String} list.productName 产品名称
     * @apiSuccess (返回结果) {Bool} list.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {Int} list.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {Object[]} list.projectList 工程项目信息
     * @apiSuccess (返回结果) {Int} list.projectList.projectID 工程项目ID
     * @apiSuccess (返回结果) {String} list.projectList.projectName 工程项目名称
     * @apiSuccess (返回结果) {String} list.projectList.projectShortName 工程项目简称
     * @apiSuccess (返回结果) {String} list.projectList.location 工程项目行政区划
     * @apiSuccess (返回结果) {String} list.projectList.projectAddress 工程项目地址
     * @apiSuccess (返回结果) {Object[]} list.projectList.monitorPointList 监测点信息
     * @apiSuccess (返回结果) {Int} list.projectList.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} list.projectList.monitorPointList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} list.projectList.monitorPointList.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} list.projectList.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} list.projectList.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} list.projectList.monitorPointList.pointGpsLocation 监测点GPS位置
     * @apiSuccess (返回结果) {String} [list.projectList.monitorPointList.pointImageLocation] 监测点底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ExportBaseDevice
     */
    @Permission(permissionName = "mdmbase:ExportBaseDevice")
    @PostMapping(value = "/ExportWtDevice", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object exportWtDevice(@Valid @RequestBody ExportWtDeviceParam pa) {
        return wtDeviceService.exportWtDevice(pa);
    }

    /**
     * @api {POST} /QueryProductSimpleList 产品简要信息列表
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryProductSimpleList
     * @apiDescription 产品简要信息列表
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int[]} [isEnable] 推送是否启用 (对应物联网数据推送开关)
     * @apiParam (请求参数) {String} [deviceToken] 指定查询的设备token
     * @apiSuccess (返回结果) {String} productID 产品ID
     * @apiSuccess (返回结果) {String} productName 产品名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseDevice")
    @PostMapping(value = "/QueryProductSimpleList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deviceTypeList(@Valid @RequestBody QueryProductSimpleParam param) {
        return wtDeviceService.productSimpleList(param);
    }

    /**
     * @api {POST} /QueryDeviceDetail 查询设备详情
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName QueryDeviceDetail
     * @apiDescription 产品简要信息列表
     * @apiParam (请求参数) {Int[]} projectIDList 工程项目ID列表
     * @apiParam (请求参数) {Int} [companyID] 公司ID,为null则获取当前用户所在公司ID
     * @apiParam (请求参数) {String} deviceToken 设备SN
     * @apiSuccess (返回结果) {Int} deviceID 设备ID
     * @apiSuccess (返回结果) {String} deviceToken 设备SN
     * @apiSuccess (返回结果) {String} deviceName 设备名称
     * @apiSuccess (返回结果) {String} productName 产品名称（设备型号）
     * @apiSuccess (返回结果) {String} firmwareVersion 固件版本
     * @apiSuccess (返回结果) {Bool} onlineStatus 在线状态
     * @apiSuccess (返回结果) {DateTime} lastActiveTime 最后活跃时间
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间
     * @apiSuccess (返回结果) {Object} state 设备状态
     * @apiSuccess (返回结果) {Int} state.status 设备状态 0.正常 1.异常
     * @apiSuccess (返回结果) {Double} [state.extPowerVolt] 外接电源电压
     * @apiSuccess (返回结果) {Double} [state.innerPowerVolt] 内部电源电压
     * @apiSuccess (返回结果) {Double} [state.temp] 温度
     * @apiSuccess (返回结果) {Double} [state.humidity] 湿度
     * @apiSuccess (返回结果) {Double} [state.tempOut] 设备外部环境温度
     * @apiSuccess (返回结果) {Double} [state.humidityOut] 设备外部环境湿度
     * @apiSuccess (返回结果) {Double} [state.fourGSignal] 4G信号强度
     * @apiSuccess (返回结果) {Double} [state.bdSignal] 北斗功率等级
     * @apiSuccess (返回结果) {Double} [state.swVersion] 固件版本
     * @apiSuccess (返回结果) {Double} [state.location] 位置
     * @apiSuccess (返回结果) {String} [state.sensorErrno] 传感器错误码
     * @apiSuccess (返回结果) {Double} [state.solarVolt] 太阳能板电压
     * @apiSuccess (返回结果) {Double} [state.batteryVolt] 蓄电池电压
     * @apiSuccess (返回结果) {Double} [state.supplyPower] 充电功率
     * @apiSuccess (返回结果) {Double} [state.consumePower] 消耗功率
     * @apiSuccess (返回结果) {Double} [state.workCurrent] 设备工作电流
     * @apiSuccess (返回结果) {Double} [state.voltPercent] 剩余电量百分比
     * @apiSuccess (返回结果) {Double} [state.uptime] 设备启动后工作时间，单位秒
     * @apiSuccess (返回结果) {Double} [state.iccid] 湿度设备物联网卡iccid，多个逗号分隔
     * @apiSuccess (返回结果) {Double} [state.imei] 通信模块身份识别码
     * @apiSuccess (返回结果) {Double} [state.onlinePercent] 设备平台在线时间百分比
     * @apiSuccess (返回结果) {Double} [state.rebootCode] 设备重启代码
     * @apiSuccess (返回结果) {Double} [state.loraUpSignal] LORA上行信号强度，单位dbm
     * @apiSuccess (返回结果) {Double} [state.loraDownSignal] LORA下行信号强度，单位dbm
     * @apiSuccess (返回结果) {Json} [location] 位置信息
     * @apiSuccess (返回结果) {String} location.address 地址
     * @apiSuccess (返回结果) {String} [location.locationJson] 位置扩展，json字符串
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseDevice
     */
    @Permission(permissionName = "mdmbase:DescribeBaseDevice")
    @PostMapping(value = "/QueryDeviceDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deviceInfo(@Valid @RequestBody QueryDeviceDetailParam param) {
        return wtDeviceService.deviceDetail(param);
    }

    /**
     * @api {POST} /SetIntelDeviceLocationInSys 设置智能设备位置信息
     * @apiVersion 1.0.0
     * @apiGroup 水利设备列表模块
     * @apiName SetIntelDeviceLocationInSys
     * @apiDescription 设置智能设备位置信息，仅在当前系统中
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {String} deviceToken 设备SN或者视频设备的Serial
     * @apiParam (请求参数) {Int} type 类型，0,1对应iot，视频设备
     * @apiParam (请求参数) {String} address 地址
     * @apiParam (请求参数) {String} [locationJson] 位置扩展，json字符串
     * @apiSuccess (返回结果) {String} none 无返回值
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:SetIntelDeviceInfoInSys
     */
    @Permission(permissionName = "mdmbase:SetIntelDeviceInfoInSys")
    @LogParam(moduleName = "水利设备列表模块", operationName = "设置物联网设备位置信息", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "/SetIntelDeviceLocationInSys", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object setIntelDeviceLocationInSys(@Valid @RequestBody SetIntelDeviceLocationInSysParam pa) {
        wtDeviceService.setIntelDeviceLocationInSys(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }
}
