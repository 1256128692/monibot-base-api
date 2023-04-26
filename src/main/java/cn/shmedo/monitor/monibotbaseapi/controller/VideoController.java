package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class VideoController {

    /**
     * @api {POST} /QueryVideoMonitorPointLiveInfo 查询视频类型监测点直播地址信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频类型监测点直播地址信息
     * @apiName QueryVideoMonitorPointLiveInfo
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList  监测点ID列表
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorType":"33"}
     * @apiSuccess (返回结果) {Int} sensorID  传感器ID
     * @apiSuccess (返回结果) {String} sensorName  传感器名称
     * @apiSuccess (返回结果) {Int} monitorPointID   监测点ID
     * @apiSuccess (返回结果) {String} monitorPointName   监测点名称
     * @apiSuccess (返回结果) {Int} monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemAlias   监测项目别名
     * @apiSuccess (返回结果) {String} baseUrl  标清直播地址
     * @apiSuccess (返回结果) {String} hdUrl  高清直播地址
     * @apiSuccess (返回结果) {String} ysToken 萤石云token
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryVideoMonitorPointLiveInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoMonitorPointLiveInfo(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QueryVideoMonitorPointHistoryLiveInfo 查询视频类型监测点历史时间段的直播地址信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 查询视频类型监测点历史时间段的直播地址信息,该接口会根据监测点下的对应的视频设备,去分析是萤石云还是海康的,返回一个地址
     * @apiName QueryVideoMonitorPointHistoryLiveInfo
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorPointID  监测点ID
     * @apiParam (请求体) {Int} monitorType  监测类别
     * @apiParam (请求体) {Date} beginTime  开始时间
     * @apiParam (请求体) {Date} endTime  结束时间
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorType":"33"}
     * @apiSuccess (返回结果) {String} historyLiveAddress  历史回放地址
     * @apiSuccess (返回结果) {String} ysToken  萤石云token
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryVideoMonitorPointHistoryLiveInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoMonitorPointHistoryLiveInfo(@Validated @RequestBody Object pa) {
        return null;
    }



    /**
     * @api {POST} /PanControlVideoPoint 云台控制监测点视频设备
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 云台控制监测点视频设备的摄像头移动,有(上下左右,焦距)等操作
     * @apiName PanControlVideoPoint
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorPointID  监测点ID
     * @apiParam (请求体) {Int} monitorType  监测类别
     * @apiParam (请求体) {Int} direction  方向   1-8:控制摄像头移动 9-10:控制摄像头缩放(9:焦距变大,10:焦距变小)
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorPointID":"33","monitorType":"40","direction":"1"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/PanControlVideoPoint", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object panControlVideoPoint(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }



    /**
     * @api {POST} /QueryVideoMonitorPointPictureInfo 查询视频类型监测点图像信息
     * @apiVersion 1.0.0
     * @apiGroup 视频模块
     * @apiDescription 云台控制监测点视频设备的摄像头移动,有(上下左右,焦距)等操作
     * @apiName QueryVideoMonitorPointPictureInfo
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorPointID  监测点ID
     * @apiParam (请求体) {Int} monitorType  监测类别
     * @apiParam (请求体) {Date} beginTime  开始时间
     * @apiParam (请求体) {Date} endTime  结束时间
     * @apiParamExample 请求体示例
     * {"projectID":"1","monitorPointID":"33","monitorType":"40","beginTime":"1","endTime":"2"}
     * @apiSuccess (返回结果) {Object[]} data
     * @apiSuccess (返回结果) {Date} data.unloadTime 图片上传时间
     * @apiSuccess (返回结果) {String} data.filePath 图片路径地址
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryVideoMonitorPointPictureInfo", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryVideoMonitorPointPictureInfo(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }


}
