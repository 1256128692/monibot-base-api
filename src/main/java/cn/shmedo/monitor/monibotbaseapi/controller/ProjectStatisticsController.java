package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
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
     * @apiSuccess (返回结果) {Boolean}   data   更新是否成功
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateDeviceCountStatistics
     */
    @Permission(permissionName = "mdmbase:UpdateDeviceCountStatistics", allowApplication = true)
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
     * @apiSuccess (返回结果) {Int}   intelligenceOfflineCount   智能设备离线数量
     * @apiSuccess (返回结果) {Double}   intelligenceRate   智能设备在线率
     * @apiSuccess (返回结果) {Int}   videoCount   视频设备总数量
     * @apiSuccess (返回结果) {Int}   videoOnlineCount   视频设备在线数量
     * @apiSuccess (返回结果) {Int}   videoOfflineCount   视频设备离线数量
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
     * @apiDescription 工程下数据总量以及监测项目和点位数量(点下面必须包含传感器),数据总量走缓存
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
     * @apiDescription 工程下监测项目的监测点数量,其中监测点必须含有传感器
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiSuccess (返回结果) {Object[]}   data   数据
     * @apiSuccess (返回结果) {Int}   data.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {Int}   data.monitorType   监测类型
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
     * @apiSuccess (返回结果) {Int}   normalCount   正常状态下监测点数量
     * @apiSuccess (返回结果) {Int}   LevelOneCount   预警级别1下监测点数量(红色)
     * @apiSuccess (返回结果) {Int}   LevelTwoCount   预警级别2下监测点数量(橙色)
     * @apiSuccess (返回结果) {Int}   LevelThreeCount   预警级别3下监测点数量(黄色)
     * @apiSuccess (返回结果) {Int}   LevelFourCount   预警级别4下监测点数量(蓝色)
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "DistinctWarnTypeMonitorPointCount", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object distinctWarnTypeMonitorPointCount(@Validated @RequestBody ProjectConditionParam pa) {
        return projectStatisticsService.queryDistinctWarnTypeMonitorPointCount(pa);
    }


    /**
     * @api {POST} /AddUserCollectionMonitorPoint  用户新增监测点收藏
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName AddUserCollectionMonitorPoint
     * @apiDescription 工程下预警类型下监测点数量
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点列表
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "AddUserCollectionMonitorPoint", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addUserCollectionMonitorPoint(@Validated @RequestBody AddUserCollectionMonitorPointParam pa) {
        projectStatisticsService.addUserCollectionMonitorPoint(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteUserCollectionMonitorPoint  用户取消监测点收藏
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName DeleteUserCollectionMonitorPoint
     * @apiDescription 工程下预警类型下监测点数量
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点列表
     * @apiSuccess (返回结果) {String} none
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "DeleteUserCollectionMonitorPoint", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteUserCollectionMonitorPoint(@Validated @RequestBody DeleteUserCollectionMonitorPointParam pa) {
        projectStatisticsService.deleteUserCollectionMonitorPoint(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /UpdateSensorOnlineStatusByIot  刷新传感器的在线离线状态
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName UpdateSensorOnlineStatusByIot
     * @apiDescription 刷新传感器的在线离线状态
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiSuccess (返回结果) {Boolean} data 更新是否成功
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:UpdateSensorOnlineStatus
     */
    @Permission(permissionName = "mdmbase:UpdateSensorOnlineStatus", allowApplication = true)
    @RequestMapping(value = "UpdateSensorOnlineStatusByIot", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateSensorOnlineStatusByIot(@Validated @RequestBody UpdateDeviceCountStatisticsParam pa) {
        return projectStatisticsService.updateSensorOnlineStatusByIot(pa);
    }


    /**
     * @api {POST} /QuerySingleProjectMonitorPointInfoList  查询单工程下监测点信息列表
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName QuerySingleProjectMonitorPointInfoList
     * @apiDescription 查询单工程下监测点下传感器最新数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} [monitorStatusList] 监测状态,[正常:0,蓝:1,黄:2,橙:3,红:4,无数据:-1]
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {Boolean} [monitorPointCollection] 监测点收藏,null查全部,true查该用户已收藏,false查全部
     * @apiSuccess (返回结果) {Object[]}   data   数据
     * @apiSuccess (返回结果) {Int}   data.monitorPointID   监测点ID
     * @apiSuccess (返回结果) {String}   data.monitorPointName   监测点名称
     * @apiSuccess (返回结果) {Int}   data.monitorType   监测类型
     * @apiSuccess (返回结果) {String}   data.gpsLocation   监测点位置
     * @apiSuccess (返回结果) {String}   data.imageLocation   监测点底图位置
     * @apiSuccess (返回结果) {String}   data.overallViewLocation   监测点全景位置
     * @apiSuccess (返回结果) {String}   data.spatialLocation   监测点三维位置
     * @apiSuccess (返回结果) {Int}   data.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String}   data.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {Int}   data.monitorGroupID   监测组ID
     * @apiSuccess (返回结果) {String}   data.monitorGroupName   监测组名称
     * @apiSuccess (返回结果) {String}   data.configFieldValue   额外配置
     * @apiSuccess (返回结果) {Date}   data.dataTime   最新接收数据时间
     * @apiSuccess (返回结果) {Boolean}   data.multiSensor 是否为多传感器
     * @apiSuccess (返回结果) {Int}   data.dataWarnStatus   监测点预警状态,[正常:0,红:1,橙:2,黄:3,蓝:4,无数据:-1]
     * @apiSuccess (返回结果) {Int}   data.deviceOnlineStatus   设备在线状态,0:离线,1:在线,按规则如果全部在线则为在线,反正则为离线
     * @apiSuccess (返回结果) {Boolean}   data.monitorPointCollection   当前用户是否收藏该监测点
     * @apiSuccess (返回结果) {T}   data.snsorData   监测点下单个传感器最新数据对象,包含传感器ID,基础属性,监测值
     * @apiSuccess (返回结果) {Object[]}   data.monitorTypeFields   监测属性
     * @apiSuccess (返回结果) {String}   data.monitorTypeFields.fieldToken   监测属性
     * @apiSuccess (返回结果) {String}   data.monitorTypeFields.fieldName   监测属性名称
     * @apiSuccess (返回结果) {String}   data.monitorTypeFields.engUnit   单位英文名称
     * @apiSuccess (返回结果) {String}   data.monitorTypeFields.chnUnit   单位中文名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "QuerySingleProjectMonitorPointInfoList", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySingleProjectMonitorPointInfoList(@Validated @RequestBody QuerySingleProjectMonitorPointInfoListParam pa) {
        return projectStatisticsService.querySingleProjectMonitorPointInfoList(pa);
    }



    /**
     * @api {POST} /QuerySingleProjectMonitorPointNewDataPage  查询单工程监测点下传感器最新数据分页
     * @apiVersion 1.0.0
     * @apiGroup 工程下首页统计模块
     * @apiName QuerySingleProjectMonitorPointNewDataPage
     * @apiDescription 查询单工程下监测点下传感器最新数据分页
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} [monitorStatusList] 监测状态,[正常:0,蓝:1,黄:2,橙:3,红:4,无数据:-1]
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
     * @apiSuccess (返回结果) {Int}   currentPageData.monitorType   监测类型
     * @apiSuccess (返回结果) {String}   currentPageData.gpsLocation   监测点位置
     * @apiSuccess (返回结果) {String}   currentPageData.imageLocation   监测点底图位置
     * @apiSuccess (返回结果) {String}   currentPageData.overallViewLocation   监测点全景位置
     * @apiSuccess (返回结果) {String}   currentPageData.spatialLocation   监测点三维位置
     * @apiSuccess (返回结果) {Int}   currentPageData.monitorItemID   监测项目ID
     * @apiSuccess (返回结果) {String}   currentPageData.monitorItemName   监测项目名称
     * @apiSuccess (返回结果) {Int}   currentPageData.monitorGroupID   监测组ID
     * @apiSuccess (返回结果) {String}   currentPageData.monitorGroupName   监测组名称
     * @apiSuccess (返回结果) {String}   currentPageData.configFieldValue   额外配置
     * @apiSuccess (返回结果) {Date}   currentPageData.dataTime   最新接收数据时间
     * @apiSuccess (返回结果) {Boolean}   currentPageData.multiSensor 是否为多传感器
     * @apiSuccess (返回结果) {Int}   currentPageData.dataWarnStatus   监测点预警状态,[正常:0,红:1,橙:2,黄:3,蓝:4,无数据:-1]
     * @apiSuccess (返回结果) {Boolean}   currentPageData.deviceOnlineStatus   设备在线状态,0:离线,1:在线,按规则如果全部在线则为在线,反正则为离线
     * @apiSuccess (返回结果) {T}   currentPageData.snsorData   监测点下单个传感器最新数据对象,包含传感器ID,基础属性,监测值
     * @apiSuccess (返回结果) {Object[]}   currentPageData.monitorTypeFields   监测属性
     * @apiSuccess (返回结果) {String}   currentPageData.monitorTypeFields.fieldToken   监测属性
     * @apiSuccess (返回结果) {String}   currentPageData.monitorTypeFields.fieldName   监测属性名称
     * @apiSuccess (返回结果) {String}   currentPageData.monitorTypeFields.engUnit   单位英文名称
     * @apiSuccess (返回结果) {String}   currentPageData.monitorTypeFields.chnUnit   单位中文名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeProjectInfo
     */
    @Permission(permissionName = "mdmbase:DescribeProjectInfo")
    @RequestMapping(value = "QuerySingleProjectMonitorPointNewDataPage", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySingleProjectMonitorPointNewDataPage(@Validated @RequestBody QuerySingleProjectMonitorPointNewDataPageParam pa) {
        return projectStatisticsService.querySingleProjectMonitorPointNewDataPage(pa);
    }


}
