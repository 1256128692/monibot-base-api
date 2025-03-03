package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.interceptor.CurrentSubjectFilter;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
import cn.shmedo.monitor.monibotbaseapi.service.IDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoDeviceService;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VideoController {
    private final VideoService videoService;
    private final ITbVideoDeviceService tbVideoDeviceService;

    private final IDeviceService deviceService;

    /**
     * @api {POST} /QueryVideoMonitorPointLiveInfo 查询视频类型监测点直播地址信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频类型监测点直播地址信息,可查看萤石云或者海康的直播地址
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
     * @api {POST} /PanControlCompanyVideoPoint 云台控制监测点视频设备(企业级)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 云台控制监测点视频设备的摄像头移动, 有(上下左右, 焦距)等操作
     * @apiName PanControlCompanyVideoPoint
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} videoDeviceID 视频设备ID
     * @apiParam (请求体) {Int} deviceChannel 通道号
     * @apiParam (请求体) {Int} direction 方向 0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/PanControlCompanyVideoPoint", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object panControlCompanyVideoPoint(@Validated @RequestBody PanControlCompanyVideoPointParam pa) {
        return videoService.panControlCompanyVideoPoint(pa);
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
     * @apiDeprecated use now (#视频模块:QueryVideoDeviceListV2).
     * @api {POST} /QueryVideoCompanyViewBaseInfo 查询视频实时预览基础信息(企业级)
     * @apiDescription 查询视频实时预览基础信息(企业级)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryVideoCompanyViewBaseInfo
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [status] 视频设备状态枚举 0.全部 1.仅在线 2.仅离线（默认是0.全部）
     * @apiParam (请求体) {String} [deviceSerial] 序列号/唯一标识
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} dataList.deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} dataList.deviceName 设备名称
     * @apiSuccess (返回结果) {String} dataList.deviceType 设备类型/型号
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
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryVideoCompanyViewBaseInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoCompanyViewBaseInfo(@Valid @RequestBody QueryVideoCompanyViewBaseInfoParam param) {
        return this.tbVideoDeviceService.queryVideoCompanyViewBaseInfo(param);
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
     * @apiParam (请求体) {String} [queryCode] 模糊匹配,可模糊匹配监测点组名称、监测点名称
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
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.displayOrder 监测点展示顺序
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.sensorAlias 传感器别称
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceName 设备名称
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.projectID 项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {Object[]} dataList.monitorGroupDataList.monitorPointDataList.videoSourceInfoList 通道视频列表
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.videoSourceInfoList.videoDeviceSourceID 通道视频ID
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupDataList.monitorPointDataList.videoSourceInfoList.channelCode 通道号
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryVideoProjectViewBaseInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoProjectViewBaseInfo(@Valid @RequestBody QueryVideoProjectViewBaseInfo param) {
        return tbVideoDeviceService.queryVideoProjectViewBaseInfo(param);
    }

    /**
     * @api {POST} /QueryVideoProjectViewBaseInfoV2 查询视频实时预览基础信息(工程级)
     * @apiDescription 查询视频实时预览基础信息(工程级)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryVideoProjectViewBaseInfoV2
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [status] 视频设备状态枚举 0.全部 1.仅在线 2.仅离线（默认是0.全部）
     * @apiParam (请求体) {String} [deviceSerial] 序列号/唯一标识
     * @apiParam (请求体) {String} [queryCode] 模糊匹配,可模糊匹配监测点组名称、监测点名称
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Object[]} dataList 监测组数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupID 监测分组ID
     * @apiSuccess (返回结果) {String} dataList.monitorGroupName 监测分组名称
     * @apiSuccess (返回结果) {Int} dataList.displayOrder 监测分组展示顺序
     * @apiSuccess (返回结果) {Object[]} [dataList.monitorPointDataList] 监测点数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.displayOrder 监测点展示顺序
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.sensorAlias 传感器别称
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} dataList.monitorPointDataList.deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.deviceName 设备名称
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.projectID 项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} dataList.monitorPointDataList.captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} dataList.monitorPointDataList.allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {Object[]} dataList.monitorPointDataList.videoSourceInfoList 通道视频列表
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.videoSourceInfoList.videoDeviceSourceID 通道视频ID
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.videoSourceInfoList.channelCode 通道号
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryVideoProjectViewBaseInfoV2", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoProjectViewBaseInfoV2(@Valid @RequestBody QueryVideoProjectViewBaseInfo param) {
        return tbVideoDeviceService.queryVideoProjectViewBaseInfoV2(param);
    }

    /**
     * @api {POST} /QueryYsVideoDeviceInfo 查询萤石视频设备信息
     * @apiDescription 查询萤石视频设备信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryYsVideoDeviceInfo
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [videoDeviceID] 视频设备ID (videoDeviceID和deviceChannel)、sensorID、monitorPointID这三组数据有且仅有一组不为空,用于确定对应萤石摄像头。
     * @apiParam (请求体) {Int} [deviceChannel] 通道号
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {Int} videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} deviceName 设备名称
     * @apiSuccess (返回结果) {String} deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} [projectID] 项目ID
     * @apiSuccess (返回结果) {Int} storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {String} ysToken 萤石token
     * @apiSuccess (返回结果) {String} baseUrl 标清直播地址
     * @apiSuccess (返回结果) {String} [hdUrl] 高清直播地址(缺少对应能力集时,该项可能为空)
     * @apiSuccess (返回结果) {Object} capabilitySet 能力集(能力集中缺少某项时,也表示不支持该能力)
     * @apiSuccess (返回结果) {Int} capabilitySet.supportAudioOnoff 是否支持声音开关设置 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.supportVolumnSet 是否支持音量调节 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.supportCapture 是否支持封面抓图: 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.supportTalk 是否支持对讲: 0-不支持, 1-全双工, 3-半双工
     * @apiSuccess (返回结果) {Int} capabilitySet.supportMcvolumnSet 是否支持麦克风音量调节：0-不支持，1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptzFocus 是否支持焦距模式 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptzTopBottom 是否支持云台上下转动 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptzLeftRight 是否支持云台左右转动 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz45 是否支持云台45度方向转动 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptzZoom 是否支持云台缩放控制 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.ptzPreset 是否支持云台预置点 0-不支持, 1-支持
     * @apiSuccess (返回结果) {Int} capabilitySet.supportRateLimit 是否支持高清码率限制 0-不支持码率限制, 1-支持高清码率限制
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryYsVideoDeviceInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryYsVideoDeviceInfo(@Valid @RequestBody QueryYsVideoDeviceInfoParam param) {
        return this.videoService.queryYsVideoDeviceInfo(param);
    }

    /**
     * @api {POST} /QueryHikVideoDeviceInfo 查询海康视频设备信息
     * @apiDescription 查询海康视频设备信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryHikVideoDeviceInfo
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [videoDeviceID] 视频设备ID,videoDeviceID、sensorID、monitorPointID有且仅有一个不为空,用于确定对应海康摄像头
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求体) {Int} [streamType] 码流类型，0.主码流 1.子码流 2.第三码流 (默认为0.主码流)
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {Int} videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Boolean} deviceStatus 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {String} deviceName 设备名称
     * @apiSuccess (返回结果) {String} deviceType 设备类型/型号
     * @apiSuccess (返回结果) {Int} accessChannelNum 接入的通道号数量
     * @apiSuccess (返回结果) {Int} accessPlatform 平台:萤石云平台:0 海康平台:1
     * @apiSuccess (返回结果) {Int} accessProtocol 协议:萤石云协议:0 , 国标协议:1
     * @apiSuccess (返回结果) {Int} companyID 注册公司ID
     * @apiSuccess (返回结果) {Int} [projectID] 项目ID
     * @apiSuccess (返回结果) {Int} storageType 存储类型 本地:0 云端:1 (暂时不用)
     * @apiSuccess (返回结果) {Boolean} captureStatus 设备配置抓拍 true:1 false:0
     * @apiSuccess (返回结果) {Boolean} allocationStatus 设备分配状态 true:1 false:0
     * @apiSuccess (返回结果) {String} baseUrl 直播地址
     * @apiSuccess (返回结果) {Object} capabilitySet 能力集(能力集中缺少某项时,也表示不支持该能力)。海康能力集未标识音频播放能力，这里暂时默认拥有该能力
     * @apiSuccess (返回结果) {Int} capabilitySet.vss 视频能力,0-不支持, 1-支持<br>拥有该能力时，海康设备允许进行 手动抓图、语音对讲、预置点、视频质量 操作
     * @apiSuccess (返回结果) {Int} capabilitySet.ptz 云台能力,0-不支持, 1-支持<br>拥有该能力时，海康设备允许进行 调节焦距、云台控制 操作
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryHikVideoDeviceInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryHikVideoDeviceInfo(@Valid @RequestBody QueryHikVideoDeviceInfoParam param) {
        return tbVideoDeviceService.queryHikVideoDeviceInfo(param);
    }

    /**
     * @api {POST} /QueryYsVideoPlayBack 萤石设备视频回放
     * @apiDescription 萤石设备视频回放
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryYsVideoPlayBack
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} recordLocation 存储类型 0.中心存储 1.设备存储
     * @apiParam (请求体) {DateTime} beginTime 开始时间,云存储开始结束时间必须在同一天
     * @apiParam (请求体) {Int} [videoDeviceID] 视频设备ID (videoDeviceID和deviceChannel)、sensorID、monitorPointID这三组数据有且仅有一组不为空,用于确定对应萤石摄像头。
     * @apiParam (请求体) {Int} [deviceChannel] 通道号
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {String} baseUrl 回放地址
     * @apiSuccess (返回结果) {String} ysToken 萤石token
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryYsVideoPlayBack", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryYsVideoPlayBack(@Valid @RequestBody QueryYsVideoPlayBackParam param) {
        return this.videoService.queryYsVideoPlayBack(param);
    }

    /**
     * @api {POST} /QueryHikVideoPlayBack 海康设备视频回放
     * @apiDescription 海康设备视频回放。<br>海康设备视频如果存储在设备里，会将设备截取成多段进行"分页"，每次查询"下一页"时需要传入"当前页"返回的uuid
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryHikVideoPlayBack
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} recordLocation 存储类型 0.中心存储 1.设备存储
     * @apiParam (请求体) {DateTime} beginTime 开始时间,开始时间和结束时间相差不超过3天
     * @apiParam (请求体) {DateTime} endTime 结束时间,开始时间和结束时间相差不超过3天
     * @apiParam (请求体) {Int} [videoDeviceID] 视频设备ID,videoDeviceID、sensorID、monitorPointID有且仅有一个不为空,用于确定对应海康摄像头
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求体) {String} [uuid] uuid,用于继续查询剩余片段
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {String} [uuid] uuid,用于继续查询剩余片段
     * @apiSuccess (返回结果) {String} [baseUrl] 回放地址,如果为空代表海康接口未返回回放地址
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryHikVideoPlayBack", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryHikVideoPlayBack(@Valid @RequestBody QueryHikVideoPlayBackParam param) {
        return tbVideoDeviceService.queryHikVideoPlayBack(param);
    }

    /**
     * @api {POST} /QueryHikVideoTalk 海康设备语音对讲取流
     * @apiDescription 海康设备语音对讲取流
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiName QueryHikVideoTalk
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [videoDeviceID] 视频设备ID,videoDeviceID、sensorID、monitorPointID有且仅有一个不为空,用于确定对应海康摄像头
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {Int} [monitorPointID] 监测点ID
     * @apiParam (请求体) {Int} [projectID] 工程ID,若该项不为空则为项目权限
     * @apiSuccess (返回结果) {String} baseUrl 语音对讲地址
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @PostMapping(value = "/QueryHikVideoTalk", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryHikVideoTalk(@Valid @RequestBody QueryHikVideoTalkParam param) {
        return tbVideoDeviceService.queryHikVideoTalk(param);
    }

    /**
     * @api {POST} /AddVideoDeviceList 批量添加视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 添加视频设备, (同步到萤石云平台, 海康平台, 物联网平台), 如果是萤石云设备的话, 要把该设备的通道信息保存到ExValue中
     * @apiName AddVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} addVideoList 新增视频设备(max = 100)
     * @apiParam (请求体) {String} addVideoList.deviceSerial 设备序列号/监控点唯一标识
     * @apiParam (请求体) {String} [addVideoList.validateCode] 设备验证码,海康设备可不传
     * @apiParam (请求体) {Byte} addVideoList.accessPlatform 接入平台:萤石云平台:0 海康平台:1
     * @apiParam (请求体) {Byte} addVideoList.accessProtocol 接入协议:萤石云协议:0 国标协议:1
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddVideoDevice
     */
    @Permission(permissionName = "mdmbase:AddVideoDevice")
    @RequestMapping(value = "/AddVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addVideoDeviceList(@Validated @RequestBody AddVideoDeviceListParam pa) {
        return videoService.addVideoDeviceList(pa);
    }


    /**
     * @api {POST} /QueryVideoDeviceList 查询视频设备列表(不分页)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频设备列表(不分页)
     * @apiName QueryVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String[]} [deviceSerialList] 设备序列号/监控点唯一标识,null查询该公司下全部
     * @apiParam (请求体) {Boolean} [deviceStatus]  设备在线状态
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Int} dataList.companyID 视频设备ID
     * @apiSuccess (返回结果) {String} dataList.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} dataList.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} dataList.deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} dataList.deviceStatus  设备在线状态
     * @apiSuccess (返回结果) {Byte} dataList.accessPlatform 接入平台
     * @apiSuccess (返回结果) {String} dataList.accessPlatformStr 接入平台名称
     * @apiSuccess (返回结果) {Int} dataList.deviceChannelNum 设备接入平台通道号的数量(海康默认为1)
     * @apiSuccess (返回结果) {Int} dataList.accessChannelNum 可接入通道号的总量(海康默认为1)
     * @apiSuccess (返回结果) {DateTime} dataList.createTime 创建时间（接入时间）
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList 传感器列表
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.sensorID] 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.sensorName 传感器名称
     * @apiSuccess (返回结果) {Boolean} [dataList.sensorList.sensorEnable] 传感器是否开启
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.projectID] 所属工程ID
     * @apiSuccess (返回结果) {Object[]} dataList.videoDeviceCaptureList 视频通道信息列表
     * @apiSuccess (返回结果) {Int} dataList.videoDeviceCaptureList.channelNo 通道号
     * @apiSuccess (返回结果) {Int} dataList.videoDeviceCaptureList.enable 通道是否启用
     * @apiSuccess (返回结果) {Boolean} [dataList.videoDeviceCaptureList.imageCapture] 抓拍是否开启
     * @apiSuccess (返回结果) {Int} [dataList.videoDeviceCaptureList.captureInterval] 抓拍间隔(单位分钟)
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDeviceList(@Validated @RequestBody QueryVideoDeviceListParam pa) {
        return videoService.queryVideoDeviceList(pa);
    }


    /**
     * @api {POST} /QueryVideoDeviceListV1 查询视频设备列表(不分页-新版)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频设备列表(不分页 - 新版)
     * @apiName QueryVideoDeviceListV1
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String[]} [deviceSerialList] 设备序列号/监控点唯一标识,null查询该公司下全部
     * @apiParam (请求体) {Boolean} [deviceStatus]  设备在线状态
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Int} dataList.companyID 公司ID
     * @apiSuccess (返回结果) {String} dataList.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} dataList.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} dataList.deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} dataList.deviceStatus  设备在线状态
     * @apiSuccess (返回结果) {Byte} dataList.accessPlatform 接入平台
     * @apiSuccess (返回结果) {String} dataList.accessPlatformStr 接入平台名称
     * @apiSuccess (返回结果) {Int} dataList.deviceChannelNum 设备接入平台通道号的数量(海康默认为1)
     * @apiSuccess (返回结果) {Int} dataList.accessChannelNum 可接入通道号的总量(海康默认为1)
     * @apiSuccess (返回结果) {DateTime} dataList.createTime 创建时间（接入时间）
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList 通道视频列表
     * @apiSuccess (返回结果) {Int} dataList.sensorList.channelNo 通道号
     * @apiSuccess (返回结果) {Boolean} dataList.sensorList.enable 通道是否启用
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.sensorID] 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.sensorName 传感器名称
     * @apiSuccess (返回结果) {Boolean} [dataList.sensorList.sensorEnable] 传感器是否开启
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.projectID] 所属工程ID
     * @apiSuccess (返回结果) {Boolean} [dataList.sensorList.imageCapture] 抓拍是否开启
     * @apiSuccess (返回结果) {Int} [dataList.sensorList.captureInterval] 抓拍间隔(单位分钟)
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDeviceListV1", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDeviceListV1(@Validated @RequestBody QueryVideoDeviceListParam pa) {
        return videoService.queryVideoDeviceListV1(pa);
    }

    /**
     * @api {POST} /QueryVideoDeviceListV2 查询监测工程下视频设备列表
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询监测工程下视频设备列表
     * @apiName QueryVideoDeviceListV2
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Boolean} [deviceStatus]  设备在线状态
     * @apiParam (请求体) {String} [queryContent]  超级搜索,设备序列号或者名称
     * @apiSuccess (返回结果) {Object[]} dataList 工程下视频设备信息列表
     * @apiSuccess (返回结果) {Int} dataList.projectID 工程ID
     * @apiSuccess (返回结果) {String} dataList.projectName 工程名称
     * @apiSuccess (返回结果) {Object[]} dataList.videoInfoList 工程下视频设备信息列表
     * @apiSuccess (返回结果) {Int} dataList.videoInfoList.videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {String} dataList.videoInfoList.accessPlatformStr 平台
     * @apiSuccess (返回结果) {String} dataList.videoInfoList.deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} dataList.videoInfoList.deviceStatus  设备在线状态
     * @apiSuccess (返回结果) {Object[]} dataList.videoInfoList.videoSourceInfoList 通道视频列表
     * @apiSuccess (返回结果) {Int} dataList.videoInfoList.videoSourceInfoList.videoDeviceSourceID 通道视频ID
     * @apiSuccess (返回结果) {Int} dataList.videoInfoList.videoSourceInfoList.channelCode 通道号
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDeviceListV2", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDeviceListV2(@Validated @RequestBody QueryVideoDeviceListParam pa) {
        return videoService.queryVideoDeviceListV2(pa);
    }


    /**
     * @api {POST} /QueryYsVideoDeviceList 查询萤石云视频分页列表
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询萤石云视频分页列表
     * @apiName QueryYsVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} currentPage 当前页，从1开始
     * @apiParam (请求体) {Int} pageSize 页大小，最大为20
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 对象
     * @apiSuccess (返回结果) {String} currentPageData.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} currentPageData.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} currentPageData.deviceName 视频设备名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryYsVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryYsVideoDeviceList(@Validated @RequestBody QueryYsVideoDeviceParam pa) {
        return videoService.queryYsVideoDeviceList(pa);
    }


    /**
     * @api {POST} /QueryHkVideoDeviceList 查询海康视频分页列表
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询萤石云视频分页列表
     * @apiName QueryHkVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} currentPage 当前页，从1开始
     * @apiParam (请求体) {Int} pageSize 页大小，最大为20
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {String} currentPageData.deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} currentPageData.deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} currentPageData.deviceName 视频设备名称
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryHkVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryHkVideoDeviceList(@Validated @RequestBody QueryHkVideoDeviceParam pa) {
        return videoService.queryHkVideoDeviceList(pa);
    }

    /**
     * @api {POST} /QueryHkVideoDeviceBaseInfo 查询海康视频设备基本信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询海康视频设备基本信息, 调用海康api服务接口
     * @apiName QueryHkVideoDeviceBaseInfo
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String} deviceSerial 海康的设备序列号
     * @apiSuccess (返回结果) {String} deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} deviceName 视频设备名称
     * @apiSuccess (返回结果) {Int} deviceChannelNum 设备可接入通道号的数量(海康默认为1)
     * @apiSuccess (返回结果) {Int} accessChannelNum 接入通道号的数量(海康默认为1)
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryHkVideoDeviceBaseInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryHkVideoDeviceBaseInfo(@Validated @RequestBody QueryHkVideoDeviceBaseInfoParam pa) {
        return videoService.queryHkVideoDeviceBaseInfo(pa);
    }


    /**
     * @api {POST} /DeleteVideoDeviceList 批量删除视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 添加删除设备, (同步删除萤石云平台, 物联网平台), 海康平台设备无法删除
     * @apiName DeleteVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String[]} deviceSerialList 设备序列号/监控点唯一标识列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DeleteVideoDevice
     */
    @Permission(permissionName = "mdmbase:DeleteVideoDevice")
    @RequestMapping(value = "/DeleteVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteVideoDeviceList(@Validated @RequestBody DeleteVideoDeviceParam pa, HttpServletRequest request) {
        return videoService.deleteVideoDeviceList(pa, request.getHeader(CurrentSubjectFilter.TOKEN_HEADER), CurrentSubjectHolder.getCurrentSubject());
    }


    /**
     * @api {POST} /UpdateVideoDeviceList 批量更新视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量更新视频设备, (同步萤石云平台, 物联网平台), 海康平台设备无法更改
     * @apiName UpdateVideoDeviceList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} updateVideoList 更新视频设备列表(max = 100)
     * @apiParam (请求体) {String} updateVideoList.deviceSerial 设备序列号
     * @apiParam (请求体) {String} updateVideoList.deviceName 设备名称
     * @apiParam (请求体) {Byte} updateVideoList.accessPlatform 接入平台
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateVideoDevice
     */
    @Permission(permissionName = "mdmbase:UpdateVideoDevice")
    @RequestMapping(value = "/UpdateVideoDeviceList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateVideoDeviceList(@Validated @RequestBody UpdateVideoDeviceParam pa) {
        return videoService.updateVideoDeviceList(pa);
    }


    /**
     * @api {POST} /SaveVideoDeviceSensorList 批量存储视频传感器
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量存储视频传感器, 生成的视频传感器可以绑定工程, 绑定抓拍配置
     * @apiName SaveVideoDeviceSensorList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} list 数据列表(max = 100)
     * @apiParam (请求体) {Int} list.videoDeviceID 视频设备ID
     * @apiParam (请求体) {String} list.deviceSerial 设备序列号
     * @apiParam (请求体) {Int} [list.projectID] 所属工程项目ID
     * @apiParam (请求体) {Object[]} list.addSensorList 新增视频传感器(max = 100)
     * @apiParam (请求体) {Int} [list.addSensorList.sensorID] 传感器ID,为空时进行新增,不为空时进行更新
     * @apiParam (请求体) {String} list.addSensorList.sensorName 传感器名称
     * @apiParam (请求体) {Boolean} [list.addSensorList.sensorEnable] 传感器是否启用
     * @apiParam (请求体) {Int} list.addSensorList.channelCode 通道号
     * @apiParam (请求体) {Boolean} list.addSensorList.imageCapture 是否开启抓拍
     * @apiParam (请求体) {Int} list.addSensorList.captureInterval 抓拍间隔(单位:分钟)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateVideoDevice
     */
    @Permission(permissionName = "mdmbase:UpdateVideoDevice")
    @RequestMapping(value = "/SaveVideoDeviceSensorList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object saveVideoDeviceSensorList(@Validated @RequestBody SaveVideoDeviceSensorParam pa) {
        return videoService.saveVideoDeviceSensorList(pa);
    }

    /**
     * @api {POST} /SaveVideoCaptureList 批量存储视频抓拍列表(新)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量存储视频抓拍列表
     * @apiName SaveVideoCaptureList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} list 数据列表(max = 100)
     * @apiParam (请求体) {String} list.deviceSerial 设备序列号
     * @apiParam (请求体) {Int} list.videoDeviceSourceID 通道视频设备ID
     * @apiParam (请求体) {Boolean} list.imageCapture 是否开启抓拍
     * @apiParam (请求体) {Int} list.captureInterval 抓拍间隔(单位:分钟)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateVideoDevice
     */
    @Permission(permissionName = "mdmbase:UpdateVideoDevice")
    @RequestMapping(value = "/SaveVideoCaptureList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object saveVideoCaptureList(@Validated @RequestBody SaveVideoDeviceCaptureParam pa) {
        return videoService.saveVideoDeviceCaptureList(pa);
    }


    /**
     * @api {POST} /SaveVideoSensorList 批量存储视频传感器列表(新)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量存储视频传感器列表
     * @apiName SaveVideoSensorList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Object[]} list 数据列表(max = 100)
     * @apiParam (请求体) {Int} list.videoDeviceID 视频设备ID
     * @apiParam (请求体) {String} list.deviceSerial 设备序列号
     * @apiParam (请求体) {Int} [list.projectID] 所属工程项目ID
     * @apiParam (请求体) {Object[]} list.addSensorList 新增视频传感器(max = 100)
     * @apiParam (请求体) {Int} [list.addSensorList.sensorID] 传感器ID,为空时进行新增,不为空时进行更新
     * @apiParam (请求体) {String} list.addSensorList.sensorName 传感器名称
     * @apiParam (请求体) {Boolean} [list.addSensorList.sensorEnable] 传感器是否启用
     * @apiParam (请求体) {Int} list.addSensorList.videoDeviceSourceID 通道视频设备ID
     * @apiParam (请求体) {Int} list.addSensorList.channelCode 通道号
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateVideoDevice
     */
    @Permission(permissionName = "mdmbase:UpdateVideoDevice")
    @RequestMapping(value = "/SaveVideoSensorList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object saveVideoSensorList(@Validated @RequestBody SaveVideoDeviceSensorParam pa) {
        return videoService.saveVideoSensorList(pa);
    }


    /**
     * @api {POST} /QueryVideoDevicePage 查询视频设备列表(分页)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量生成视频传感器
     * @apiName QueryVideoDevicePage
     * @apiParam (请求体) {Int} companyID  当前公司ID
     * @apiParam (请求体) {String} [deviceSerial] 设备序列号,模糊查询
     * @apiParam (请求体) {String} [fuzzyItem] 模糊查询项,可匹配序列号/标识，型号/类型
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
     * @apiSuccess (返回结果) {String} currentPageData.deviceName 视频设备名称
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
     * @apiSuccess (返回结果) {Int[]} currentPageData.channelNoList 视频设备通道号列表
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryVideoDevicePage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDevicePage(@Validated @RequestBody QueryVideoDevicePageParam pa) {
        return videoService.queryVideoDevicePage(pa);
    }


    /**
     * @api {POST} /QueryCapturePage 查询抓拍列表(分页)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询抓拍列表
     * @apiName QueryCapturePage
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} videoDeviceSourceID 通道视频ID
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {DateTime} [begin] 开始时间
     * @apiParam (请求体) {DateTime} [end] 结束时间
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.id 图片ID
     * @apiSuccess (返回结果) {Int} currentPageData.sensorID 传感器ID
     * @apiSuccess (返回结果) {Date} currentPageData.uploadTime 上传时间
     * @apiSuccess (返回结果) {String} currentPageData.path 图片地址
     * @apiSuccess (返回结果) {String} currentPageData.fileType 图片类型
     * @apiSuccess (返回结果) {Int} currentPageData.fileSize 图片大小
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryCapturePage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryCapturePage(@Validated @RequestBody QueryCapturePageParam pa) {
        return videoService.queryCapturePage(pa);
    }


    /**
     * @api {POST} /QueryCaptureList 查询抓拍列表(不分页)
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询抓拍列表
     * @apiName QueryCaptureList
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} videoDeviceSourceID 通道视频ID
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {DateTime} [begin] 开始时间
     * @apiParam (请求体) {DateTime} [end] 结束时间
     * @apiSuccess (返回结果) {Object[]} dataList 当前页数据
     * @apiSuccess (返回结果) {Int} dataList.id 图片ID
     * @apiSuccess (返回结果) {Int} dataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {Date} dataList.uploadTime 上传时间
     * @apiSuccess (返回结果) {String} dataList.path 图片地址
     * @apiSuccess (返回结果) {String} dataList.fileType 图片类型
     * @apiSuccess (返回结果) {Int} dataList.fileSize 图片大小
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryCaptureList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryCaptureList(@Validated @RequestBody QueryCaptureParam pa) {
        return videoService.queryCaptureList(pa);
    }

    /**
     * @api {POST} /QueryCaptureDate 查询抓拍数据时间
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询抓拍列表
     * @apiName QueryCaptureDate
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {Int} videoDeviceSourceID 通道视频ID
     * @apiSuccess (返回结果) {Object[]} dataList 当前页数据
     * @apiSuccess (返回结果) {Date} dataList.uploadTime 上传时间
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseVideoDevice
     */
    @Permission(permissionName = "mdmbase:ListBaseVideoDevice")
    @RequestMapping(value = "/QueryCaptureDate", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryCaptureDate(@Validated @RequestBody QueryCaptureParam pa) {
        return videoService.queryCaptureDate(pa);
    }


    /**
     * @api {POST} /QueryVideoDeviceDetail 查询视频设备详情
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频设备详情
     * @apiName QueryVideoDeviceDetail
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiParam (请求体) {String} deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {Int} videoDeviceID 视频设备ID
     * @apiSuccess (返回结果) {Int} companyID 视频设备ID
     * @apiSuccess (返回结果) {String} deviceSerial 设备序列号/监控点唯一标识
     * @apiSuccess (返回结果) {String} deviceType 视频设备类型
     * @apiSuccess (返回结果) {String} deviceName 视频设备名称
     * @apiSuccess (返回结果) {Boolean} deviceStatus  设备在线状态
     * @apiSuccess (返回结果) {Byte} accessPlatform 接入平台
     * @apiSuccess (返回结果) {String} accessPlatformStr 接入平台名称
     * @apiSuccess (返回结果) {Int} deviceChannelNum 设备接入平台通道号的数量(海康默认为1)
     * @apiSuccess (返回结果) {Int} accessChannelNum 可接入通道号的总量(海康默认为1)
     * @apiSuccess (返回结果) {DateTime} createTime 创建时间（接入时间）
     * @apiSuccess (返回结果) {Object[]} videoDeviceSourceList 通道视频列表
     * @apiSuccess (返回结果) {Int} videoDeviceSourceList.videoDeviceSourceID 通道视频ID
     * @apiSuccess (返回结果) {Int} videoDeviceSourceList.channelNo 通道号
     * @apiSuccess (返回结果) {Boolean} videoDeviceSourceList.enable 通道是否启用
     * @apiSuccess (返回结果) {Int} [videoDeviceSourceList.sensorID] 传感器ID
     * @apiSuccess (返回结果) {String} videoDeviceSourceList.sensorName 传感器名称
     * @apiSuccess (返回结果) {Boolean} [videoDeviceSourceList.sensorEnable] 传感器是否开启
     * @apiSuccess (返回结果) {Int} [videoDeviceSourceList.projectID] 所属工程ID
     * @apiSuccess (返回结果) {Int} [videoDeviceSourceList.monitorPointID] 监测点ID
     * @apiSuccess (返回结果) {Int} [videoDeviceSourceList.monitorItemID] 监测项目ID
     * @apiSuccess (返回结果) {String} [videoDeviceSourceList.monitorPointName] 监测点名称
     * @apiSuccess (返回结果) {String} [videoDeviceSourceList.monitorItemName] 监测项目名称
     * @apiSuccess (返回结果) {String} [videoDeviceSourceList.gpsLocation] 监测点位置
     * @apiSuccess (返回结果) {String} [videoDeviceSourceList.location] 行政区划
     * @apiSuccess (返回结果) {Json} [location] 位置信息
     * @apiSuccess (返回结果) {String} location.address 地址
     * @apiSuccess (返回结果) {String} [location.locationJson] 位置扩展，json字符串
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseVideo
     */
    @Permission(permissionName = "mdmbase:DescribeBaseVideo")
    @RequestMapping(value = "/QueryVideoDeviceDetail", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoDeviceDetail(@Validated @RequestBody QueryVideoDeviceDetailParam pa) {
        return videoService.QueryVideoDeviceDetail(pa);
    }


    /**
     * @api {POST} /BatchUpdateVideoDeviceStatus 批量更新视频设备在线离线状态
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量更新视频设备在线离线状态
     * @apiName BatchUpdateVideoDeviceStatus
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiSuccess (返回结果) {Boolean} data 数据
     * @apiSampleRequest off
     * @apiPermission 系统权限+应用权限 mdmbase:UpdateVideoDevice
     */
    @Permission(permissionName = "mdmbase:UpdateVideoDevice", allowApplication = true)
    @RequestMapping(value = "/BatchUpdateVideoDeviceStatus", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object batchUpdateVideoDeviceStatus(@Validated @RequestBody BatchUpdateVideoDeviceStatusParam pa) {
        return videoService.batchUpdateVideoDeviceStatus(pa);
    }


    /**
     * @api {POST} /BatchHandlerIotDeviceStatusChange 批量处理Iot设备在线离线状态变化
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 批量处理Iot设备在线离线状态变化, 设备由在线转变为离线后, 发送预警通知
     * @apiName BatchHandlerIotDeviceStatusChange
     * @apiParam (请求体) {Int} companyID  公司ID
     * @apiSuccess (返回结果) {Boolean} data 数据
     * @apiSampleRequest off
     * @apiPermission 系统权限+应用权限 mdmbase:UpdateVideoDevice
     */
    @Permission(permissionName = "mdmbase:UpdateVideoDevice", allowApplication = true)
    @RequestMapping(value = "/BatchHandlerIotDeviceStatusChange", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object batchHandlerIotDeviceStatusChange(@Validated @RequestBody BatchUpdateVideoDeviceStatusParam pa) {
        return deviceService.batchHandlerIotDeviceStatusChange(pa);
    }

}
