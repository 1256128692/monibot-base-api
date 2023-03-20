package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.service.ReservoirMonitorService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ReservoirMonitorDataController {


    private ReservoirMonitorService reservoirMonitorService;

    /**
     * @api {POST} /QueryCompanyMonitorPointNewDataList 查询公司下所有监测点最新数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiName QueryMonitorPointNewDataList
     * @apiDescription 查询公司下所有监测点最新数据列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} [projectTypeID] 工程类型(水库:1 河道:2 提防:3 流域:4 尾矿库:5 基坑:6)
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求体) {String} [areaCode] 行政区划编码
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectTypeID":1,"areaCode":"110102014","monitorType":11,"monitorItemID":12}
     * @apiSuccess (响应结果) {Object[]} data                 结果列表
     * @apiSuccess (响应结果) {Int} data.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType          监测类型
     * @apiSuccess (响应结果) {String} data.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} data.monitorTypeName   监测类型别名
     * @apiSuccess (响应结果) {Int} data.monitorItemID        监测项目ID
     * @apiSuccess (响应结果) {String} data.monitorItemName   监测项目名称
     * @apiSuccess (响应结果) {String} data.monitorItemAlias  监测项目别名
     * @apiSuccess (响应结果) {Int} data.projectTypeID        工程项目类型ID
     * @apiSuccess (响应结果) {String} data.projectTypeName   工程项目类型名称
     * @apiSuccess (响应结果) {Int} data.projectID            工程项目ID
     * @apiSuccess (响应结果) {String} data.projectName       工程项目名称
     * @apiSuccess (响应结果) {String} data.projectShortName  工程项目简称
     * @apiSuccess (响应结果) {String} data.location          四级行政区域信息
     * @apiSuccess (响应结果) {String} data.locationInfo      第四级区域名称
     * @apiSuccess (响应结果) {String} data.gpsLocation       监测点地图经纬度
     * @apiSuccess (响应结果) {String} data.imageLocation     监测点底图位置
     * @apiSuccess (响应结果) {Object[]} data.sensorList      传感器信息
     * @apiSuccess (响应结果) {Int} data.sensorList.ID        传感器id
     * @apiSuccess (响应结果) {Int} data.sensorList.projectID 项目id
     * @apiSuccess (响应结果) {Int} data.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {Byte} data.sensorList.status         传感器状态 -1 无数据 0 正常 1,2,3,4对应预警级别
     * @apiSuccess (响应结果) {Bool} data.multiSensor        是否为关联多传感器
     * @apiSuccess (响应结果) {Object} data.sensorData       传感器最新数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {Int} data.sensorData.sensorID         传感器ID
     * @apiSuccess (响应结果) {DateTime} data.sensorData.time       数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorData.data              传感器数据(动态值)，参考监测项目属性字段列表
     * @apiSuccess (响应结果) {T} sensorData.data        传感器数据(动态值)，参考监测项目属性字段列表
     * @apiSuccess (响应结果) {Object[]} multiSensorData   多传感器最新数据
     * @apiSuccess (响应结果) {Int} multiSensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Int} multiSensorData.time      传感器ID
     * @apiSuccess (响应结果) {Double} multiSensorData.deep  区分值，如:深度
     * @apiSuccess (响应结果) {T} multiSensorData.data  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Object[]} data.fieldList   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldList.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} data.fieldList.fieldName   字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSuccessExample 响应结果示例
     * "data": [
     *         {
     *             "projectID": 220,
     *             "projectTypeID": 1,
     *             "monitorPointID": 5,
     *             "monitorType": 9,
     *             "projectName": "水库测试项目3（勿删）",
     *             "projectShortName": "墒情监测项目1",
     *             "projectTypeName": "水库",
     *             "monitorPointName": "墒情监测点1",
     *             "monitorTypeName": "",
     *             "monitorItemID": 2,
     *             "monitorItemName": "",
     *             "monitorItemAlias": "",
     *             "location": "{\"province\":\"420000\",\"city\":\"420100\",\"area\":\"420115\",\"town\":\"420115404\"}",
     *             "locationInfo": "江夏梁子湖风景区",
     *             "gpsLocation": "1",
     *             "imageLocation": "1",
     *             "sensorList": [
     *                 {
     *                     "projectID": 220,
     *                     "templateID": 1,
     *                     "dataSourceID": "111",
     *                     "dataSourceComposeType": 1,
     *                     "monitorType": 9,
     *                     "name": "9_1",
     *                     "alias": "9_1",
     *                     "kind": 1,
     *                     "displayOrder": 6,
     *                     "monitorPointID": 5,
     *                     "configFieldValue": "{\"deep\":10}",
     *                     "exValues": "测试数据",
     *                     "status": 0,
     *                     "warnNoData": false,
     *                     "monitorBeginTime": "2023-03-16 16:24:31",
     *                     "imagePath": null,
     *                     "id": 6
     *                 },
     *                 {
     *                     "projectID": 220,
     *                     "templateID": 1,
     *                     "dataSourceID": "111",
     *                     "dataSourceComposeType": 1,
     *                     "monitorType": 9,
     *                     "name": "9_2",
     *                     "alias": "9_2",
     *                     "kind": 1,
     *                     "displayOrder": 7,
     *                     "monitorPointID": 5,
     *                     "configFieldValue": "{\"deep\":20}",
     *                     "exValues": "测试数据",
     *                     "status": 0,
     *                     "warnNoData": false,
     *                     "monitorBeginTime": "2023-03-16 16:27:58",
     *                     "imagePath": null,
     *                     "id": 7
     *                 }
     *             ],
     *             "multiSensor": true,
     *             "sensorData": null,
     *             "multiSensorData": [
     *                 {
     *                     "time": "2023-03-16 14:26:03.326",
     *                     "v1": 21.2,
     *                     "sensorID": 6
     *                 },
     *                 {
     *                     "time": "2023-03-16 14:26:03.326",
     *                     "v1": 22.2,
     *                     "sensorID": 7
     *                 }
     *             ],
     *             "time": "2023-03-16 14:26:03.326",
     *             "fieldList": [
     *                 {
     *                     "fieldToken": "v1",
     *                     "fieldName": "土壤含水量",
     *                     "fieldOrder": 1,
     *                     "fieldType": "DOUBLE",
     *                 }
     *             ]
     *         }]
     * @apiSampleRequest off
     * @apiPermission 系统权限
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryCompanyMonitorPointNewDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryCompanyMonitorPointNewDataList(@Validated @RequestBody QueryMonitorPointListParam pa) {
        return reservoirMonitorService.queryMonitorPointList(pa);
    }


    /**
     * @api {POST} /QuerySingleMonitorPointNewData 查询单个监测点最新数据详情
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiDescription 查询单个监测点最新数据详情
     * @apiName QuerySingleMonitorPointNewData
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiSuccess (响应结果) {Int} monitorPointID      监测点ID
     * @apiSuccess (响应结果) {String} monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} monitorType         监测类型
     * @apiSuccess (响应结果) {String} monitorTypeName  监测类型名称
     * @apiSuccess (响应结果) {String} monitorTypeName  监测类型别名
     * @apiSuccess (响应结果) {Int} monitorItemID       监测项目ID
     * @apiSuccess (响应结果) {String} monitorItemName  监测项目名称
     * @apiSuccess (响应结果) {String} monitorItemAlias  监测项目别名
     * @apiSuccess (响应结果) {Int} projectTypeID       工程项目类型ID
     * @apiSuccess (响应结果) {String} projectTypeName     工程项目类型名称
     * @apiSuccess (响应结果) {Int} projectID           工程项目ID
     * @apiSuccess (响应结果) {String} projectName      工程项目名称
     * @apiSuccess (响应结果) {String} projectShortName 工程项目简称
     * @apiSuccess (响应结果) {String} location         四级行政区域信息
     * @apiSuccess (响应结果) {String} locationInfo     第四级区域名称
     * @apiSuccess (响应结果) {Bool} multiSensor       是否为关联多传感器
     * @apiSuccess (响应结果) {String} sensorStatus     传感器状态 -1:无数据 0:正常 1-4:警报等级
     * @apiSuccess (响应结果) {Object} sensorData        传感器最新数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {Int} sensorData.sid       传感器ID
     * @apiSuccess (响应结果) {DateTime} sensorData.time     数据采集时间
     * @apiSuccess (响应结果) {T} sensorData.data        传感器数据(动态值)，参考监测项目属性字段列表
     * @apiSuccess (响应结果) {Object[]} multiSensorData   多传感器最新数据
     * @apiSuccess (响应结果) {Int} multiSensorData.sid      传感器ID
     * @apiSuccess (响应结果) {Double} multiSensorData.deep  区分值，如:深度
     * @apiSuccess (响应结果) {T} multiSensorData.data  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Object[]} fieldList 监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QuerySingleMonitorPointNewData", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object querySingleMonitorPointNewData(@Validated @RequestBody QueryMonitorPointDescribeParam pa) {
        return reservoirMonitorService.querySingleMonitorPointNewData(pa);
    }


    /**
     * @api {POST} /QueryMonitorPointTypeStatistics 查询统计当前公司下的监测点类型数量以及项目状态数量
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiDescription 查询统计当前公司下的监测点类型数量以及项目状态数量
     * @apiName StatisticsMonitorPointType
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} queryType 查询类型(0:环境监测, 1:安全监测, 2:工情监测 3:防洪调度指挥监测 4:视频监测)
     * @apiSuccess (响应结果) {Object[]} typeInfo          监测类型统计信息
     * @apiSuccess (响应结果) {Int} typeInfo.monitorType   监测类型
     * @apiSuccess (响应结果) {String} typeInfo.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} typeInfo.monitorTypeAlias   监测类型别名
     * @apiSuccess (响应结果) {Int} typeInfo.pointCount   监测类型监测点统计数量
     * @apiSuccess (响应结果) {Object} warnInfo          监测点预警统计信息
     * @apiSuccess (响应结果) {Int} warnInfo.normalCount         正常数量
     * @apiSuccess (响应结果) {Int} warnInfo.noDataCount         无数据数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelOneCount   一级警报数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelTwoCount   二级警报数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelThreeCount 三级警报数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelFourCount  四级警报数量
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryMonitorPointTypeStatistics", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointTypeStatistics(@Validated @RequestBody StatisticsMonitorPointTypeParam pa) {
        return null;
    }

    /**
     * @api {POST} /QueryMonitorPointHistoryDataList 查询监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiDescription 查询监测点历史数据列表
     * @apiName QueryMonitorPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2021-09-27 00:00:00","end":"2021-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Int} monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorDataList 传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorDataList.sid      传感器ID
     * @apiSuccess (响应结果) {DateTime} sensorDataList.time   采集时间
     * @apiSuccess (响应结果) {T} sensorDataList.data      传感器数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldCalOrder  属性字段排序
     * @apiSuccessExample 响应结果示例
     * [{
     * "monitorPointID":1,
     * "monitorPointName":"xxx监测点",
     * "sensorDataList":[
     * {
     * "sensorID":2,
     * "time":"2021-09-27 00:00:00",
     * "flow":1.5,
     * "speed":2.8}
     * ],
     * "fieldList": [
     * {"fieldToken":"flow","fieldName":"流量"},
     * {"fieldToken":"speed","fieldName":"速度"}
     * ]
     * }]
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryMonitorPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointHistoryDataList(@Validated @RequestBody QueryMonitorPointSensorDataListParam pa) {
        return null;
    }

    /**
     * @api {POST} /QuerySmcPointHistoryDataList 查询墒情监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiDescription 查询墒情监测点历史数据列表
     * @apiName QuerySmcPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2022-09-27 00:00:00","end":"2022-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Int} monitorPointID         监测点ID
     * @apiSuccess (响应结果) {Int} monitorPointName       监测点名称
     * @apiSuccess (响应结果) {Object[]} dataList   数据列表
     * @apiSuccess (响应结果) {DateTime} dataList.time     数据采集时间
     * @apiSuccess (响应结果) {Object[]} dataList.timeDataList     时刻数据列表
     * @apiSuccess (响应结果) {Int} dataList.timeDataList.sid      传感器ID
     * @apiSuccess (响应结果) {Double} dataList.timeDataList.depth    深度
     * @apiSuccess (响应结果) {Double} dataList.timeDataList.value  土壤含水量(%)
     * @apiSuccessExample 响应结果示例
     * {
     * "monitorPointID":1,
     * "monitorPointName":"测点1",
     * "dataList":[
     * {
     * "time":"2023-03-01 02:00:00",
     * "timeDataList":[{"sid":1,"depth":10,"value":10},{"sid":2,"depth":20,"value":20},{"sid":3,"depth":30,"value":30}]
     * },
     * {
     * "time":"2023-03-01 04:00:00",
     * "timeDataList":[{"sid":1,"depth":10,"value":10},{"sid":2,"depth":20,"value":20},{"sid":3,"depth":30,"value":30}]
     * },
     * ]
     * }
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QuerySmcPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object querySmcPointHistoryDataList(@Validated @RequestBody QueryMonitorPointSensorDataListParam pa) {
        return null;
    }


    /**
     * @api {POST} /QueryRainPointHistoryDataList 查询雨量监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiDescription 查询雨量监测点历史数据列表
     * @apiName QueryRainPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2022-09-27 00:00:00","end":"2022-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Int} monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} monitorPointName 监测点名称
     * @apiSuccess (响应结果) {String} monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorDataList             传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorDataList.sid              传感器ID
     * @apiSuccess (响应结果) {DateTime} sensorDataList.time        采集时间
     * @apiSuccess (响应结果) {Double} sensorDataList.rainfall      降雨量
     * @apiSuccess (响应结果) {Double} sensorDataList.currentRainfall   当前降雨量
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldCalOrder  属性字段排序
     * @apiSuccessExample 响应结果示例
     * {
     * "monitorPointID":1,
     * "monitorPointName":"测点1",
     * "floodLimitWater":300.1,
     * "designFloodWater":300.1,
     * "checkFloodWater":300.1,
     * "deadWater":300.1,
     * "sensorDataList":[
     * {"sensorID":1,
     * "time":"2021-09-27 00:00:00",
     * "damWater":100.5,
     * "capacity":200.8}
     * ]
     * }
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryRainPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryRainPointHistoryDataList(@Validated @RequestBody QueryMonitorPointSensorDataListParam pa) {
        return null;
    }

    /**
     * @api {POST} /QueryMonitorPointListHistoryDataList 查询多监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点数据模块
     * @apiDescription 查询多监测点历史数据列表
     * @apiName QueryMonitorPointListHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} pointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"pointIDList":[1,2,3],"density":"2h","begin":"2021-09-27 00:00:00","end":"2021-09-28 00:00:00","projectID":66}
     * @apiSuccess (响应结果) {Object[]} data   结果数据
     * @apiSuccess (响应结果) {DateTime} data.time     数据采集时间
     * @apiSuccess (响应结果) {Object[]} data.timeDataList     时刻数据列表
     * @apiSuccess (响应结果) {Int} data.timeDataList.pointID      监测点ID
     * @apiSuccess (响应结果) {Int} data.timeDataList.pointName      监测点名称
     * @apiSuccess (响应结果) {Double} data.timeDataList.sid   传感器ID
     * @apiSuccess (响应结果) {T} data.timeDataList.value  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldCalOrder  属性字段排序
     * @apiSuccessExample 响应结果示例
     * [{
     * "time":"2023-02-27 00:00:00",
     * "timeDataList":[
     * {
     * "pointID":1,
     * "pointName":"测点1",
     * "value":1.5},
     * {
     * "pointID":2,
     * "pointName":"测点2",
     * "value":2.5}
     * ]
     * },
     * {
     * "time":"2023-02-27 02:00:00",
     * "timeDataList":[
     * {
     * "pointID":1,
     * "pointName":"测点1",
     * "value":1.6},
     * {
     * "pointID":2,
     * "pointName":"测点2",
     * "value":2.4}
     * ]
     * }]
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @RequestMapping(value = "/QueryMonitorPointListHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointListHistoryDataList(@Validated @RequestBody QueryMonitorPointSensorDataListParam pa) {
        return null;
    }

}


