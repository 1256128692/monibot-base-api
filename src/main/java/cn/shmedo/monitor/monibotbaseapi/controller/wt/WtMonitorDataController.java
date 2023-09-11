package cn.shmedo.monitor.monibotbaseapi.controller.wt;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WtMonitorDataController {


    private WtMonitorService wtMonitorService;

    /**
     * @api {POST} /QueryCompanyMonitorPointNewDataList 查询公司下所有监测点最新数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiName QueryMonitorPointNewDataList
     * @apiDescription 查询公司下所有监测点最新数据列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} [projectIDList] 工程ID列表
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} [projectTypeID] 工程类型(水库:1 河道:2 提防:3 流域:4 尾矿库:5 基坑:6)
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求体) {String} [monitorItemName] 监测项目名称,精准查询不支持模糊
     * @apiParam (请求体) {Int} monitorClassType 监测类别
     * @apiParam (请求体) {String} [areaCode] 行政区划编码
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectTypeID":1,"areaCode":"110102014","monitorType":11,"monitorItemID":12}
     * @apiSuccess (响应结果) {Object[]} data                 结果列表
     * @apiSuccess (响应结果) {Int} data.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType          监测类型
     * @apiSuccess (响应结果) {String} data.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} data.monitorTypeAlias  监测类型别名
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
     * @apiSuccess (响应结果) {String} data.density       监测点查询密度
     * @apiSuccess (响应结果) {Object[]} data.sensorList      传感器信息
     * @apiSuccess (响应结果) {Int} data.sensorList.id        传感器id
     * @apiSuccess (响应结果) {Int} data.sensorList.projectID 项目id
     * @apiSuccess (响应结果) {Int} data.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {Byte} data.sensorList.status         传感器状态 -1 无数据 0 正常 1,2,3,4对应预警级别
     * @apiSuccess (响应结果) {Bool} data.multiSensor        是否为关联多传感器
     * @apiSuccess (响应结果) {Object} data.sensorData       传感器最新数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {Int} data.sensorData.sensorID         传感器ID
     * @apiSuccess (响应结果) {DateTime} data.sensorData.time       数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorData.data              传感器数据(动态值)，参考监测项目属性字段列表
     * @apiSuccess (响应结果) {Object[]} data.multiSensorData   多传感器最新数据
     * @apiSuccess (响应结果) {Int} data.multiSensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Int} data.multiSensorData.time      传感器ID
     * @apiSuccess (响应结果) {Double} data.multiSensorData.deep  区分值，如:深度
     * @apiSuccess (响应结果) {T} data.multiSensorData.data  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Object[]} data.fieldList   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldList.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} data.fieldList.fieldName   字段名称
     * @apiSuccess (响应结果) {Int} data.fieldList.fieldStatisticsType   属性类型 1 - 基础属性  2 - 扩展属性 3 - 扩展配置
     * @apiSuccess (响应结果) {String} data.fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} data.dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} data.dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} data.dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} data.dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} data.dataUnitList.unitDesc  单位类型描述
     * @apiSuccessExample 响应结果示例
     * "data": [
     * {
     * "projectID": 220,
     * "projectTypeID": 1,
     * "monitorPointID": 5,
     * "monitorType": 9,
     * "projectName": "水库测试项目3（勿删）",
     * "projectShortName": "墒情监测项目1",
     * "projectTypeName": "水库",
     * "monitorPointName": "墒情监测点1",
     * "monitorTypeName": "",
     * "monitorItemID": 2,
     * "monitorItemName": "",
     * "monitorItemAlias": "",
     * "location": "{\"province\":\"420000\",\"city\":\"420100\",\"area\":\"420115\",\"town\":\"420115404\"}",
     * "locationInfo": "江夏梁子湖风景区",
     * "gpsLocation": "1",
     * "imageLocation": "1",
     * "sensorList": [
     * {
     * "projectID": 220,
     * "templateID": 1,
     * "dataSourceID": "111",
     * "dataSourceComposeType": 1,
     * "monitorType": 9,
     * "name": "9_1",
     * "alias": "9_1",
     * "kind": 1,
     * "displayOrder": 6,
     * "monitorPointID": 5,
     * "configFieldValue": "{\"deep\":10}",
     * "exValues": "测试数据",
     * "status": 0,
     * "warnNoData": false,
     * "monitorBeginTime": "2023-03-16 16:24:31",
     * "imagePath": null,
     * "id": 6
     * },
     * {
     * "projectID": 220,
     * "templateID": 1,
     * "dataSourceID": "111",
     * "dataSourceComposeType": 1,
     * "monitorType": 9,
     * "name": "9_2",
     * "alias": "9_2",
     * "kind": 1,
     * "displayOrder": 7,
     * "monitorPointID": 5,
     * "configFieldValue": "{\"deep\":20}",
     * "exValues": "测试数据",
     * "status": 0,
     * "warnNoData": false,
     * "monitorBeginTime": "2023-03-16 16:27:58",
     * "imagePath": null,
     * "id": 7
     * }
     * ],
     * "multiSensor": true,
     * "sensorData": null,
     * "multiSensorData": [
     * {
     * "time": "2023-03-16 14:26:03.326",
     * "v1": 21.2,
     * "sensorID": 6
     * },
     * {
     * "time": "2023-03-16 14:26:03.326",
     * "v1": 22.2,
     * "sensorID": 7
     * }
     * ],
     * "time": "2023-03-16 14:26:03.326",
     * "fieldList": [
     * {
     * "fieldToken": "v1",
     * "fieldName": "土壤含水量",
     * "fieldOrder": 1,
     * "fieldType": "DOUBLE",
     * }
     * ]
     * }]
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryCompanyMonitorPointNewDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryCompanyMonitorPointNewDataList(@Validated @RequestBody QueryMonitorPointListParam pa) {
        return wtMonitorService.queryMonitorPointList(pa);
    }


    /**
     * @api {POST} /QuerySingleMonitorPointNewData 查询单个监测点最新数据详情
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询单个监测点最新数据详情
     * @apiName QuerySingleMonitorPointNewData
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiSuccess (响应结果) {Int} monitorPointID      监测点ID
     * @apiSuccess (响应结果) {String} monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} monitorType         监测类型
     * @apiSuccess (响应结果) {String} monitorTypeName  监测类型名称
     * @apiSuccess (响应结果) {String} monitorTypeAlias 监测类型别名
     * @apiSuccess (响应结果) {Int} monitorItemID       监测项目ID
     * @apiSuccess (响应结果) {String} monitorItemName  监测项目名称
     * @apiSuccess (响应结果) {String} monitorItemAlias 监测项目别名
     * @apiSuccess (响应结果) {Int} projectTypeID       工程项目类型ID
     * @apiSuccess (响应结果) {String} projectTypeName  工程项目类型名称
     * @apiSuccess (响应结果) {Int} projectID           工程项目ID
     * @apiSuccess (响应结果) {String} projectName      工程项目名称
     * @apiSuccess (响应结果) {String} projectShortName 工程项目简称
     * @apiSuccess (响应结果) {String} location         四级行政区域信息
     * @apiSuccess (响应结果) {String} locationInfo     第四级区域名称
     * @apiSuccess (响应结果) {String} density          监测点查询密度
     * @apiSuccess (响应结果) {Bool} multiSensor        是否为关联多传感器
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
     * @apiSuccess (响应结果) {Int} fieldList.fieldStatisticsType   属性类型 1 - 基础属性  2 - 扩展属性 3 - 扩展配置
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QuerySingleMonitorPointNewData", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object querySingleMonitorPointNewData(@Validated @RequestBody QueryMonitorPointDescribeParam pa) {
        return wtMonitorService.querySingleMonitorPointNewData(pa);
    }


    /**
     * @api {POST} /QueryMonitorPointTypeStatistics 查询统计当前公司下的监测点类型数量以及传感器状态数量
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询统计当前公司下的监测点类型数量以及项目状态数量
     * @apiName StatisticsMonitorPointType
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} queryType 查询类型(0:环境监测, 1:安全监测, 2:工情监测 3:防洪调度指挥监测 4:视频监测 5:水情监测 6:雨晴监测 7:农情监测 8:工情监测)
     * @apiSuccess (响应结果) {Object[]} typeInfoList          监测类型统计信息
     * @apiSuccess (响应结果) {Int} typeInfoList.monitorType   监测类型
     * @apiSuccess (响应结果) {String} typeInfoList.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} typeInfoList.monitorTypeAlias   监测类型别名
     * @apiSuccess (响应结果) {Int} typeInfoList.pointCount   监测类型监测点统计数量
     * @apiSuccess (响应结果) {Int} typeInfoList.displayOrder   监测类型排序
     * @apiSuccess (响应结果) {Object[]} typeInfoList.monitorItemList   监测项目类型
     * @apiSuccess (响应结果) {Int} typeInfoList.monitorItemList.monitorItemID   监测项目ID
     * @apiSuccess (响应结果) {String} typeInfoList.monitorItemList.name   监测项目名称
     * @apiSuccess (响应结果) {String} typeInfoList.monitorItemList.alias   监测项目别名
     * @apiSuccess (响应结果) {Object} typeInfoList.warnInfo          监测点预警统计信息
     * @apiSuccess (响应结果) {Int} warnInfo.normalCount         正常数量
     * @apiSuccess (响应结果) {Int} warnInfo.noDataCount         无数据数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelOneCount   一级警报数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelTwoCount   二级警报数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelThreeCount 三级警报数量
     * @apiSuccess (响应结果) {Int} warnInfo.levelFourCount  四级警报数量
     * @apiSuccess (响应结果) {Object[]} typeInfoList.projectTypeList  工程类型信息
     * @apiSuccess (响应结果) {Int} projectTypeList.id  工程类型ID
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:DescribeBaseCompanyMonitor
     */
    @Permission(permissionName = "mdmbase:DescribeBaseCompanyMonitor")
    @RequestMapping(value = "/QueryMonitorPointTypeStatistics", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointTypeStatistics(@Validated @RequestBody StatisticsMonitorPointTypeParam pa) {
        return wtMonitorService.queryMonitorPointTypeStatistics(pa);
    }

    /**
     * @api {POST} /QueryMonitorPointHistoryDataList 查询监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询监测点历史数据列表
     * @apiName QueryMonitorPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度  30m:30分钟一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2021-09-27 00:00:00","end":"2021-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Object} monitorPoint 监测点信息
     * @apiSuccess (响应结果) {Int} monitorPoint.id   监测点ID
     * @apiSuccess (响应结果) {String} monitorPoint.name 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorList 传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorList.id      传感器ID
     * @apiSuccess (响应结果) {String} sensorList.name   传感器名称
     * @apiSuccess (响应结果) {Object[]} dataList   数据列表
     * @apiSuccess (响应结果) {Object[]} dataList.T     时刻数据列表
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryMonitorPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointHistoryDataList(@Validated @RequestBody QueryMonitorPointSensorDataListParam pa) {
        return wtMonitorService.queryMonitorPointHistoryDataList(pa);
    }

    /**
     * @api {POST} /QuerySmcPointHistoryDataList 查询墒情监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询墒情监测点历史数据列表
     * @apiName QuerySmcPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度   30m:30分钟一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2022-09-27 00:00:00","end":"2022-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Object} monitorPoint 监测点信息
     * @apiSuccess (响应结果) {Int} monitorPoint.id   监测点ID
     * @apiSuccess (响应结果) {String} monitorPoint.name 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorList 传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorList.id      传感器ID
     * @apiSuccess (响应结果) {String} sensorList.name   传感器名称
     * @apiSuccess (响应结果) {Object[]} dataList   数据列表
     * @apiSuccess (响应结果) {Object[]} dataList.T     时刻数据列表
     * @apiSuccess (响应结果) {Int} dataList.T.sensorID      传感器ID
     * @apiSuccess (响应结果) {Double} dataList.T.deep    深度
     * @apiSuccess (响应结果) {Double} dataList.T.v1  土壤含水量(%)
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSuccessExample 响应结果示例
     * [{
     * "monitorPoint": {
     * "projectID": 238,
     * "monitorType": 9,
     * "monitorItemID": 9,
     * "name": "墒情监测点3",
     * "id": 17
     * },
     * "sensorList": [
     * {
     * "projectID": 238,
     * "name": "9_11",
     * "alias": "9_11",
     * "kind": 1,
     * "displayOrder": 20,
     * "id": 20
     * },
     * {
     * "projectID": 238,
     * "name": "9_12",
     * "alias": "9_12",
     * "kind": 1,
     * "displayOrder": 21,
     * "id": 21
     * }
     * ],
     * "dataList": {
     * {
     * "deep": 10,
     * "time": "2023-03-22 12:00:00.000",
     * "v1": 27.3,
     * "sensorID": 20
     * },
     * {
     * "deep": 20,
     * "time": "2023-03-22 12:00:00.000",
     * "v1": 28.3,
     * "sensorID": 21
     * }
     * },
     * "fieldList": [
     * {
     * "fieldToken": "v1",
     * "fieldName": "土壤含水量",
     * "fieldOrder": 1,
     * "fieldExValue": "18",
     * }
     * ],
     * "dataUnitList": [
     * {
     * "engUnit": "",
     * "chnUnit": "无",
     * "unitClass": "无",
     * "unitDesc": "无",
     * "id": 18
     * }
     * ]
     * }]
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QuerySmcPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object querySmcPointHistoryDataList(@Validated @RequestBody QuerySmcPointHistoryDataListParam pa) {
        return wtMonitorService.querySmcPointHistoryDataList(pa);
    }


    /**
     * @api {POST} /QueryRainPointHistoryDataList 查询雨量监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询雨量监测点历史数据列表
     * @apiName QueryRainPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度   30m:30分钟一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2022-09-27 00:00:00","end":"2022-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Object} monitorPoint 监测点信息
     * @apiSuccess (响应结果) {Int} monitorPoint.id   监测点ID
     * @apiSuccess (响应结果) {String} monitorPoint.name 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorList 传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorList.id      传感器ID
     * @apiSuccess (响应结果) {String} sensorList.name   传感器名称
     * @apiSuccess (响应结果) {Object[]} dataList   数据列表
     * @apiSuccess (响应结果) {Object[]} dataList.T     时刻数据列表
     * @apiSuccess (响应结果) {Int} dataList.T.sensorID      传感器ID
     * @apiSuccess (响应结果) {Double} dataList.T.periodRainfall    降雨量
     * @apiSuccess (响应结果) {Double} dataList.T.currentRainfall  当前降雨量
     * @apiSuccess (响应结果) {Double} dailyRainfall   日降雨量
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSuccess (响应结果) {Object[]} dailyRainfallList   日降雨量列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryRainPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryRainPointHistoryDataList(@Validated @RequestBody QueryRainMonitorPointSensorDataListParam pa) {
        return wtMonitorService.queryRainPointHistoryDataList(pa);
    }

    /**
     * @api {POST} /QueryMonitorPointListHistoryDataList 查询多监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询多监测点历史数据列表,目前支持类型(水位)
     * @apiName QueryMonitorPointListHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointIDs 监测点ID列表,监测点必须属于同一监测类型
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度   30m:30分钟一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"pointIDList":[1,2,3],"density":"2h","begin":"2021-09-27 00:00:00","end":"2021-09-28 00:00:00","projectID":66}
     * @apiSuccess (响应结果) {Object[]} monitorPointList 监测点信息
     * @apiSuccess (响应结果) {Int} monitorPointList.id   监测点ID
     * @apiSuccess (响应结果) {String} monitorPointList.name 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorList 传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorList.id      传感器ID
     * @apiSuccess (响应结果) {String} sensorList.name   传感器名称
     * @apiSuccess (响应结果) {Object[]} dataList   数据列表
     * @apiSuccess (响应结果) {Object[]} dataList.T     时刻数据列表
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldCalOrder  属性字段排序
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryMonitorPointListHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointListHistoryDataList(@Validated @RequestBody QueryMonitorPointsSensorDataListParam pa) {
        return wtMonitorService.queryMonitorPointListHistoryDataList(pa);
    }



    /**
     * @api {POST} /QueryDisplacementPointHistoryDataList 查询内部三轴位移监测点历史数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询内部三轴位移监测点历史数据列表
     * @apiName QueryDisplacementPointHistoryDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度   30m:30分钟一组的密度),null或者all:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2022-09-27 00:00:00","end":"2022-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Object} monitorPoint 监测点信息
     * @apiSuccess (响应结果) {Int} monitorPoint.id   监测点ID
     * @apiSuccess (响应结果) {String} monitorPoint.name 监测点名称
     * @apiSuccess (响应结果) {Object[]} sensorList 传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorList.id      传感器ID
     * @apiSuccess (响应结果) {String} sensorList.name   传感器名称
     * @apiSuccess (响应结果) {Object[]} dataList   数据列表
     * @apiSuccess (响应结果) {Object[]} dataList.T     时刻数据列表
     * @apiSuccess (响应结果) {Int} dataList.T.sensorID      传感器ID
     * @apiSuccess (响应结果) {Double} dataList.T.deep    深度
     * @apiSuccess (响应结果) {Double} dataList.T.v1  位移
     * @apiSuccess (响应结果) {Object[]} fieldList         监测类型属性字段列表
     * @apiSuccess (响应结果) {String} fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {String} fieldList.fieldExValue  字段单位ID
     * @apiSuccess (响应结果) {Object[]} dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} dataUnitList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryDisplacementPointHistoryDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryDisplacementPointHistoryDataList(@Validated @RequestBody QueryDisplacementPointHistoryParam pa) {
        return wtMonitorService.queryDisplacementPointHistoryDataList(pa);
    }

    /**
     * @api {POST} /QueryDisplacementMonitorPointNewDataList 查询内部三轴位移所有监测点最新数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiName QueryDisplacementMonitorPointNewDataList
     * @apiDescription 查询公司下所有监测点最新数据列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} monitorType 监测类型(内部三轴位移)
     * @apiParam (请求体) {Int} [projectTypeID] 工程类型(水库:1 河道:2 提防:3 流域:4 尾矿库:5 基坑:6)
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求体) {String} [monitorItemName] 监测项目名称,精准查询不支持模糊
     * @apiParam (请求体) {Int} monitorClassType 监测类别
     * @apiParam (请求体) {String} [areaCode] 行政区划编码
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectTypeID":1,"areaCode":"110102014","monitorType":13,"monitorItemID":12}
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
     * @apiSuccess (响应结果) {String} data.density       监测点查询密度
     * @apiSuccess (响应结果) {Date} data.time            查询时间
     * @apiSuccess (响应结果) {Object[]} data.sensorList      传感器信息
     * @apiSuccess (响应结果) {Int} data.sensorList.id        传感器id
     * @apiSuccess (响应结果) {Int} data.sensorList.projectID 项目id
     * @apiSuccess (响应结果) {Int} data.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {Byte} data.sensorList.status         传感器状态 -1 无数据 0 正常 1,2,3,4对应预警级别
     * @apiSuccess (响应结果) {Object} data.sensorData       传感器最新数据,(最新时间内,数据值最大这笔数据展示出来,已经它对应的深度)
     * @apiSuccess (响应结果) {Double} data.sensorData.xDeep       A轴深度
     * @apiSuccess (响应结果) {Double} data.sensorData.xValue      A轴数据
     * @apiSuccess (响应结果) {Double} data.sensorData.yDeep       B轴深度
     * @apiSuccess (响应结果) {Double} data.sensorData.yValue      B轴数据
     * @apiSuccess (响应结果) {Double} data.sensorData.zDeep       C轴深度
     * @apiSuccess (响应结果) {Double} data.sensorData.zValue      C轴数据
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListCompanySensorData
     */
    @Permission(permissionName = "mdmbase:ListCompanySensorData")
    @RequestMapping(value = "/QueryDisplacementMonitorPointNewDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryDisplacementMonitorPointNewDataList(@Validated @RequestBody QueryDisplacementMonitorPointNewDataParam pa) {
        return wtMonitorService.queryDisplacementMonitorPointNewDataList(pa);
    }



    /**
     * @api {POST} /QueryMonitorItemFieldList 根据监测项目查询监测子类型列表信息
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据监测项目查询监测子类型列表信息
     * @apiName QueryMonitorItemFieldList
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiSuccess (返回结果) {Object} data 信息
     * @apiSuccess (响应结果) {Object[]} data.fieldList      监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldList.fieldToken 属性字段标志
     * @apiSuccess (响应结果) {String} data.fieldList.fieldName  属性字段名称
     * @apiSuccess (响应结果) {Int} data.fieldList.fieldUnitID  字段单位ID
     * @apiSuccess (响应结果) {Int} data.fieldList.fieldClass 字段类型(1:基础类型字段, 2:拓展类型字段)
     * @apiSuccess (响应结果) {Object[]} data.dataUnitList 字段单位列表
     * @apiSuccess (响应结果) {String} data.dataUnitList.id 字段单位ID
     * @apiSuccess (响应结果) {String} data.dataUnitList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} data.dataUnitList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} data.dataUnitList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} data.dataUnitList.unitDesc  单位类型描述
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryMonitorItemFieldList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorItemFieldList(@Validated @RequestBody QueryMonitorItemFieldListParam pa) {
        return wtMonitorService.queryMonitorItemFieldList(pa);
    }


    /**
     * @api {POST} /QueryMonitorPointHistoryAvgDataList 根据监测点查询传感器历史平均数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据监测点查询传感器历史平均数据列表
     * @apiName QueryMonitorPointHistoryAvgDataList
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日平均 2:月平均 3:年平均)
     * @apiSuccess (响应结果) {Object[]} data  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} data.projectID   工程ID
     * @apiSuccess (响应结果) {String} data.projectName 工程名称
     * @apiSuccess (响应结果) {String} data.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} data.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {Int} data.monitorType   监测类型(2:水位 3:流速 31:雨量)
     * @apiSuccess (响应结果) {String} data.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} data.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} data.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} data.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} data.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} data.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} data.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} data.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} data.sensorData.T   数据key(根据不同的监测类型,返回不同的监测key)
     * @apiSuccess (响应结果) {Date} data.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryMonitorPointHistoryAvgDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointHistoryAvgDataList(@Validated @RequestBody QueryMonitorPointHistoryAvgDataParam pa) {
        return wtMonitorService.queryMonitorPointHistoryAvgDataList(pa);
    }


    /**
     * @api {POST} /QueryMonitorPointHistoryAvgDataPage 根据监测点分页查询传感器历史平均数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据监测点分页查询传感器历史平均数据列表
     * @apiName QueryMonitorPointHistoryAvgDataPage
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日平均 2:月平均 3:年平均)
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.projectID   工程ID
     * @apiSuccess (响应结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (响应结果) {String} currentPageData.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType   监测类型(2:水位 3:流速 31:雨量)
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} currentPageData.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} currentPageData.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} currentPageData.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} currentPageData.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} currentPageData.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} currentPageData.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} currentPageData.sensorData.T   数据key(根据不同的监测类型,返回不同的监测key)
     * @apiSuccess (响应结果) {Date} currentPageData.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryMonitorPointHistoryAvgDataPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointHistoryAvgDataPage(@Validated @RequestBody QueryMonitorPointHistoryAvgDataPageParam pa) {
        return wtMonitorService.queryMonitorPointHistoryAvgDataPage(pa);
    }



    /**
     * @api {POST} /QuerySensorHistoryAvgDataList 根据传感器查询传感器历史平均数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据传感器查询传感器历史平均数据列表,传感器所属监测点必须属于同一个监测项目
     * @apiName QuerySensorHistoryAvgDataList
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} sensorIDList 传感器ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日平均 2:月平均 3:年平均)
     * @apiSuccess (响应结果) {Object[]} data  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} data.projectID   工程ID
     * @apiSuccess (响应结果) {String} data.projectName 工程名称
     * @apiSuccess (响应结果) {String} data.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} data.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType   监测类型(2:水位 3:流速 31:雨量)
     * @apiSuccess (响应结果) {Int} data.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} data.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} data.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} data.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} data.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} data.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} data.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} data.sensorData.T   数据key(根据不同的监测类型,返回不同的监测key)
     * @apiSuccess (响应结果) {Date} data.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QuerySensorHistoryAvgDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object querySensorHistoryAvgDataList(@Validated @RequestBody QuerySensorHistoryAvgDataParam pa) {
        return wtMonitorService.querySensorHistoryAvgDataList(pa);
    }


    /**
     * @api {POST} /QuerySensorHistoryAvgDataPage 根据传感器分页查询传感器历史平均数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据传感器分页查询传感器历史平均数据列表
     * @apiName QuerySensorHistoryAvgDataPage
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} sensorIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日平均 2:月平均 3:年平均)
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.projectID   工程ID
     * @apiSuccess (响应结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (响应结果) {String} currentPageData.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType   监测类型(2:水位 3:流速 31:雨量)
     * @apiSuccess (响应结果) {Int} currentPageData.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} currentPageData.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} currentPageData.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} currentPageData.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} currentPageData.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} currentPageData.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} currentPageData.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} currentPageData.sensorData.T   数据key(根据不同的监测类型,返回不同的监测key)
     * @apiSuccess (响应结果) {Date} currentPageData.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QuerySensorHistoryAvgDataPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object querySensorHistoryAvgDataPage(@Validated @RequestBody QuerySensorHistoryAvgDataPageParam pa) {
        return wtMonitorService.querySensorHistoryAvgDataPage(pa);
    }


    /**
     * @api {POST} /QueryRainPointHistorySumDataList 查询雨量类型监测点历史累加数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询雨量类型监测点历史累加数据列表
     * @apiName QueryRainPointHistorySumDataList
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日累加 2:月累加 3:年累加 4:一小时累加 5:三小时累加 6:六小时累加 7:十二小时累加)
     * @apiSuccess (响应结果) {Object[]} data  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} data.projectID   工程ID
     * @apiSuccess (响应结果) {String} data.projectName 工程名称
     * @apiSuccess (响应结果) {String} data.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} data.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType   监测类型(31:雨量)
     * @apiSuccess (响应结果) {Int} data.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} data.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} data.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} data.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} data.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} data.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} data.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} data.sensorData.rainfall  累加雨量
     * @apiSuccess (响应结果) {Date} data.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryRainPointHistorySumDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryRainPointHistorySumDataList(@Validated @RequestBody QueryRainPointHistorySumDataParam pa) {
        return wtMonitorService.queryRainPointHistorySumDataList(pa);
    }



    /**
     * @api {POST} /QueryRainPointHistorySumDataPage 分页查询雨量类型监测点历史累加数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 查询监测点历史平均数据列表
     * @apiName QueryRainPointHistorySumDataPage
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日累加 2:月累加 3:年累加 4:一小时累加 5:三小时累加 6:六小时累加 7:十二小时累加)
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.projectID   工程ID
     * @apiSuccess (响应结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (响应结果) {String} currentPageData.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType   监测类型(31:雨量)
     * @apiSuccess (响应结果) {Int} currentPageData.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} currentPageData.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} currentPageData.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} currentPageData.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} currentPageData.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} currentPageData.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} currentPageData.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} currentPageData.sensorData.rainfall  累加雨量
     * @apiSuccess (响应结果) {Date} currentPageData.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryRainPointHistorySumDataPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryRainPointHistorySumDataPage(@Validated @RequestBody QueryRainPointHistorySumDataPageParam pa) {
        return wtMonitorService.queryRainPointHistorySumDataPage(pa);
    }


    /**
     * @api {POST} /QueryWaterRainSensorHistoryAvgDataList 根据监测点查询水雨晴传感器历史平均数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据监测点分页查询水雨晴传感器历史平均数据列表
     * @apiName QueryWaterRainSensorHistoryAvgDataList
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日 2:月 3:年)
     * @apiSuccess (响应结果) {Object[]} data  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} data.projectID   工程ID
     * @apiSuccess (响应结果) {String} data.projectName 工程名称
     * @apiSuccess (响应结果) {String} data.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} data.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType   监测类型(2:水位 3:流速 31:雨量)
     * @apiSuccess (响应结果) {Int} data.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} data.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} data.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} data.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} data.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} data.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} data.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} data.sensorData.T   数据key(根据不同的监测类型,返回不同的监测key)
     * @apiSuccess (响应结果) {Date} data.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryWaterRainSensorHistoryAvgDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryWaterRainSensorHistoryAvgDataList(@Validated @RequestBody QueryWaterRainSensorHistoryAvgDataParam pa) {
        return wtMonitorService.queryWaterRainSensorHistoryAvgDataList(pa);
    }



    /**
     * @api {POST} /QueryWaterRainSensorHistoryAvgDataPage 根据监测点分页查询水雨晴传感器历史平均数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水利监测点数据模块
     * @apiDescription 根据监测点分页查询水雨晴传感器历史平均数据列表
     * @apiName QueryWaterRainSensorHistoryAvgDataPage
     * @apiParam (请求体) {Int[]} projectIDList  项目ID列表
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 1:日 2:月 3:年)
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData  传感器数据信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.projectID   工程ID
     * @apiSuccess (响应结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (响应结果) {String} currentPageData.projectShortName 工程短称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID   监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType   监测类型(2:水位 3:流速 31:雨量)
     * @apiSuccess (响应结果) {Int} currentPageData.sensorID      传感器ID
     * @apiSuccess (响应结果) {String} currentPageData.sensorName   传感器名称
     * @apiSuccess (响应结果) {String} currentPageData.sensorAlias   传感器别名
     * @apiSuccess (响应结果) {String} currentPageData.configFieldValue   传感器配置
     * @apiSuccess (响应结果) {String} currentPageData.exValues   传感器额外补充信息
     * @apiSuccess (响应结果) {Object} currentPageData.sensorData   传感器数据
     * @apiSuccess (响应结果) {Int} currentPageData.sensorData.sensorID   传感器ID
     * @apiSuccess (响应结果) {Double} currentPageData.sensorData.T   数据key(根据不同的监测类型,返回不同的监测key)
     * @apiSuccess (响应结果) {Date} currentPageData.time   时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ListBaseSensorData")
    @RequestMapping(value = "/QueryWaterRainSensorHistoryAvgDataPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryWaterRainSensorHistoryAvgDataPage(@Validated @RequestBody QueryWaterRainSensorHistoryAvgDataPageParam pa) {
        return wtMonitorService.queryWaterRainSensorHistoryAvgDataPage(pa);
    }

}


