package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.*;
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
     * @apiDescription 查询视频类型监测点历史时间段的直播地址信息,该接口会根据监测点下的对应的视频设备,去分析是萤石云还是海康的,返回一个地址
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
     * @apiDescription 云台控制监测点视频设备的摄像头移动,有(上下左右,焦距)等操作
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
     * @apiDescription 查询视频类型监测点图像信息,开始时间与结束时间不能超过一周
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
     * @apiSuccess (返回结果) {Boolean} dataList.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {Int[]} dataList.directionList 支持的方向List,只有该方向List含有对应枚举值时才能进行相应操作。<br>枚举值定义: 0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制
     * @apiSuccess (返回结果) {Int[]} dataList.deviceChannel 通道号
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
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupDataList.monitorPointDataList.online 在线状态 在线:true 离线:false
     * @apiSuccess (返回结果) {String} dataList.monitorGroupDataList.monitorPointDataList.deviceSerial 视频设备序列号/唯一标识
     * @apiSuccess (返回结果) {Int[]} dataList.monitorGroupDataList.monitorPointDataList.directionList 支持的方向List,只有该方向List含有对应枚举值时才能进行相应操作。<br>枚举值定义: 0-上，1-下，2-左，3-右，4-左上，5-左下，6-右上，7-右下，8-放大，9-缩小，10-近焦距，11-远焦距，16-自动控制
     * @apiSuccess (返回结果) {Int[]} dataList.monitorGroupDataList.monitorPointDataList.deviceChannel 通道号列表
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryVideoProjectViewBaseInfo", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryVideoProjectViewBaseInfo(@Valid @RequestBody Object param) {
        //
        return null;
    }
}
