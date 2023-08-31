package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.HkVideoService;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VideoController {
    private final VideoService videoService;

    private final HkVideoService hkVideoService;

    /**
     * @api {POST} /QueryVideoMonitorPointLiveInfo 查询视频类型监测点直播地址信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频类型监测点直播地址信息
     * @apiName QueryVideoMonitorPointLiveInfo
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList  监测点ID列表
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorPointIDList":["33"]}
     * @apiSuccess (响应结果) {Object[]} data  结果列表
     * @apiSuccess (返回结果) {Int} data.sensorID  传感器ID
     * @apiSuccess (返回结果) {String} data.sensorName  传感器名称
     * @apiSuccess (返回结果) {Int} data.monitorPointID   监测点ID
     * @apiSuccess (返回结果) {String} data.monitorPointName   监测点名称
     * @apiSuccess (返回结果) {Int} data.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String} data.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {String} data.monitorItemAlias   监测项目别名
     * @apiSuccess (返回结果) {String} data.baseUrl  标清直播地址
     * @apiSuccess (返回结果) {String} data.hdUrl  高清直播地址
     * @apiSuccess (返回结果) {String} data.ysToken 萤石云token
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryVideoMonitorPointLiveInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoMonitorPointLiveInfo(@Validated @RequestBody QueryVideoMonitorPointLiveInfoParam pa) {
        return videoService.queryVideoMonitorPointLiveInfo(pa);
    }


    /**
     * @api {POST} /QueryVideoMonitorPointHistoryLiveInfo 查询视频类型监测点历史时间段的直播地址信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频类型监测点历史时间段的直播地址信息, 该接口会根据监测点下的对应的视频设备, 去分析是萤石云还是海康的, 返回一个地址
     * @apiName QueryVideoMonitorPointHistoryLiveInfo
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorPointID  监测点ID
     * @apiParam (请求体) {Date} beginTime  开始时间
     * @apiParam (请求体) {Date} endTime  结束时间
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorPointID":"33","beginTime":"2023-04-12 08:00:00","endTime":"2023-04-13 08:00:00"}
     * @apiSuccess (返回结果) {String} historyLiveAddress  历史回放地址
     * @apiSuccess (返回结果) {String} ysToken  萤石云token
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryVideoMonitorPointHistoryLiveInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoMonitorPointHistoryLiveInfo(@Validated @RequestBody QueryVideoMonitorPointHistoryLiveInfoParam pa) {
        return videoService.queryVideoMonitorPointHistoryLiveInfo(pa);
    }


    /**
     * @api {POST} /PanControlVideoPoint 云台控制监测点视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 云台控制监测点视频设备的摄像头移动, 有(上下左右, 焦距)等操作
     * @apiName PanControlVideoPoint
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorPointID  监测点ID
     * @apiParam (请求体) {Int} direction  方向   0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorPointID":"33","direction":"1"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/PanControlVideoPoint", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object panControlVideoPoint(@Validated @RequestBody PanControlVideoPointParam pa) {
        return videoService.panControlVideoPoint(pa);
    }


    /**
     * @api {POST} /QueryVideoMonitorPointPictureInfo 查询视频类型监测点图像信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频类型监测点图像信息, 开始时间与结束时间不能超过一周
     * @apiName QueryVideoMonitorPointPictureInfo
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorPointID  监测点ID
     * @apiParam (请求体) {Date} beginTime  开始时间
     * @apiParam (请求体) {Date} endTime  结束时间
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorPointID":"33",,"beginTime":"2023-04-12 08:00:00","endTime":"2023-04-13 08:00:00"}
     * @apiSuccess (返回结果) {Object[]} data
     * @apiSuccess (返回结果) {Int} data.sensorID 传感器ID
     * @apiSuccess (返回结果) {Date} data.unloadTime 图片上传时间
     * @apiSuccess (返回结果) {String} data.filePath 图片路径地址
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryVideoMonitorPointPictureInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoMonitorPointPictureInfo(@Validated @RequestBody QueryVideoMonitorPointPictureInfoParam pa) {
        return videoService.queryVideoMonitorPointPictureInfo(pa);
    }

    /**
     * @api {POST} /QueryVideoBaseInfo 查询视频基本信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryVideoBaseInfo
     * @apiDescription 查询视频基本信息
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {String} videoSn 视频SN
     * @apiSuccess (返回结果) {String} videoName 视频设备名称
     * @apiSuccess (返回结果) {String} videoType 视频设备型号
     * @apiSuccess (返回结果) {Int} status 在线状态：0-不在线，1-在线
     * @apiSuccess (返回结果) {String} netAddress ip地址
     * @apiSuccess (返回结果) {String} netType 网络类型
     * @apiSuccess (返回结果) {String} signal 信号强度
     * @apiSuccess (返回结果) {DateTime} updateTime 修改时间
     * @apiSuccess (返回结果) {Int} isEncrypt 是否加密：0-不加密，1-加密
     * @apiSuccess (返回结果) {Int} riskLevel 设备风险安全等级，0-安全，大于零，有风险，风险越高，值越大
     * @apiSuccess (返回结果) {Int} offlineNotify 设备下线是否通知：0-不通知 1-通知
     * @apiSuccess (返回结果) {Int} alarmSoundMode 告警声音模式：-1-不支持警告声音 0-短叫，1-长叫，2-静音
     * @apiSuccess (返回结果) {Int} defence 能力的设备布撤防状态：0-睡眠，8-在家，16-外出，普通IPC布撤防状态：0-撤防，1-布防
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryVideoBaseInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoBaseInfo(@Valid @RequestBody QueryVideoBaseInfoParam param) {
        return videoService.queryVideoBaseInfo(param);
    }

    /**
     * @api {POST} /QueryVideoCompanyViewBaseInfo 查询视频实时预览基础信息(企业级)
     * @apiDescription 查询视频实时预览基础信息(企业级)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryVideoCompanyViewBaseInfo
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [status] 视频设备状态枚举 0.全部 1.仅在线 2.仅离线（默认是0.全部）
     * @apiParam (请求体) {String} [deviceSerial] 序列号/唯一标识
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.deviceVideoID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} dataList.deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} dataList.deviceName 设备名称
     * @apiSuccess (返回结果) {String} dataList.deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} dataList.deviceChannelNum 设备可接入的通道号数量
     * @apiSuccess (返回结果) {Int} dataList.accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} dataList.accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} dataList.accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} dataList.companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} [dataList.projectID] 项目ID
     * @apiSuccess (返回结果) {Int} dataList.storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} dataList.captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} dataList.allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {Int[]} dataList.deviceChannel 通道号列表
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryVideoCompanyViewBaseInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoCompanyViewBaseInfo(@Valid @RequestBody Object param) {
        //
        return null;
    }

    /**
     * @api {POST} /QueryVideoProjectViewBaseInfo 查询视频实时预览基础信息(工程级)
     * @apiDescription 查询视频实时预览基础信息(工程级)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryVideoProjectViewBaseInfo
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [status] 视频设备状态枚举 0.全部 1.仅在线 2.仅离线（默认是0.全部）
     * @apiParam (请求体) {String} [deviceSerial] 序列号/唯一标识
     * @apiParam (请求体) {Int} [deviceChannel] 通道号
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupParentID 监测组别ID
     * @apiSuccess (返回结果) {String} dataList.monitorGroupParentName 监测组别名称
     * @apiSuccess (返回结果) {Int} dataList.displayOrder 监测组别展示顺序
     * @apiSuccess (返回结果) {Object[]} dataList.monitorGroupDataList 监测组数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorGroupID 监测分组ID
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorGroupName 监测分组名称
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.displayOrder 监测分组展示顺序
     * @apiSuccess (返回结果) {Object[]} [dataList.monitorGroupDataList.monitorPointDataList] 监测点数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.monitorItemID 监测类型ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.displayOrder 监测点展示顺序
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.monitorPointAlias 监测点别称
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.sensorAlias 传感器别称
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.deviceVideoID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceName 设备名称
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.deviceChannelNum 设备可接入的通道号数量
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.projectID 项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {Int[]} dataList.monitorGroupDataList.monitorPointDataList.deviceChannel 通道号列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryVideoProjectViewBaseInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoProjectViewBaseInfo(@Valid @RequestBody Object param) {
        //
        return null;
    }

    /**
     * @api {POST} /QueryYsVideoDeviceInfo 查询萤石视频设备信息
     * @apiDescription 查询萤石视频设备信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryYsVideoDeviceInfo
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} deviceVideoID 视频设备ID
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {Int} deviceVideoID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} deviceName 设备名称
     * @apiSuccess (返回结果) {String} deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} deviceChannelNum 设备可接入的通道号数量
     * @apiSuccess (返回结果) {Int} accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} [projectID] 项目ID
     * @apiSuccess (返回结果) {Int} storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {String} baseUrl 标清直播地址
     * @apiSuccess (返回结果) {String} [hdUrl] 高清直播地址(缺少对应能力集时,该项可能为空)
     * @apiSuccess (返回结果) {Object} capabilitySet 能力集(能力集中缺少某项时,也表示不支持该能力)
     * @apiSuccess (返回结果) {Int} capabilitySet.support_audio_onoff 是否支持声音开关设置 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.support_volumn_set 是否支持音量调节 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.support_capture 是否支持封面抓图: 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.support_talk 是否支持对讲: 0-不支持, 1-全双工, 3-半双工
     * @apiSuccess (返回结果) {Int} capabilitySet.support_mcvolumn_set 是否支持麦克风音量调节：0-不支持，1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz_focus 是否支持焦距模式 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz_top_bottom 是否支持云台上下转动 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz_left_right 是否支持云台左右转动 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz_45 是否支持云台45度方向转动 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz_zoom 是否支持云台缩放控制 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz_preset 是否支持云台预置点 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.support_rate_limit 是否支持高清码率限制 0-不支持码率限制, 1-支持高清码率限制
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryYsVideoDeviceInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryYsVideoDeviceInfo(@Valid @RequestBody Object param) {
        //@see #queryVideoMonitorPointLiveInfo(QueryVideoMonitorPointLiveInfoParam)
        return null;
    }

    /**
     * @api {POST} /QueryHikVideoDeviceInfo 查询海康视频设备信息
     * @apiDescription 查询海康视频设备信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryHikVideoDeviceInfo
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} deviceVideoID 视频设备ID
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {Int} deviceVideoID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} deviceName 设备名称
     * @apiSuccess (返回结果) {String} deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} deviceChannelNum 设备可接入的通道号数量
     * @apiSuccess (返回结果) {Int} accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} [projectID] 项目ID
     * @apiSuccess (返回结果) {Int} storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {String} baseUrl 标清直播地址
     * @apiSuccess (返回结果) {String} [hdUrl] 高清直播地址(缺少对应能力集时,该项可能为空)
     * @apiSuccess (返回结果) {Object} capabilitySet 能力集(能力集中缺少某项时,也表示不支持该能力)。海康能力集未标识音频播放能力，这里暂时默认拥有该能力
     * @apiSuccess (返回结果) {Int} capabilitySet.vss 视频能力,0-不支持, 1-支持<br>拥有该能力时，海康设备允许进行 手动抓图、语音对讲、预置点、视频质量 操作
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz 云台能力,0-不支持, 1-支持<br>拥有该能力时，海康设备允许进行 调节焦距、云台控制 操作
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryHikVideoDeviceInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryHikVideoDeviceInfo(@Valid @RequestBody Object param) {
        //
        return null;
    }


    /**
     * @api {POST} /QueryHkVideoPage (测试)查询海康视频接口信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryHkVideoPage
     * @apiDescription 查询视频基本信息
     * @apiSuccess (返回结果) {String} videoName 视频设备名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryHkVideoPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryHkVideoPage(@Valid @RequestBody Object param) {
        return hkVideoService.QueryHkVideoPage(param);
    }


    /**
     * @api {POST} /AddVideoDeviceList 批量添加视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 添加视频设备,(同步到萤石云平台,海康平台,物联网平台),如果是萤石云设备的话,要把该设备的通道信息保存到ExValue中
     * @apiName AddVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} addVideoList 新增视频设备(max = 100)
     * @apiParam (请求体) {String} addVideoList.deviceSerial 设备序列号/监控点唯一标识
     * @apiParam (请求体) {String} [addVideoList.validateCode] 设备验证码,海康设备可不传
     * @apiParam (请求体) {Byte} addVideoList.accessPlatform 接入平台:萤石云平台:0 海康平台:1
     * @apiParam (请求体) {Byte} addVideoList.accessProtocol 接入协议:萤石云协议:0 国标协议:1
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/AddVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addVideoDeviceList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QueryVideoDeviceInfo 查询单个视频设备详情
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询单个视频设备详情
     * @apiName QueryVideoDeviceInfo
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String} deviceSerial 设备序列号/监控点唯一标识
     * @apiParam (请求体) {Byte} accessPlatform 接入平台,萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {String} deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} deviceStatus 设备在线状态
     * @apiSuccess (返回结果) {Int} deviceChannelNum 设备可接入通道号的数量
     * @apiSuccess (返回结果) {Int} accessChannelNum 接入通道号的数量
     * @apiSuccess (返回结果) {Int} hkChannelNo 海康通道号(萤石云平台设备该字段返回null)
     * @apiSuccess (返回结果) {Object[]} accessChannelList 接入通道号列表(海康的话为null)
     * @apiSuccess (返回结果) {String} accessChannelList.ipcSerial ipc设备序列号
     * @apiSuccess (返回结果) {String} accessChannelList.channelNo 通道号
     * @apiSuccess (返回结果) {String} accessChannelList.channelName 接入通道名称
     * @apiSuccess (返回结果) {Object[]} sensorList 传感器列表
     * @apiSuccess (返回结果) {Int} sensorList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} sensorList.sensorName 传感器名称
     * @apiSuccess (返回结果) {Int} sensorList.captureInterval 抓拍间隔(单位分钟)
     * @apiSuccess (返回结果) {Int} sensorList.projectID 所属工程ID
     * @apiSuccess (返回结果) {Int} sensorList.channelNo 通道号
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDeviceInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDeviceInfo(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QueryVideoDeviceList 查询视频设备列表(不分页)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频设备列表(不分页)
     * @apiName QueryVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String[]} deviceSerialList 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {String} dataList.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} dataList.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} dataList.deviceName 视频设备名称
     * @apiSuccess (返回结果) {Byte} dataList.accessPlatform 接入平台
     * @apiSuccess (返回结果) {String} dataList.accessPlatformStr 接入平台名称
     * @apiSuccess (返回结果) {Int} dataList.deviceChannelNum 设备可接入通道号的数量(海康默认为1)
     * @apiSuccess (返回结果) {Int} dataList.accessChannelNum 接入通道号的数量(海康默认为1)
     * @apiSuccess (返回结果) {Object[]} dataList.accessChannelList 接入通道号列表(海康的话为null)
     * @apiSuccess (返回结果) {String} dataList.accessChannelList.ipcSerial ipc设备序列号
     * @apiSuccess (返回结果) {String} dataList.accessChannelList.channelNo 通道号
     * @apiSuccess (返回结果) {String} dataList.accessChannelList.channelName 接入通道名称
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList 传感器列表
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.sensorID] 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.sensorName 传感器名称
     * @apiSuccess (返回结果) {Int} dataList.sensorList.captureInterval 抓拍间隔(单位分钟)
     * @apiSuccess (返回结果) {Int} dataList.sensorList.projectID 所属工程ID
     * @apiSuccess (返回结果) {Int} dataList.sensorList.channelNo 通道号
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDeviceList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QueryYsVideoDeviceList 查询萤石云视频分页列表
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询萤石云视频分页列表
     * @apiName QueryYsVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} pageStart 分页起始页，从0开始
     * @apiParam (请求体) {Int} pageSize 分页大小，最大为50
     * @apiSuccess (返回结果) {Object} page 页面对象
     * @apiSuccess (返回结果) {Int} page.total 查询数据记录总数
     * @apiSuccess (返回结果) {Int} page.page 当前页
     * @apiSuccess (返回结果) {Int} page.size 每页记录总数
     * @apiSuccess (返回结果) {Object[]} data 对象
     * @apiSuccess (返回结果) {String} data.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} data.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} data.deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} data.existMd 是否在米度中台存在
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryYsVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryYsVideoDeviceList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QueryHkVideoDeviceList 查询海康视频分页列表
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询萤石云视频分页列表
     * @apiName QueryHkVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} pageNo 分页起始页，从1开始
     * @apiParam (请求体) {Int} pageSize 分页大小，最大为1000
     * @apiSuccess (返回结果) {Object[]} data 对象
     * @apiSuccess (返回结果) {String} data.total 查询数据记录总数
     * @apiSuccess (返回结果) {String} data.pageNo 当前页码
     * @apiSuccess (返回结果) {String} data.pageSize 每页记录总数
     * @apiSuccess (返回结果) {String} data.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} data.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} data.deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} data.existMd 是否在米度中台存在
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryHkVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryHkVideoDeviceList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /DeleteVideoDeviceList 批量删除视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 添加删除设备,(同步删除萤石云平台,物联网平台),海康平台设备无法删除
     * @apiName DeleteVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String[]} deviceSerialList 设备序列号/监控点唯一标识列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/DeleteVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteVideoDeviceList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /UpdateVideoDeviceList 批量更新视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量更新视频设备,(同步萤石云平台,物联网平台),海康平台设备无法更改
     * @apiName UpdateVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} updateVideoList 更新视频设备列表(max = 100)
     * @apiParam (请求体) {String} updateVideoList.deviceSerial 设备序列号
     * @apiParam (请求体) {String} updateVideoList.deviceName 设备名称
     * @apiParam (请求体) {Byte} updateVideoList.accessPlatform 接入平台
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/UpdateVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateVideoDeviceList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /SaveVideoDeviceSensorList 批量存储视频传感器
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量存储视频传感器,生成的视频传感器可以绑定工程,绑定抓拍配置
     * @apiName SaveVideoDeviceSensorList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} list 数据列表(max = 100)
     * @apiParam (请求体) {Int} list.videoDeviceID 视频设备ID
     * @apiParam (请求体) {Int} [list.projectID] 所属工程项目ID
     * @apiParam (请求体) {Object[]} list.addSensorList 新增视频传感器(max = 100)
     * @apiParam (请求体) {Int} [list.addSensorList.sensorID] 传感器ID,为空时进行新增,不为空时进行更新
     * @apiParam (请求体) {String} list.addSensorList.sensorName 传感器名称
     * @apiParam (请求体) {Int} list.addSensorList.channelCode 通道号
     * @apiParam (请求体) {Boolean} list.addSensorList.bindProject 是否绑定工程
     * @apiParam (请求体) {Int} [list.addSensorList.captureInterval] 抓拍间隔(单位:分钟)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/SaveVideoDeviceSensorList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object saveVideoDeviceSensorList(@Validated @RequestBody Object pa) {
        return null;
    }



    /**
     * @api {POST} /QueryVideoDevicePage 查询视频设备列表(分页)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量生成视频传感器
     * @apiName QueryVideoDevicePage
     * @apiParam (请求体) {Int} companyID  当前公司ID
     * @apiParam (请求体) {String} deviceSerial 设备序列号
     * @apiParam (请求体) {Int} [ownedCompanyID] 所属公司ID,null查全部
     * @apiParam (请求体) {Int} [projectID] 项目ID,null查全部
     * @apiParam (请求体) {Boolean} [deviceStatus] 视频设备在线状态,null查全部
     * @apiParam (请求体) {Boolean} [allocationStatus] 分配状态,null查全部
     * @apiParam (请求体) {Boolean} [captureStatus] 配置状态,null查全部
     * @apiParam (请求体) {DateTime} [begin] 开始时间
     * @apiParam (请求体) {DateTime} [end] 结束时间
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {String} currentPageData.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} currentPageData.deviceType 视频设备类型
     * @apiSuccess (返回结果) {Int} currentPageData.companyID 公司ID
     * @apiSuccess (返回结果) {String} currentPageData.companyName 公司名称
     * @apiSuccess (返回结果) {Boolean} currentPageData.deviceStatus 设备在线状态
     * @apiSuccess (返回结果) {Int} currentPageData.accessChannelNum 接入通道号数量
     * @apiSuccess (返回结果) {Int} currentPageData.sensorNum 传感器(视频)数量
     * @apiSuccess (返回结果) {Byte} currentPageData.accessPlatform 接入平台,萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {String} currentPageData.accessPlatformStr 接入平台名称
     * @apiSuccess (返回结果) {Byte} currentPageData.accessProtocol 接入协议,萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {String} currentPageData.accessProtocolStr 接入协议名称
     * @apiSuccess (返回结果) {Int} currentPageData.projectID 所属项目ID
     * @apiSuccess (返回结果) {String} currentPageData.projectName 所属项目名称
     * @apiSuccess (返回结果) {Boolean} currentPageData.captureStatus 配置抓拍状态
     * @apiSuccess (返回结果) {Boolean} currentPageData.allocationStatus 设备分配所属工程状态
     * @apiSuccess (返回结果) {Int} currentPageData.createUserID 创建用户ID
     * @apiSuccess (返回结果) {Date} currentPageData.createTime 创建时间
     * @apiSuccess (返回结果) {Int} currentPageData.updateUserID 最后更新用户ID
     * @apiSuccess (返回结果) {Date} currentPageData.updateTime 最后更新时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
//    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDevicePage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDevicePage(@Validated @RequestBody Object pa) {
        return null;
    }


}
