package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.AddDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.DeleteBatchDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.QueryDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.dataEvent.UpdateDataEventParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.AddEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.DeleteBatchEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.QueryEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue.UpdateEigenValueParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.QueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata.QueryMonitorPointHasDataCountParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypeConfigurationParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointListParam;
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
     * @apiParam (请求体) {Int[]} [eigenvalueIDList] 特征值ID列表
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
     * @apiSuccess (响应结果) {Object[]} data.sensorList.maxSensorDataList 传感器最大数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {DateTime} data.sensorList.maxSensorDataList.time       数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorList.maxSensorDataList.data              传感器数据(动态值)，参考监测项目属性字段列表
     * @apiSuccess (响应结果) {Object[]} data.sensorList.minSensorDataList 传感器最小数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {DateTime} data.sensorList.minSensorDataList.time       数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorList.minSensorDataList.data              传感器数据(动态值)，参考监测项目属性字段列表
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
    public Object queryMonitorPointFilterDataList(@Validated @RequestBody QueryMonitorPointListParam pa) {
//        return wtMonitorService.queryMonitorPointList(pa);
        return null;
    }


    /**
     * @api {POST} /QueryRainMonitorPointDataList 查询雨量监测点数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryRainMonitorPointDataList
     * @apiDescription 查询雨量监测点数据列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} rainDensityType 密度,(0:2h 1:4h 2:6h 3:12h)
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorType":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31","rainDensityType":"1"}
     * @apiSuccess (响应结果) {Object[]} data                 结果列表
     * @apiSuccess (响应结果) {Int} data.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} data.monitorType          监测类型
     * @apiSuccess (响应结果) {String} data.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} data.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Date} data.time  数据采集时间
     * @apiSuccess (响应结果) {Object[]} data.sensorList      传感器信息
     * @apiSuccess (响应结果) {Int} data.sensorList.id        传感器ID
     * @apiSuccess (响应结果) {Int} data.sensorList.projectID 项目ID
     * @apiSuccess (响应结果) {Int} data.sensorList.monitorPointID  监测点ID
     * @apiSuccess (响应结果) {String} data.sensorList.name  传感器名称
     * @apiSuccess (响应结果) {Object[]} data.sensorDataList       传感器最新数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {Int} data.sensorDataList.sensorID         传感器ID
     * @apiSuccess (响应结果) {DateTime} data.sensorDataList.time       数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorDataList.data              传感器数据(动态值)，参考监测项目属性字段列表
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
    @RequestMapping(value = "/QueryRainMonitorPointDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryRainMonitorPointDataList(@Validated @RequestBody QueryMonitorPointListParam pa) {
//        return wtMonitorService.queryMonitorPointList(pa);
        return null;
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
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
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
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
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
     * @apiSuccess (响应结果) {String} [eigenvalueList.monitorTypeFieldName] 属性名称
     * @apiSuccess (响应结果) {String} eigenvalueList.name 特征值
     * @apiSuccess (响应结果) {Double} eigenvalueList.value 数值
     * @apiSuccess (响应结果) {Int} eigenvalueList.unitID 单位ID
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
     * @api {POST} /AddDataEvent 新增大事件
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName AddDataEvent
     * @apiDescription 新增大事件
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {String} name 大事件名称
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
     * @api {POST} /DeleteBatchDataEvent 删除大事件
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName DeleteBatchDataEvent
     * @apiDescription 删除大事件
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} eventIDList 大事件名称
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
     * @api {POST} /QueryDataEventList 查询大事件列表
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName QueryDataEventList
     * @apiDescription 查询大事件列表
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [monitorItemID] 项目ID
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorItemID":1}
     * @apiSuccess (返回结果) {Object[]} dataList 大事件ID
     * @apiSuccess (返回结果) {Int} dataList.id 大事件ID
     * @apiSuccess (返回结果) {Int} dataList.projectID 工程ID
     * @apiSuccess (返回结果) {String} dataList.name 大事件名称
     * @apiSuccess (返回结果) {Int} dataList.frequency 频率,0:单次  1:每年
     * @apiSuccess (返回结果) {String} dataList.frequencyStr 频率,0:单次  1:每年
     * @apiSuccess (返回结果) {String} dataList.timeRange 开始-结束时间,json格式
     * @apiSuccess (返回结果) {String} [dataList.exValue] 拓展属性
     * @apiSuccess (响应结果) {Object[]} dataList.monitorItemList 监测项目列表
     * @apiSuccess (响应结果) {Int} dataList.monitorItemList.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {String} dataList.monitorItemList.name 监测项目名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryDataEventList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryDataEventList(@Validated @RequestBody QueryDataEventParam pa) {
        return monitorDataService.queryDataEventList(pa);
    }


    /**
     * @api {POST} /UpdateDataEvent 更新大事件
     * @apiVersion 1.0.0
     * @apiGroup 监测通用数据模块
     * @apiName UpdateDataEvent
     * @apiDescription 更新大事件
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} id 大事记ID
     * @apiParam (请求体) {String} name 大事件名称
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
}
