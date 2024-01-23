package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.ProjectConditionParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateDeviceCountStatisticsParam;
import cn.shmedo.monitor.monibotbaseapi.service.ProjectStatisticsService;
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


    private final ProjectStatisticsService projectStatisticsService;


    /**
     * @api {POST} /UpdateDeviceCountStatistics  更新设备资产数量统计
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName UpdateDeviceCountStatistics
     * @apiDescription 更新设备资产数量统计,每小时触发一次
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Boolean}   flag   更新是否成功
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateDeviceCountStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateDeviceCountStatistics")
    @RequestMapping(value = "UpdateDeviceCountStatistics", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateDeviceCountStatistics(@Validated @RequestBody UpdateDeviceCountStatisticsParam pa) {
        return projectStatisticsService.updateDeviceCountStatistics(pa);
    }


    /**
     * @api {POST} /DeviceCountStatistics  设备资产数量统计
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DeviceCountStatistics
     * @apiDescription 设备资产数量统计,走缓存查询
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Int}   projectTotalCount   工程下设备总数量
     * @apiSuccess (返回结果) {Int}   intelligenceCount   智能设备总数量
     * @apiSuccess (返回结果) {Int}   intelligenceOnlineCount   智能设备在线数量
     * @apiSuccess (返回结果) {Int}   intelligenceOffOnlineCount   智能设备离线数量
     * @apiSuccess (返回结果) {Double}   intelligenceRate   智能设备在线率
     * @apiSuccess (返回结果) {Int}   videoCount   视频设备总数量
     * @apiSuccess (返回结果) {Int}   videoOnlineCount   视频设备在线数量
     * @apiSuccess (返回结果) {Int}   videoOffOnlineCount   视频设备离线数量
     * @apiSuccess (返回结果) {Double}   videoRate   视频设备在线率
     * @apiSuccess (返回结果) {Int}   otherCount   其它设备总数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "DeviceCountStatistics", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deviceCountStatistics(@Validated @RequestBody ProjectConditionParam pa) {
        return projectStatisticsService.queryDeviceCountStatistics(pa);
    }


    /**
     * @api {POST} /DataCountStatistics  工程下数据总量以及监测项目和点位数量
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DataCountStatistics
     * @apiDescription 工程下数据总量以及监测项目和点位数量,数据总量走缓存
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Int}   dataCount   数据总数量
     * @apiSuccess (返回结果) {Long}   monitorItemCount   监测项目数量
     * @apiSuccess (返回结果) {Long}   monitorPointTotalCount   监测点总数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "DataCountStatistics", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object dataCountStatistics(@Validated @RequestBody ProjectConditionParam pa) {
        return projectStatisticsService.queryDataCountStatistics(pa);
    }


    /**
     * @api {POST} /MonitorItemCountStatistics  工程下监测项目的监测点数量
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName MonitorItemCountStatistics
     * @apiDescription 工程下监测项目的监测点数量
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Object[]}   data   数据
     * @apiSuccess (返回结果) {Int}   data.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String}   data.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {Int}   data.monitorPointCount   监测点数量
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "MonitorItemCountStatistics", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object monitorItemCountStatistics(@Validated @RequestBody ProjectConditionParam pa) {
        return projectStatisticsService.queryMonitorItemCountStatistics(pa);
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
     * @api {POST} /QuerySingleProjectMonitorPointInfoList  查询单工程下监测点信息列表
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName QuerySingleProjectMonitorPointInfoList
     * @apiDescription 查询单工程下监测点下传感器最新数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} [monitorStatus] 监测状态,[正常:0,蓝:1,黄:2,橙:3,红:4,无数据:-1,设备离线:5]
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {Boolean} [monitorPointCollection] 监测点收藏,null查全部,true查该用户已收藏,false查全部
     * @apiSuccess (返回结果) {Object[]}   data   数据
     * @apiSuccess (返回结果) {Int}   data.monitorPointID   监测点ID
     * @apiSuccess (返回结果) {String}   data.monitorPointName   监测点名称
     * @apiSuccess (返回结果) {String}   data.gpsLocation   监测点位置
     * @apiSuccess (返回结果) {Int}   data.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String}   data.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {Int}   data.monitorGroupID   监测组ID
     * @apiSuccess (返回结果) {String}   data.monitorGroupName   监测组名称
     * @apiSuccess (返回结果) {Date}   data.dataTime   最新接收数据时间
     * @apiSuccess (返回结果) {Int}   data.dataWarnStatus   监测点预警状态,按传感器最高预警状态为判断依据
     * @apiSuccess (返回结果) {Boolean}   data.deviceOnlineStatus   设备在线状态,按规则如果全部在线则为在线,反正则为离线
     * @apiSuccess (返回结果) {T}   data.data   监测点下单个传感器最新数据对象,包含传感器ID名称,基础属性,监测值,属性单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "QuerySingleProjectMonitorPointInfoList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySingleProjectMonitorPointInfoList(@Validated @RequestBody Object pa) {
        return null;
    }



    /**
     * @api {POST} /QuerySingleProjectMonitorPointNewDataPage  查询单工程监测点下传感器最新数据分页
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName QuerySingleProjectMonitorPointNewDataPage
     * @apiDescription 查询单工程下监测点下传感器最新数据分页
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} [monitorStatus] 监测状态,[正常:0,蓝:1,黄:2,橙:3,红:4,无数据:-1,设备离线:5]
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {Boolean} [monitorPointCollection] 监测点收藏,null查全部,true查该用户已收藏,false查全部
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
     * @apiSuccess (返回结果) {Boolean}   currentPageData.multiSensor 是否为多传感器
     * @apiSuccess (返回结果) {Int}   currentPageData.dataWarnStatus   监测点预警状态,按传感器最高预警状态为判断依据
     * @apiSuccess (返回结果) {Boolean}   currentPageData.deviceOnlineStatus   设备在线状态,按规则如果全部在线则为在线,反正则为离线
     * @apiSuccess (返回结果) {T}   currentPageData.data   监测点下单个传感器最新数据对象,包含传感器ID名称,基础属性,监测值,属性单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddBaseProject
     */
    @Permission(permissionName = "mdmbase:AddBaseProject")
    @RequestMapping(value = "QuerySingleProjectMonitorPointNewDataPage", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySingleProjectMonitorPointNewDataPage(@Validated @RequestBody Object pa) {
        return null;
    }


}
