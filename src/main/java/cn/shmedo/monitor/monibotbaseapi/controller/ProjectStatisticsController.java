package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.CheckProjectNameParam;
import cn.shmedo.monitor.monibotbaseapi.service.IOtherDeviceService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工程下首页统计模块
 */
@RestController
@AllArgsConstructor
public class ProjectStatisticsController {


    private final IOtherDeviceService otherDeviceService;


    /**
     * @api {POST} /DeviceCountStatistics  设备资产数量统计
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DeviceCountStatistics
     * @apiDescription 设备资产数量统计
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Int}   IntelligenceCount   智能设备数量
     * @apiSuccess (返回结果) {Int}   videoCount   视频设备数量
     * @apiSuccess (返回结果) {Int}   otherCount   其它设备数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "DeviceCountStatistics", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deviceCountStatistics(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /DataCountStatistics  工程下数据总量以及监测项目和点位数量
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DataCountStatistics
     * @apiDescription 工程下数据总量以及监测项目和点位数量
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Int}   dataCount   数据总数量
     * @apiSuccess (返回结果) {Int}   monitorItemCount   监测项目数量
     * @apiSuccess (返回结果) {Int}   monitorPointTotalCount   监测点总数量
     * @apiSuccess (返回结果) {Object[]}   data   监测类型信息
     * @apiSuccess (返回结果) {Int}   data.monitorType   监测类型ID
     * @apiSuccess (返回结果) {String}   data.monitorTypeName   监测类型名称
     * @apiSuccess (返回结果) {Int}   data.monitorPointCount   监测类型下监测点数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "DataCountStatistics", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object dataCountStatistics(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /DistinctWarnTypeMonitorPointCount  工程下预警类型下监测点数量
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DistinctWarnTypeMonitorPointCount
     * @apiDescription 工程下预警类型下监测点数量
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Int}   noDataCount   无数据状态下监测点数量
     * @apiSuccess (返回结果) {Int}   normalDataCount   正常状态下监测点数量
     * @apiSuccess (返回结果) {Int}   LevelOneCount   预警级别1下监测点数量
     * @apiSuccess (返回结果) {Int}   LevelTwoCount   预警级别2下监测点数量
     * @apiSuccess (返回结果) {Int}   LevelThreeCount   预警级别3下监测点数量
     * @apiSuccess (返回结果) {Int}   LevelFourCount   预警级别4下监测点数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "DistinctWarnTypeMonitorPointCount", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object distinctWarnTypeMonitorPointCount(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QuerySingleProjectMonitorPointNewDataList  查询单工程下监测点下传感器最新数据列表
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DistinctWarnTypeMonitorPointCount
     * @apiDescription 查询单工程下监测点下传感器最新数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiSuccess (返回结果) {Object[]}   data   数据
     * @apiSuccess (返回结果) {Int}   data.monitorPointID   监测点ID
     * @apiSuccess (返回结果) {String}   data.monitorPointName   监测点名称
     * @apiSuccess (返回结果) {String}   data.gpsLocation   监测点位置
     * @apiSuccess (返回结果) {Int}   data.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String}   data.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {Int}   data.monitorGroupID   监测组ID
     * @apiSuccess (返回结果) {String}   data.monitorGroupName   监测组名称
     * @apiSuccess (返回结果) {Date}   data.dataTime   最新接收数据时间
     * @apiSuccess (返回结果) {Int}   data.dataWarnStatus   监测点预警状态
     * @apiSuccess (返回结果) {Boolean}   data.deviceOnlineStatus   设备在线状态,按规则取某个特定的传感器
     * @apiSuccess (返回结果) {T}   data.data   监测点下单个传感器最新数据对象,包含基础属性,监测值,属性单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "QuerySingleProjectMonitorPointNewDataList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySingleProjectMonitorPointNewDataList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QuerySingleProjectMonitorPointNewDataPage  查询单工程下监测点下传感器最新数据分页
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName QuerySingleProjectMonitorPointNewDataPage
     * @apiDescription 查询单工程下监测点下传感器最新数据分页
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int}   currentPageData.monitorPointID   监测点ID
     * @apiSuccess (返回结果) {String}   currentPageData.monitorPointName   监测点名称
     * @apiSuccess (返回结果) {String}   currentPageData.gpsLocation   监测点位置
     * @apiSuccess (返回结果) {Int}   currentPageData.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String}   currentPageData.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {Int}   currentPageData.monitorGroupID   监测组ID
     * @apiSuccess (返回结果) {String}   currentPageData.monitorGroupName   监测组名称
     * @apiSuccess (返回结果) {Date}   currentPageData.dataTime   最新接收数据时间
     * @apiSuccess (返回结果) {Int}   currentPageData.dataWarnStatus   监测点预警状态
     * @apiSuccess (返回结果) {Boolean}   currentPageData.deviceOnlineStatus   设备在线状态,按规则取某个特定的传感器
     * @apiSuccess (返回结果) {T}   currentPageData.data   监测点下单个传感器最新数据对象,包含基础属性,监测值,属性单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "QuerySingleProjectMonitorPointNewDataPage", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySingleProjectMonitorPointNewDataPage(@Validated @RequestBody Object pa) {
        return null;
    }


}
