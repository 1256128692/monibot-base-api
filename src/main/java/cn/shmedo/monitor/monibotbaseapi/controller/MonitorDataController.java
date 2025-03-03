package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.DeleteBatchDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.UpdateDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeConfigurationParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorDataService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MonitorDataController {

    private MonitorDataService monitorDataService;

    /**
     * @api {POST} /QueryMonitorPointDataList 查询监测点数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorPointDataList
     * @apiDescription 查询监测点数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} densityType 密度,(全部:1 小时:2 日:3 周:4 月:5 年:6),查询最新数据默认传1
     * @apiParam (请求体) {Int} statisticsType 统计方式,(最新一条:1 平均:2 阶段累积:3 阶段变化:4),查询最新数据默认传1
     * @apiParam (请求体) {Boolean} [filterEmptyData]  是否过滤空数据,默认为false(不过滤),true(过滤空数据)
     * @apiParam (请求体) {Boolean} [dataSort]  数据排序,为空默认倒序,true为正序,false为倒序
     * @apiParam (请求体) {Int[]} [eigenValueIDList] 特征值ID列表
     * @apiParam (请求体) {Int[]} [eventIDList] 大事记ID列表
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorType":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31","densityType":0,"statisticsType":0}
     * @apiSuccess (响应结果) {Object[]} data                 结果列表
     * @apiSuccess (响应结果) {Int} data.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType          监测类型
     * @apiSuccess (响应结果) {String} data.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} data.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Object[]} [data.sensorList]      传感器信息
     * @apiSuccess (响应结果) {Int} data.sensorList.sensorID    传感器ID
     * @apiSuccess (响应结果) {Int} data.sensorList.projectID 项目ID
     * @apiSuccess (响应结果) {Int} data.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {String} data.sensorList.sensorName  传感器名称
     * @apiSuccess (响应结果) {Object[]} data.sensorList.multiSensorData   多传感器数据
     * @apiSuccess (响应结果) {Int} data.sensorList.multiSensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {DateTime} data.sensorList.multiSensorData.time  数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorList.multiSensorData.data  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Bool} [data.multiSensor]   是否为关联多传感器
     * @apiSuccess (响应结果) {Int} data.sensorDataCount  数据总量
     * @apiSuccess (响应结果) {Object[]} data.fieldList   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldList.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} data.fieldList.fieldName   字段名称
     * @apiSuccess (响应结果) {String} data.fieldList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} data.fieldList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} data.fieldList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} data.fieldList.unitDesc  单位类型描述
     * @apiSuccess (响应结果) {Object[]} [data.eigenValueList] 特征值列表
     * @apiSuccess (响应结果) {Int} data.eigenValueList.id 特征值列表
     * @apiSuccess (响应结果) {Int} data.eigenValueList.projectID 工程ID
     * @apiSuccess (响应结果) {Int} data.eigenValueList.scope 使用范围,0:专题分析 1:历史数据
     * @apiSuccess (响应结果) {String} [data.eigenValueList.scopeStr] 使用范围描述
     * @apiSuccess (响应结果) {Int} data.eigenValueList.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {Int} data.eigenValueList.monitorTypeFieldID 属性(监测子类型ID)
     * @apiSuccess (响应结果) {String} data.eigenValueList.monitorTypeFieldName 属性名称
     * @apiSuccess (响应结果) {String} data.eigenValueList.name 特征值名称
     * @apiSuccess (响应结果) {Double} data.eigenValueList.value 数值
     * @apiSuccess (响应结果) {Int} data.eigenValueList.fieldUnitID 单位ID
     * @apiSuccess (响应结果) {String} data.eigenValueList.engUnit 单位英文描述
     * @apiSuccess (响应结果) {String} data.eigenValueList.chnUnit 单位中文描述
     * @apiSuccess (响应结果) {Object[]} [data.eventList] 大事记列表
     * @apiSuccess (响应结果) {Int} data.eventList.id 大事记id
     * @apiSuccess (响应结果) {String} data.eventList.eventName 大事记名称
     * @apiSuccess (响应结果) {Int} data.eventList.frequency 频率
     * @apiSuccess (响应结果) {String} data.eventList.frequencyStr 频率描述
     * @apiSuccess (响应结果) {String} data.eventList.timeRange 时间范围
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorPointDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointDataList(@Validated @RequestBody QueryMonitorPointDataParam pa) {
        return monitorDataService.queryMonitorPointDataList(pa);
    }

    /**
     * @api {POST} /QueryMonitorPointDataListPage 查询监测点数据列表(分页)
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorPointDataListPage
     * @apiDescription 查询监测点数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end 结束时间
     * @apiParam (请求体) {Int} densityType 密度,(全部:1 小时:2 日:3 周:4 月:5 年:6),查询最新数据默认传1
     * @apiParam (请求体) {Int} statisticsType 统计方式,(最新一条:1 平均:2 阶段累积:3 阶段变化:4),查询最新数据默认传1
     * @apiParam (请求体) {Int} pageSize 分页大小 (1-100)
     * @apiParam (请求体) {Int} currentPage 当前页码 (大于0)
     * @apiParam (请求体) {Boolean} [filterEmptyData] 是否过滤空数据,默认为false(不过滤),true(过滤空数据)
     * @apiParam (请求体) {Boolean} [dataSort]  数据排序,为空默认倒序,true为正序,false为倒序
     * @apiParam (请求体) {Int[]} [eigenValueIDList] 特征值ID列表
     * @apiParam (请求体) {Int[]} [eventIDList] 大事记ID列表,该参数不传时为全部(列表没有大事记下拉框,不传该参数,展示全部)
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorType":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31","densityType":0,"statisticsType":0,
     * "pageSize":5,"currentPage":1}
     * @apiSuccess (响应结果) {Int} totalCount 数据总量
     * @apiSuccess (响应结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object} map 监测属性描述(表头数据)
     * @apiSuccess (响应结果) {Object[]} map.fieldList 监测类型属性字段列表
     * @apiSuccess (响应结果) {String} map.fieldList.fieldToken 字段标志
     * @apiSuccess (响应结果) {String} map.fieldList.fieldName 字段名称
     * @apiSuccess (响应结果) {String} map.fieldList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} map.fieldList.chnUnit 中文单位
     * @apiSuccess (响应结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType 监测类型
     * @apiSuccess (响应结果) {String} currentPageData.monitorTypeName 监测类型名称
     * @apiSuccess (响应结果) {String} currentPageData.monitorTypeAlias 监测类型别名
     * @apiSuccess (响应结果) {Int} currentPageData.sensorID 传感器ID
     * @apiSuccess (响应结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (响应结果) {String} currentPageData.sensorName 传感器名称
     * @apiSuccess (响应结果) {String} currentPageData.sensorAlias 传感器别称
     * @apiSuccess (响应结果) {DateTime} currentPageData.time 数据采集时间
     * @apiSuccess (响应结果) {Map} currentPageData.data 传感器数据map,内部key为动态值，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Bool} [currentPageData.multiSensor] 是否为关联多传感器
     * @apiSuccess (响应结果) {Object} [currentPageData.maxMark] 最大值标记,key-fieldToken,value-标记值;仅data中该属性值为最大值时存在该标记,恒为1(同时有多个最大值时,取最新一条)
     * @apiSuccess (响应结果) {Object} [currentPageData.minMark] 最小值标记,key-fieldToken,value-标记值;仅data中该属性值为最小值时存在该标记,恒为1(同时有多个最小值时,取最新一条)
     * @apiSuccess (响应结果) {Object[]} [currentPageData.eventList] 大事记列表
     * @apiSuccess (响应结果) {Int} currentPageData.eventList.id 大事记id
     * @apiSuccess (响应结果) {Int} currentPageData.eventList.frequency 频率
     * @apiSuccess (响应结果) {String} currentPageData.eventList.eventName 大事记名称
     * @apiSuccess (响应结果) {String} currentPageData.eventList.frequencyStr 频率描述
     * @apiSuccess (响应结果) {String} currentPageData.eventList.timeRange 时间范围
     * @apiSuccess (响应结果) {String} currentPageData.eventList.hintTimeRange 大事记命中的时间范围
     * @apiSampleRequest off
     * @apiSuccessExample {json} 响应结果示例
     * {"code":0,"msg":null,"data":{"totalPage":1,"currentPageData":[{"monitorPointID":693,"monitorPointName":"203物模型（绝对）","monitorType":22,"monitorTypeName":"表面一维形变（绝对）","monitorTypeAlias":"表面一维形变（绝对）","multiSensor":false,"projectID":253,"sensorID":961,"sensorName":"22_2","time":"2024-05-13 14:04:22","data":{"xTotalDisp":2.304584048387496,"xNowSpeed":6.319209644999391,"xNowCoor":2.304584048387496,"xNowDisp":0.131357644935404,"xNowAcc":-1351.859569073989,"time":"2024-05-13 14:04:22.000","sensorID":961},"maxMark":null,"minMark":null,"eventList":[{"id":65,"eventName":"历史数据测试","frequency":1,"frequencyStr":"每年","timeRange":"[{\"startTime\":\"2024-04-12 00:00:00\",\"endTime\":\"2024-04-13 23:59:59\"},{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]","hintTimeRange":"[{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]"}]},{"monitorPointID":695,"monitorPointName":"213物模型（绝对）","monitorType":22,"monitorTypeName":"表面一维形变（绝对）","monitorTypeAlias":"表面一维形变（绝对）","multiSensor":false,"projectID":253,"sensorID":958,"sensorName":"22_1","time":"2024-05-13 14:04:22","data":{"xTotalDisp":3.668110988301363,"xNowSpeed":108.02387479478264,"xNowCoor":3.668110988301363,"xNowDisp":2.2454962862433985,"xNowAcc":11186.458528175273,"time":"2024-05-13 14:04:22.000","sensorID":958},"maxMark":{"xTotalDisp":1,"xNowSpeed":1,"xNowCoor":1,"xNowDisp":1,"xNowAcc":1},"minMark":null,"eventList":[{"id":65,"eventName":"历史数据测试","frequency":1,"frequencyStr":"每年","timeRange":"[{\"startTime\":\"2024-04-12 00:00:00\",\"endTime\":\"2024-04-13 23:59:59\"},{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]","hintTimeRange":"[{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]"}]},{"monitorPointID":693,"monitorPointName":"203物模型（绝对）","monitorType":22,"monitorTypeName":"表面一维形变（绝对）","monitorTypeAlias":"表面一维形变（绝对）","multiSensor":false,"projectID":253,"sensorID":961,"sensorName":"22_2","time":"2024-05-13 13:34:26","data":{"xTotalDisp":2.173226403452092,"xNowSpeed":34.42036457621333,"xNowCoor":2.173226403452092,"xNowDisp":0.718684464068158,"xNowAcc":7545.976265171761,"time":"2024-05-13 13:34:26.000","sensorID":961},"maxMark":null,"minMark":null,"eventList":[{"id":65,"eventName":"历史数据测试","frequency":1,"frequencyStr":"每年","timeRange":"[{\"startTime\":\"2024-04-12 00:00:00\",\"endTime\":\"2024-04-13 23:59:59\"},{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]","hintTimeRange":"[{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]"}]},{"monitorPointID":695,"monitorPointName":"213物模型（绝对）","monitorType":22,"monitorTypeName":"表面一维形变（绝对）","monitorTypeAlias":"表面一维形变（绝对）","multiSensor":false,"projectID":253,"sensorID":958,"sensorName":"22_1","time":"2024-05-13 13:34:26","data":{"xTotalDisp":1.4226147020579645,"xNowSpeed":-124.50945294367561,"xNowCoor":1.4226147020579645,"xNowDisp":-2.5997112628517454,"xNowAcc":-4773.432530420755,"time":"2024-05-13 13:34:26.000","sensorID":958},"maxMark":null,"minMark":{"xTotalDisp":1,"xNowSpeed":1,"xNowCoor":1,"xNowDisp":1,"xNowAcc":1},"eventList":[{"id":65,"eventName":"历史数据测试","frequency":1,"frequencyStr":"每年","timeRange":"[{\"startTime\":\"2024-04-12 00:00:00\",\"endTime\":\"2024-04-13 23:59:59\"},{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]","hintTimeRange":"[{\"startTime\":\"2024-05-12 00:00:00\",\"endTime\":\"2024-05-17 23:59:59\"},{\"startTime\":\"2024-05-10 00:00:00\",\"endTime\":\"2024-05-14 23:59:59\"}]"}]}],"totalCount":4,"map":{"fieldList":[{"fieldToken":"xNowCoor","fieldName":"X绝对坐标","fieldUnitID":1,"engUnit":"mm","chnUnit":"毫米","unitClass":"长度","unitDesc":"毫米","displayOrder":null,"fieldClass":null},{"fieldToken":"xTotalDisp","fieldName":"X相对位移","fieldUnitID":1,"engUnit":"mm","chnUnit":"毫米","unitClass":"长度","unitDesc":"毫米","displayOrder":null,"fieldClass":null},{"fieldToken":"xNowDisp","fieldName":"实时X位移变化量","fieldUnitID":1,"engUnit":"mm","chnUnit":"毫米","unitClass":"长度","unitDesc":"毫米","displayOrder":null,"fieldClass":null},{"fieldToken":"xNowSpeed","fieldName":"实时X位移变化速率","fieldUnitID":24,"engUnit":"mm/d","chnUnit":"毫米每天","unitClass":"速度","unitDesc":"毫米每天","displayOrder":null,"fieldClass":null},{"fieldToken":"xNowAcc","fieldName":"实时X位移加速度","fieldUnitID":25,"engUnit":"mm/d²","chnUnit":"毫米每二次方天","unitClass":"加速度","unitDesc":"毫米每二次方天","displayOrder":null,"fieldClass":null}]}}}
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorPointDataListPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointDataListPage(@Validated @RequestBody QueryMonitorPointDataListPageParam pa) {
        return monitorDataService.queryMonitorPointDataListPage(pa);
    }

    /**
     * @api {POST} /QueryMonitorPointFilterDataList 查询监测点过滤值列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorPointFilterDataList
     * @apiDescription 查询监测点过滤值列表, 包含传感器每个属性在一段时间内的最大值, 最小值统计
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Boolean} [filterEmptyData]  是否过滤空数据,默认为false(不过滤),true(过滤空数据)
     * @apiParam (请求体) {Boolean} [dataSort]  数据排序,为空默认倒序,true为正序,false为倒序
     * @apiParam (请求体) {Int} densityType 密度,(全部:1 小时:2 日:3 周:4 月:5 年:6)
     * @apiParam (请求体) {Int} statisticsType 统计方式,(最新一条:1 平均:2 阶段累积:3 阶段变化:4)
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorType":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31","densityType":2,"statisticsType":0}
     * @apiSuccess (响应结果) {Object[]} data                 结果列表
     * @apiSuccess (响应结果) {Int} data.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType          监测类型
     * @apiSuccess (响应结果) {String} data.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} data.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Object[]} data.sensorList      传感器信息
     * @apiSuccess (响应结果) {Int} data.sensorList.id        传感器id
     * @apiSuccess (响应结果) {Int} data.sensorList.projectID 项目id
     * @apiSuccess (响应结果) {Int} data.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {String} data.sensorList.name  传感器名称
     * @apiSuccess (响应结果) {Object} data.sensorList.maxSensorDataList 传感器最大数据
     * @apiSuccess (响应结果) {Object} data.sensorList.maxSensorDataList.fieldToken   监测子类型
     * @apiSuccess (响应结果) {DateTime} data.sensorList.maxSensorDataList.fieldToken.time  数据采集时间
     * @apiSuccess (响应结果) {Int} data.sensorList.maxSensorDataList.fieldToken.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} data.sensorList.maxSensorDataList.fieldToken.value   数值
     * @apiSuccess (响应结果) {Object[]} data.sensorList.minSensorDataList 传感器最小数据
     * @apiSuccess (响应结果) {Object} data.sensorList.minSensorDataList.fieldToken   监测子类型
     * @apiSuccess (响应结果) {DateTime} data.sensorList.minSensorDataList.fieldToken.time  数据采集时间
     * @apiSuccess (响应结果) {Int} data.sensorList.minSensorDataList.fieldToken.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} data.sensorList.minSensorDataList.fieldToken.value   数值
     * @apiSuccess (响应结果) {Int} data.sensorDataCount  数据总量
     * @apiSuccess (响应结果) {Object[]} data.fieldList   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldList.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} data.fieldList.fieldName   字段名称
     * @apiSuccess (响应结果) {String} data.fieldList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} data.fieldList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} data.fieldList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} data.fieldList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorPointFilterDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointFilterDataList(@Validated @RequestBody QueryMonitorPointDataParam pa) {
        return monitorDataService.queryMonitorPointFilterDataList(pa);
    }

    /**
     * @api {POST} /AddEigenValueList 批量新增数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName AddEigenValueList
     * @apiDescription 新增数据特征值
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Object[]} dataList 参数列表
     * @apiParam (请求体) {Int} dataList.scope 作用范围,0:专题分析 1:历史数据
     * @apiParam (请求体) {Int} dataList.monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} [dataList.monitorPointIDList] 监测点ID列表 (!为空时表示关联所有监测点)
     * @apiParam (请求体) {Int} dataList.monitorTypeFieldID 属性(监测子类型ID)
     * @apiParam (请求体) {String} dataList.name 特征值名称
     * @apiParam (请求体) {Double} dataList.value 数值
     * @apiParam (请求体) {Int} dataList.unitID 单位ID
     * @apiParam (请求体) {String} [dataList.exValue] 拓展属性
     * @apiParamExample 请求体示例
     * {"projectID":1,"dataList":[
     * {
     * "scope":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * * "monitorTypeFieldID":"1","name":"123","value":"123.123","unitID":"1"
     * }
     * ]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddEigenValue
     */
    @Permission(permissionName = "mdmbase:AddEigenValue")
    @RequestMapping(value = "/AddEigenValueList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addEigenValueList(@Validated @RequestBody AddEigenValueListParam pa) {
        monitorDataService.addEigenValueList(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /AddEigenValue 新增数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName AddEigenValue
     * @apiDescription 新增数据特征值
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} scope 作用范围,0:专题分析 1:历史数据
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {Int} monitorTypeFieldID 属性(监测子类型ID)
     * @apiParam (请求体) {String} name 特征值名称
     * @apiParam (请求体) {Double} value 数值
     * @apiParam (请求体) {Int} unitID 单位ID
     * @apiParam (请求体) {String} [exValue] 拓展属性
     * @apiParamExample 请求体示例
     * {"projectID":1,"scope":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "monitorTypeFieldID":"1","name":"123","value":"123.123","unitID":"1"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddEigenValue
     */
    @Permission(permissionName = "mdmbase:AddEigenValue")
    @RequestMapping(value = "/AddEigenValue", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addEigenValue(@Validated @RequestBody AddEigenValueParam pa) {
        monitorDataService.addEigenValue(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /UpdateEigenValue 更新数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName UpdateEigenValue
     * @apiDescription 更新数据特征值
     * @apiParam (请求体) {Int} eigenValueID 特征ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} scope 作用范围,0:专题分析 1:历史数据
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表 (!为空时表示关联所有监测点)
     * @apiParam (请求体) {Int} monitorTypeFieldID 属性(监测子类型ID)
     * @apiParam (请求体) {String} name 特征值名称
     * @apiParam (请求体) {Double} value 数值
     * @apiParam (请求体) {Int} unitID 单位ID
     * @apiParam (请求体) {String} [exValue] 拓展属性
     * @apiParamExample 请求体示例
     * {"eigenValueID":1,"projectID":1,"scope":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "monitorTypeFieldID":"1","name":"123","value":"123.123","unitID":"1"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateEigenValue
     */
    @Permission(permissionName = "mdmbase:UpdateEigenValue")
    @RequestMapping(value = "/UpdateEigenValue", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateEigenValue(@Validated @RequestBody UpdateEigenValueParam pa) {
        monitorDataService.updateEigenValue(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /DeleteBatchEigenValue 删除数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName DeleteBatchEigenValue
     * @apiDescription 删除数据特征值
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} eigenValueIDList 特征ID列表
     * @apiParamExample 请求体示例
     * {"projectID":1,"eigenValueIDList":[1,2]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteEigenValue
     */
    @Permission(permissionName = "mdmbase:DeleteEigenValue")
    @RequestMapping(value = "/DeleteBatchEigenValue", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteBatchEigenValue(@Validated @RequestBody DeleteBatchEigenValueParam pa) {
        monitorDataService.deleteBatchEigenValue(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryEigenValueList 查询数据特征值列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryEigenValueList
     * @apiDescription 查询数据特征值列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求体) {String[]} [fieldTokenList] 监测子属性token列表
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
     * @apiParam (请求体) {Int} [scope] 数据范围
     * @apiParamExample 请求体示例
     * {"projectID":1}
     * @apiSuccess (响应结果) {Object[]} eigenvalueList 特征值列表
     * @apiSuccess (响应结果) {Int} eigenvalueList.id 特征值列表
     * @apiSuccess (响应结果) {Int} eigenvalueList.projectID 工程ID
     * @apiSuccess (响应结果) {Int} eigenvalueList.scope 使用范围,0:专题分析 1:历史数据
     * @apiSuccess (响应结果) {String} eigenvalueList.scopeStr 使用范围描述
     * @apiSuccess (响应结果) {Int} eigenvalueList.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} eigenvalueList.monitorItemName 监测项目名称
     * @apiSuccess (响应结果) {Int} eigenvalueList.monitorTypeFieldID 属性(监测子类型ID)
     * @apiSuccess (响应结果) {String} eigenvalueList.monitorTypeFieldToken 属性标识
     * @apiSuccess (响应结果) {String} [eigenvalueList.monitorTypeFieldName] 属性名称
     * @apiSuccess (响应结果) {String} eigenvalueList.name 特征值
     * @apiSuccess (响应结果) {Double} eigenvalueList.value 数值
     * @apiSuccess (响应结果) {Int} eigenvalueList.unitID 单位ID
     * @apiSuccess (响应结果) {Boolean} eigenvalueList.allMonitorPoint 是否关联全部监测点
     * @apiSuccess (响应结果) {String} eigenvalueList.engUnit 单位英文描述
     * @apiSuccess (响应结果) {String} eigenvalueList.chnUnit 单位中文描述
     * @apiSuccess (响应结果) {String} [eigenvalueList.exValue] 拓展属性
     * @apiSuccess (响应结果) {Int} eigenvalueList.createUserID 创建人ID
     * @apiSuccess (响应结果) {Int} eigenvalueList.updateUserID 修改人ID
     * @apiSuccess (响应结果) {Date} eigenvalueList.createTime 创建时间
     * @apiSuccess (响应结果) {Date} eigenvalueList.updateTime 修改时间
     * @apiSuccess (响应结果) {Object[]} eigenvalueList.monitorPointList 监测点ID列表
     * @apiSuccess (响应结果) {Int} eigenvalueList.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (响应结果) {String} eigenvalueList.monitorPointList.monitorPointName 监测点名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryEigenValueList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryEigenValueList(@Validated @RequestBody QueryEigenValueParam pa) {
        return monitorDataService.queryEigenValueList(pa);
    }


    /**
     * @api {POST} /AddDataEvent 新增大事记
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName AddDataEvent
     * @apiDescription 新增大事记
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {String} name 大事记名称
     * @apiParam (请求体) {Int} frequency 频率,0:单次  1:每年
     * @apiParam (请求体) {String} timeRange 开始-结束时间,json格式
     * @apiParam (请求体) {String} [exValue] 拓展属性
     * @apiParam (请求体) {Int[]} monitorItemIDList 监测项目ID列表
     * @apiParamExample 请求体示例
     * {
     * "projectID": 623,
     * "name": "台风事件",
     * "frequency": 1,
     * "monitorItemIDList": [
     * 12702
     * ],
     * "timeRange": "[{\"startTime\": \"2023-10-18 16:02:52\", \"endTime\": \"2023-10-18 16:02:52\"}, {\"startTime\": \"2023-11-18 16:02:52\", \"endTime\": \"2023-11-18 16:02:52\"}, {\"startTime\": \"2023-12-18 16:02:52\", \"endTime\": \"2023-12-18 16:02:52\"}]"
     * }
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:AddDataEvent
     */
    @Permission(permissionName = "mdmbase:AddDataEvent")
    @RequestMapping(value = "/AddDataEvent", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addDataEvent(@Validated @RequestBody AddDataEventParam pa) {
        monitorDataService.addDataEvent(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /DeleteBatchDataEvent 删除大事记
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName DeleteBatchDataEvent
     * @apiDescription 删除大事记
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} eventIDList 大事记名称
     * @apiParamExample 请求体示例
     * {"projectID":1,"eventIDList":[1]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteDataEvent
     */
    @Permission(permissionName = "mdmbase:DeleteDataEvent")
    @RequestMapping(value = "/DeleteBatchDataEvent", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteBatchDataEvent(@Validated @RequestBody DeleteBatchDataEventParam pa) {
        monitorDataService.deleteBatchDataEvent(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryDataEventList 查询大事记列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryDataEventList
     * @apiDescription 查询大事记列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [monitorItemID] 项目ID
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
     * @apiParam (请求体) {DateTime} [begin] 开始时间
     * @apiParam (请求体) {DateTime} [end]   结束时间
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorItemID":1}
     * @apiSuccess (返回结果) {Object[]} dataList 大事记ID
     * @apiSuccess (返回结果) {Int} dataList.id 大事记ID
     * @apiSuccess (返回结果) {Int} dataList.projectID 工程ID
     * @apiSuccess (返回结果) {String} dataList.name 大事记名称
     * @apiSuccess (返回结果) {Int} dataList.frequency 频率,0:单次  1:每年
     * @apiSuccess (返回结果) {String} dataList.frequencyStr 频率,0:单次  1:每年
     * @apiSuccess (返回结果) {String} dataList.timeRange 开始-结束时间,json格式
     * @apiSuccess (返回结果) {String} [dataList.exValue] 拓展属性
     * @apiSuccess (响应结果) {Object[]} dataList.monitorItemList 监测项目列表
     * @apiSuccess (响应结果) {Int} dataList.monitorItemList.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} dataList.monitorItemList.name 监测项目名称
     * @apiSuccess (响应结果) {Date} dataList.monitorItemList.createTime 创建时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryDataEventList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryDataEventList(@Validated @RequestBody QueryDataEventParam pa) {
        return monitorDataService.queryDataEventList(pa);
    }


    /**
     * @api {POST} /UpdateDataEvent 更新大事记
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName UpdateDataEvent
     * @apiDescription 更新大事记
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} id 大事记ID
     * @apiParam (请求体) {String} name 大事记名称
     * @apiParam (请求体) {Int} frequency 频率,0:单次  1:每年
     * @apiParam (请求体) {String} timeRange 开始-结束时间,json格式
     * @apiParam (请求体) {String} [exValue] 拓展属性
     * @apiParam (请求体) {Int[]} monitorItemIDList 监测项目ID列表
     * @apiParamExample 请求体示例
     * {"projectID":1,"id":1,"name":"1","frequency":1,"monitorItemIDList":[1,2],
     * "timeRange": "[{\"startTime\": \"2023-10-18 16:02:52\", \"endTime\": \"2023-10-18 16:02:52\"}, {\"startTime\": \"2023-11-18 16:02:52\", \"endTime\": \"2023-11-18 16:02:52\"}, {\"startTime\": \"2023-12-18 16:02:52\", \"endTime\": \"2023-12-18 16:02:52\"}]"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateDataEvent
     */
    @Permission(permissionName = "mdmbase:UpdateDataEvent")
    @RequestMapping(value = "/UpdateDataEvent", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateDataEvent(@Validated @RequestBody UpdateDataEventParam pa) {
        monitorDataService.updateDataEvent(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryMonitorPointHasDataCount 查询监测点有无数据日期时间列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorPointHasDataCount
     * @apiDescription 查询监测点有无数据日期时间列表, 最低支持到日
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点列表,[1-100],必须为同一种监测类型
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end 结束时间
     * @apiParam (请求体) {Int} density 密度,(日:3 月:5 年:6)
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorPointIDList":[1,2],"begin":"2023-10-18 16:02:52","end":"2023-10-18 16:02:52","density":1}
     * @apiSuccess (返回结果) {Object} data 结果
     * @apiSuccess (返回结果) {String[]} data.dataList 监测点有数据列表，日格式:yyyy-MM-dd,月格式:yyyy-MM,年格式:yyyy
     * @apiSampleRequest off
     * @apiPermission 项目权限:mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryMonitorPointHasDataCount", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorPointHasDataCount(@Valid @RequestBody QueryMonitorPointHasDataCountParam pa) {
        return monitorDataService.queryMonitorPointHasDataCount(pa);
    }


    /**
     * @api {POST} /QueryDisMonitorTypeHasDataCountByMonitorPoints 查询通用监测点有无数据日期时间列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryDisMonitorTypeHasDataCountByMonitorPoints
     * @apiDescription 查询通用监测点有无数据日期时间列表, 可以跨监测类型, 最低支持到日
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点列表,[1-100]
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end 结束时间
     * @apiParam (请求体) {Int} density 密度,(日:3 月:5 年:6)
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorPointIDList":[1,2],"begin":"2023-10-18 16:02:52","end":"2023-10-18 16:02:52","density":1}
     * @apiSuccess (返回结果) {Object} data 结果
     * @apiSuccess (返回结果) {String[]} data.dataList 监测点有数据列表，日格式:yyyy-MM-dd,月格式:yyyy-MM,年格式:yyyy
     * @apiSampleRequest off
     * @apiPermission 项目权限:mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryDisMonitorTypeHasDataCountByMonitorPoints", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDisMonitorTypeHasDataCountByMonitorPoints(@Valid @RequestBody QueryDisMonitorTypeHasDataCountByMonitorPointsParam pa) {
        return monitorDataService.queryDisMonitorTypeHasDataCountByMonitorPoints(pa);
    }

    /**
     * @api {POST} /QueryMonitorTypeConfiguration 查询监测类型的预定义密度与统计方式
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorTypeConfiguration
     * @apiDescription 查询监测类型的预定义密度与统计方式
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [monitorType] 监测类型
     * @apiParamExample 请求体示例
     * {"companyID":1,"monitorType":1}
     * @apiSuccess (返回结果) {Object[]} data 监测类型标识
     * @apiSuccess (返回结果) {Int} data.monitorType 监测类型标识
     * @apiSuccess (返回结果) {String} data.typeName 监测类型名称
     * @apiSuccess (返回结果) {Int[]} [data.displayDensity] 预定义密度,(全部:1 小时:2 日:3 周:4 月:5 年:6)
     * @apiSuccess (返回结果) {Int[]} [data.statisticalMethods] 统计方式,(最新一条:1 平均:2 阶段累积:3 阶段变化:4)
     * @apiSampleRequest off
     * @apiPermission 系统权限:mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryMonitorTypeConfiguration", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypeConfiguration(@Valid @RequestBody QueryMonitorTypeConfigurationParam pa) {
        return monitorDataService.queryMonitorTypeConfiguration(pa);
    }

    /**
     * @api {POST} /QueryMonitorTypeFieldList 查询监测项目绑定的监测子类型
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorTypeFieldList
     * @apiDescription 查询监测项目绑定的监测子类型
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorItemID":1}
     * @apiSuccess (响应结果) {Object[]} data   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} data.fieldName   字段名称
     * @apiSuccess (响应结果) {String} data.engUnit 英文单位
     * @apiSuccess (响应结果) {String} data.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} data.unitClass  单位类型
     * @apiSuccess (响应结果) {String} data.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorTypeFieldList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorTypeFieldList(@Validated @RequestBody QueryMonitorTypeFieldParam pa) {
        return monitorDataService.queryMonitorTypeFieldList(pa);
    }

    /**
     * @api {POST} /QueryMonitorPointDataPage 查询监测点数据列表分页
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryMonitorPointDataPage
     * @apiDescription 查询监测点数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} densityType 密度,(全部:1 小时:2 日:3 周:4 月:5 年:6),查询最新数据默认传1
     * @apiParam (请求体) {Int} statisticsType 统计方式,(最新一条:1 平均:2 阶段累积:3 阶段变化:4),查询最新数据默认传1
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorType":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31","densityType":0,"statisticsType":0,
     * "pageSize":10, "currentPage":1}
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType          监测类型
     * @apiSuccess (响应结果) {String} currentPageData.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} currentPageData.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Object[]} [currentPageData.sensorList]      传感器信息
     * @apiSuccess (响应结果) {Int} currentPageData.sensorList.sensorID    传感器ID
     * @apiSuccess (响应结果) {Int} currentPageData.sensorList.projectID 项目ID
     * @apiSuccess (响应结果) {Int} currentPageData.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.sensorList.sensorName  传感器名称
     * @apiSuccess (响应结果) {Object[]} currentPageData.sensorList.multiSensorData   多传感器数据
     * @apiSuccess (响应结果) {Int} currentPageData.sensorList.multiSensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {DateTime} currentPageData.sensorList.multiSensorData.time  数据采集时间
     * @apiSuccess (响应结果) {T} currentPageData.sensorList.multiSensorData.data  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Object[]} currentPageData.fieldList   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} currentPageData.fieldList.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} currentPageData.fieldList.fieldName   字段名称
     * @apiSuccess (响应结果) {String} currentPageData.fieldList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} currentPageData.fieldList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} currentPageData.fieldList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} currentPageData.fieldList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorPointDataPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointDataPage(@Validated @RequestBody QueryMonitorPointDataPageParam pa) {
        return monitorDataService.queryMonitorPointDataPage(pa);
    }

}
