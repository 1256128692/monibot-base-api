package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.service.SensorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-21 16:02
 **/
@RestController
@AllArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    /**
     * @api {POST} /SensorList 传感器列表
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName SensorList
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [sensorName] 传感器名称 模糊查询
     * @apiParam (请求体) {String} [monitorType] 监测类型名称 模糊查询
     * @apiParam (请求体) {String} [monitorPoint] 关联监测点名称
     * @apiParam (请求体) {String} [sensorID] 传感器ID
     * @apiParam (请求体) {String} [monitorTypeID] 监测类型ID
     * @apiParam (请求体) {String} [monitorPointID] 关联监测点ID
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.id 传感器ID
     * @apiSuccess (响应结果) {Int} data.monitorType 监测类型 (Code)
     * @apiSuccess (响应结果) {String} data.monitorTypeName 监测类型名称
     * @apiSuccess (响应结果) {String} data.name 传感器名称 (由系统自动生成不可修改)
     * @apiSuccess (响应结果) {String} data.alias 传感器别名
     * @apiSuccess (响应结果) {Int} data.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Int} data.monitorPointID 关联监测点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName 关联监测点名称
     * @apiSuccess (响应结果) {String} data.exValues 拓展信息
     * @apiSuccess (响应结果) {Boolean} data.enable 是否启用, 不启用将不会接收数据
     * @apiPermission mdmbase:ListSensor
     */
    @Permission(permissionName = "mdmbase:ListBaseSensor")
    @PostMapping(value = "/SensorList", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sensorList(@RequestBody @Validated SensorListRequest request) {
        return sensorService.sensorList(request);
    }

    /**
     * @api {POST} /SensorPage 传感器分页
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName SensorPage
     * @apiParam (请求体) {Int} pageSize 分页大小 (1-100)
     * @apiParam (请求体) {Int} currentPage 当前页码 (大于0)
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [keyword] 聚合模糊检索 (传感器名称, 关联监测点名称)
     * @apiParam (请求体) {String} [sensorName] 传感器名称 模糊查询
     * @apiParam (请求体) {String} [monitorType] 监测类型名称 模糊查询
     * @apiParam (请求体) {String} [monitorPoint] 关联监测点名称
     * @apiParam (请求体) {String} [sensorID] 传感器ID
     * @apiParam (请求体) {String} [monitorTypeID] 监测类型ID
     * @apiParam (请求体) {String} [monitorPointID] 关联监测点ID
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.totalCount 总条数
     * @apiSuccess (响应结果) {Int} data.totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} data.currentPageData 当前页数据
     * @apiSuccess (响应结果) {Int} data.currentPageData.id 传感器ID
     * @apiSuccess (响应结果) {Int} data.currentPageData.monitorType 监测类型 (Code)
     * @apiSuccess (响应结果) {String} data.currentPageData.monitorTypeName 监测类型名称
     * @apiSuccess (响应结果) {String} data.currentPageData.name 传感器名称 (由系统自动生成不可修改)
     * @apiSuccess (响应结果) {String} data.currentPageData.alias 传感器别名
     * @apiSuccess (响应结果) {Int} data.currentPageData.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Int} data.currentPageData.monitorPointID 关联监测点ID
     * @apiSuccess (响应结果) {String} data.currentPageData.monitorPointName 关联监测点名称
     * @apiSuccess (响应结果) {String} data.currentPageData.exValues 拓展信息
     * @apiSuccess (响应结果) {Boolean} data.currentPageData.enable 是否启用, 不启用将不会接收数据
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"totalPage": 1,"totalCount": 1,"currentPageData": [{"id": 0,"monitorType": 0,"monitorTypeName": "","name": "","alias": "","displayOrder": 0,"monitorPointID": 0,"monitorPointName": 0,"exValues": "","enable": false}]}}
     * @apiPermission mdmbase:ListSensor
     */
    @Permission(permissionName = "mdmbase:ListBaseSensor")
    @PostMapping(value = "/SensorPage", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sensorPage(@RequestBody @Validated SensorPageRequest request) {
        return sensorService.sensorPage(request);
    }

    /**
     * @api {POST} /DataSourceCatalog 数据源选择
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName DataSourceCatalog
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} [companyID] 公司ID， 该项为null则使用当前用户所在公司ID
     * @apiParam (请求体) {String} [dataSourceComposeType] 模板数据来源类型 默认为1 <br/>1单一物模型单一传感器 <br/>2多个物联网传感器（同一物模型多个或者不同物模型多个）<br/>3物联网传感器+监测传感器<br/>4单个监测传感器<br/>5多个监测传感器<br/>100API 推送
     * @apiParam (请求体) {Int} [monitorType] 监测类型
     * @apiParam (请求体) {String} [keyword] 检索关键字
     * @apiParamExample 请求体示例
     * {"monitorType":1,"dataSourceComposeType":"1","keyword":"11B","projectID":5411}
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {String} data.id 监测类型模板ID
     * @apiSuccess (响应结果) {String} data.name 监测类型模板名称
     * @apiSuccess (响应结果) {Int} data.dataSourceComposeType 模板数据来源类型
     * @apiSuccess (响应结果) {String} data.templateDataSourceID 监测类型模板分布式唯一ID
     * @apiSuccess (响应结果) {Int} data.monitorType 监测类型
     * @apiSuccess (响应结果) {Int} data.calType 计算类型 1 - 公式计算 2 - 脚本计算 3 - 外部HTTP计算 -1 不计算
     * @apiSuccess (响应结果) {Int} data.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {String} data.exValues 拓展信息
     * @apiSuccess (响应结果) {Int} data.createType 创建类型 0 - 系统创建 1 - 用户创建
     * @apiSuccess (响应结果) {Int} data.companyID 公司ID
     * @apiSuccess (响应结果) {Boolean} data.defaultTemplate 是否为默认模板
     * @apiSuccess (响应结果) {Object[]} data.dataSourceList 数据源列表
     * @apiSuccess (响应结果) {Int} data.dataSourceList.dataSourceType 数据源类型 1 - 物联网传感器 2 - 监测传感器 3 - 外部HTTP 4 - 脚本 5 - 公式
     * @apiSuccess (响应结果) {String} data.dataSourceList.templateDataSourceToken 模板数据源标识
     * @apiSuccess (响应结果) {Object[]} data.dataSourceList.childList 数据源列表
     * @apiSuccess (响应结果) {Int} data.dataSourceList.childList.id 设备ID/监测传感器ID
     * @apiSuccess (响应结果) {String} [data.dataSourceList.childList.deviceName] 设备名称/监测传感器名称
     * @apiSuccess (响应结果) {String} [data.dataSourceList.childList.uniqueToken] 设备传感器标识
     * @apiSuccess (响应结果) {String} [data.dataSourceList.childList.deviceToken] 设备Token
     * @apiSuccess (响应结果) {String} [data.dataSourceList.childList.alias] 监测传感器别名
     * @apiSuccess (响应结果) {Object[]} [data.dataSourceList.childList.sensorList] 传感器列表
     * @apiSuccess (响应结果) {String} data.dataSourceList.childList.sensorList.sensorName 传感器名称
     * @apiSuccess (响应结果) {String} data.dataSourceList.childList.sensorList.alias 传感器别名
     * @apiSuccess (响应结果) {String} data.dataSourceList.childList.sensorList.iotSensorType 传感器类型
     * @apiSuccess (响应结果) {Int} data.dataSourceList.childList.sensorList.id 传感器ID
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": [{"id": 0,"name": "物模型1","dataSourceComposeType": 1,"templateDataSourceID": "777d75e0-f2de-4c0b-a759-d98fe9091d05","monitorType": 1,"calType": 1,"displayOrder": 1,"exValues": "","createType": 0,"companyID": 0,"defaultTemplate": false,"dataSourceList": [{"dataSourceType": 1,"childList": [{"productID": 385,"deviceName": "test2023","deviceToken": "test2023","uniqueToken": "D8DB467AB743476CA4B6F6F909704FBB","sensorList": [{"id": 353,"sensorName": "103_1","iotSensorType": "103","alias": "103_1"}],"id": 15567}],"templateDataSourceToken": "103"},{"dataSourceType": 2,"childList": [{"id": 1,"deviceName": "10086_1","alias": "监测传感器"}],"templateDataSourceToken": "10086"}]}]}
     * @apiPermission mdmbase:ListDataSource
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor")
    @PostMapping(value = "/DataSourceCatalog", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object dataSourceCate(@RequestBody @Validated DataSourceCatalogRequest request) {
        return sensorService.dataSourceCatalog(request);
    }

    /**
     * @api {POST} /MonitorTypeCatalog 监测类型选择
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName MonitorTypeCatalog
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {String} [dataSourceComposeType] 模板数据来源类型 默认为1 <br/>1单一物模型单一传感器 <br/>2多个物联网传感器（同一物模型多个或者不同物模型多个）<br/>3物联网传感器+监测传感器<br/>4单个监测传感器<br/>5多个监测传感器<br/>100API 推送
     * @apiParam (请求参数) {String} [templateDataSourceID] 监测类型模板分布式唯一ID
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.id 监测类型ID
     * @apiSuccess (响应结果) {Int} data.monitorType 监测类型
     * @apiSuccess (响应结果) {String} data.typeName 监测类型名称
     * @apiSuccess (响应结果) {String} data.typeAlias 监测类型别名
     * @apiSuccess (响应结果) {Int} data.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Boolean} data.multiSensor 是否多传感器
     * @apiSuccess (响应结果) {Boolean} data.apiDatasource 是否API数据源
     * @apiSuccess (响应结果) {Int} data.createType 创建类型
     * @apiSuccess (响应结果) {Int} data.companyID 公司ID
     * @apiSuccess (响应结果) {String} data.exValues 拓展信息
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": [{"ID": 0,"monitorType": 0,"typeName": "","typeAlias": "","displayOrder": 0,"multiSensor": false,"apiDatasource": false,"createType": 0,"companyID": 0,"exValues": ""}]}
     * @apiPermission mdmbase:ListMonitorType
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor")
    @PostMapping(value = "/MonitorTypeCatalog", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object monitorTypeCatalog(@RequestBody @Validated MonitorTypeCatalogRequest request) {
        return sensorService.monitorTypeCatalog(request);
    }

    /**
     * @api {POST} /BaseConfig 基础配置
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName BaseConfig
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {String} templateID 监测类型模板ID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiParamExample 请求体示例
     * {"monitorType":4646,"templateID":5116,"projectID":2730}
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {String} data.exValues 拓展信息
     * @apiSuccess (响应结果) {Object[]} data.exFields 拓展字段
     * @apiSuccess (响应结果) {Int} data.exFields.id 拓展字段ID
     * @apiSuccess (响应结果) {Int} data.exFields.monitorType 监测类型
     * @apiSuccess (响应结果) {String} data.exFields.fieldToken 字段标识
     * @apiSuccess (响应结果) {String} data.exFields.fieldName 字段名称
     * @apiSuccess (响应结果) {String} data.exFields.fieldDataType 字段数据类型
     * @apiSuccess (响应结果) {Int} data.exFields.fieldClass 字段分类
     * @apiSuccess (响应结果) {String} data.exFields.fieldDesc 字段描述
     * @apiSuccess (响应结果) {Int} data.exFields.fieldUnitID 字段单位ID
     * @apiSuccess (响应结果) {Int} data.exFields.parentID 父级ID
     * @apiSuccess (响应结果) {Int} data.exFields.createType 创建类型
     * @apiSuccess (响应结果) {String} data.exFields.exValues 拓展信息
     * @apiSuccess (响应结果) {Int} data.exFields.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Object[]} data.paramFields 参数列表
     * @apiSuccess (响应结果) {Int} data.paramFields.id 参数字段ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectID 主体ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectType 主体类型
     * @apiSuccess (响应结果) {String} data.paramFields.dataType 数据类型
     * @apiSuccess (响应结果) {String} data.paramFields.token 字段标识
     * @apiSuccess (响应结果) {String} data.paramFields.name 字段名称
     * @apiSuccess (响应结果) {String} data.paramFields.paValue 字段值
     * @apiSuccess (响应结果) {Int} data.paramFields.paUnitID 字段单位ID
     * @apiSuccess (响应结果) {String} data.paramFields.paDesc 字段描述
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"exFields": [{"id": 0,"monitorType": 0,"fieldToken": "","fieldName": "","fieldDataType": "","fieldClass": 0,"fieldDesc": "","fieldUnitID": 0,"parentID": 0,"createType": 0,"exValues": "","displayOrder": 0}],"paramFields": [{"id": 0,"subjectID": 0,"subjectType": 0,"dataType": "","token": "","name": "","paValue": "","paUnitID": 0,"paDesc": ""}]}}
     * @apiPermission mdmbase:ListMonitorType
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor")
    @PostMapping(value = "/BaseConfig", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object baseConfig(@RequestBody @Validated BaseConfigRequest request) {
        return sensorService.baseConfig(request);
    }

    /**
     * @api {POST} /AddSensor 添加传感器
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName AddSensor
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [imagePath] 传感器图片base64 带前缀形如 data:image/png;base64,iVBORw0AAA
     * @apiParam (请求体) {String} alias 传感器别名
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} [templateID] 监测类型模板ID, 仅当dataSourceComposeType为100时不需要
     * @apiParam (请求体) {String} [dataSourceComposeType] 数据来源类型, 默认为1 <br/>      1单一物模型单一传感器 <br/>      2多个物联网传感器（同一物模型多个或者不同物模型多个）<br/>      3物联网传感器+监测传感器<br/>      4单个监测传感器<br/>      5多个监测传感器<br/>      100API 推送
     * @apiParam (请求体) {Object[]} [dataSourceList] 数据源列表, 仅当dataSourceComposeType为100时不需要
     * @apiParam (请求体) {Int} dataSourceList.dataSourceType 数据源类型 1-物联网传感器 2-监测传感器
     * @apiParam (请求体) {String} dataSourceList.templateDataSourceToken 模板数据源标识
     * @apiParam (请求体) {String} dataSourceList.sensorName (监测/物联网)传感器名称
     * @apiParam (请求体) {String} dataSourceList.uniqueToken 设备传感器标识 数据源类型为1时必填
     * @apiParam (请求体) {String} dataSourceList.exValues 数据源拓展信息
     * @apiParam (请求体) {Object[]} [exFields] 扩展配置列表，由监测类型决定是否需要
     * @apiParam (请求体) {Int} exFields.id 扩展配置参数ID
     * @apiParam (请求体) {String} exFields.value 扩展配置参数值
     * @apiParam (请求体) {Object[]} [paramFields] 参数列表，由监测类型决定是否需要
     * @apiParam (请求体) {Int} paramFields.id 参数ID
     * @apiParam (请求体) {String} paramFields.value 参数值
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.id 传感器ID
     * @apiPermission mdmbase:UpdateSensor
     * @apiParamExample {json} 请求体示例
     * {"projectID": 0,"imagePath": "","alias": "","monitorType": 20010,"dataSourceComposeType": 0,"templateDataSourceID": "dae6e561-4d01-4c22-9306-1a7b3d6171aa","templateID": 38,"dataSourceList": [{"dataSourceType": 1,"templateDataSourceToken": "103","sensorName": "test2023","uniqueToken": "D8DB467AB743476CA4B6F6F909704FBB"}],"exFields": [{"id": "180","value": "321"}],"paramFields": [{"id": "7","value": "666"}]}
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"id": 10086}}
     */
    @LogParam(moduleName = "传感器模块", operationName = "新增传感器", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:UpdateBaseSensor")
    @PostMapping(value = "/AddSensor", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object addSensor(@RequestBody @Validated SaveSensorRequest request) {
        return sensorService.addSensor(request);
    }

    /**
     * @api {POST} /SensorInfo 传感器详情
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName SensorInfo
     * @apiParam (请求体) {Int} sensorID 传感器ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.id 传感器ID
     * @apiSuccess (响应结果) {Int} data.projectID 项目ID
     * @apiSuccess (响应结果) {Int} data.templateID 模板ID
     * @apiSuccess (响应结果) {Object[]} data.dataSourceList 数据源列表
     * @apiSuccess (响应结果) {Int} data.dataSourceList.id 数据源id
     * @apiSuccess (响应结果) {String} data.dataSourceList.dataSourceID 数据源分布式唯一ID
     * @apiSuccess (响应结果) {Int} data.dataSourceList.dataSourceType 数据源类型 1-物联网传感器 2-监测传感器
     * @apiSuccess (响应结果) {String} data.dataSourceList.templateDataSourceToken 模板数据源标识
     * @apiSuccess (响应结果) {String} data.dataSourceList.dataSourceToken 数据源标识
     * @apiSuccess (响应结果) {String} data.dataSourceList.dataSourceComposeType 数据源组合类型
     * @apiSuccess (响应结果) {String} data.dataSourceList.exValues 扩展字段
     * @apiSuccess (响应结果) {String} data.dataSourceID 数据源ID
     * @apiSuccess (响应结果) {Int} data.dataSourceComposeType 数据源组合类型
     * @apiSuccess (响应结果) {Int} data.monitorType 监测类型
     * @apiSuccess (响应结果) {String} data.monitorTypeName 监测类型名称
     * @apiSuccess (响应结果) {String} data.name 传感器名称
     * @apiSuccess (响应结果) {String} data.alias 传感器别名
     * @apiSuccess (响应结果) {Int} data.kind 传感器类型
     * @apiSuccess (响应结果) {Int} data.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Int} data.monitorPointID 监测点ID
     * @apiSuccess (响应结果) {String} data.configFieldValue 配置字段值
     * @apiSuccess (响应结果) {String} data.exValues 拓展信息
     * @apiSuccess (响应结果) {Int} data.status 状态
     * @apiSuccess (响应结果) {Boolean} data.warnNoData 是否无数据报警
     * @apiSuccess (响应结果) {DateTime} data.monitorBeginTime 监测开始时间
     * @apiSuccess (响应结果) {String} data.imagePath 传感器图片路径
     * @apiSuccess (响应结果) {Int} [data.videoDeviceID] 视频设备ID
     * @apiSuccess (响应结果) {Byte} [data.accessPlatform] 视频设备平台类型
     * @apiSuccess (响应结果) {String} [data.accessPlatformStr] 视频设备平台类型名称
     * @apiSuccess (响应结果) {String} [data.deviceSerial] 视频设备序列号
     * @apiSuccess (响应结果) {Int} [data.channelCode] 通道号
     * @apiSuccess (响应结果) {String} [data.videoDeviceSource] 视频设备来源
     * @apiSuccess (响应结果) {Boolean} [enable] 是否启用, 不启用将不会接收数据
     * @apiSuccess (响应结果) {Object[]} data.exFields 拓展字段
     * @apiSuccess (响应结果) {Int} data.exFields.id 拓展字段ID
     * @apiSuccess (响应结果) {Int} data.exFields.monitorType 监测类型
     * @apiSuccess (响应结果) {String} data.exFields.fieldToken 字段标识
     * @apiSuccess (响应结果) {String} data.exFields.fieldName 字段名称
     * @apiSuccess (响应结果) {String} data.exFields.fieldDataType 字段数据类型
     * @apiSuccess (响应结果) {Int} data.exFields.fieldClass 字段分类
     * @apiSuccess (响应结果) {String} data.exFields.fieldDesc 字段描述
     * @apiSuccess (响应结果) {Int} data.exFields.fieldUnitID 字段单位ID
     * @apiSuccess (响应结果) {Int} data.exFields.parentID 父级ID
     * @apiSuccess (响应结果) {Int} data.exFields.createType 创建类型
     * @apiSuccess (响应结果) {String} data.exFields.exValues 拓展信息
     * @apiSuccess (响应结果) {Int} data.exFields.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Int} data.exFields.value 字段值
     * @apiSuccess (响应结果) {Object[]} data.paramFields 参数列表
     * @apiSuccess (响应结果) {Int} data.paramFields.id 参数字段ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectID 主体ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectType 主体类型
     * @apiSuccess (响应结果) {String} data.paramFields.dataType 数据类型
     * @apiSuccess (响应结果) {String} data.paramFields.token 字段标识
     * @apiSuccess (响应结果) {String} data.paramFields.name 字段名称
     * @apiSuccess (响应结果) {String} data.paramFields.paValue 字段值
     * @apiSuccess (响应结果) {Int} data.paramFields.paUnitID 字段单位ID
     * @apiSuccess (响应结果) {String} data.paramFields.paDesc 字段描述
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"exFields": [{"value": "","id": 0,"monitorType": 0,"fieldToken": "","fieldName": "","fieldDataType": "","fieldClass": 0,"fieldDesc": "","fieldUnitID": 0,"parentID": 0,"createType": 0,"exValues": "","displayOrder": 0}],"paramFields": [{"value": "","id": 0,"subjectID": 0,"subjectType": 0,"dataType": "","token": "","name": "","paValue": "","paUnitID": 0,"paDesc": ""}],"id": 0,"projectID": 0,"templateID": 0,"dataSourceID": "","dataSourceComposeType": 0,"monitorType": 0,"name": "","alias": "","kind": 0,"displayOrder": 0,"monitorPointID": 0,"configFieldValue": "","exValues": "","status": 0,"warnNoData": false,"monitorBeginTime": "2023-04-03 11:41:06","imagePath": "","createTime": "2023-04-03 11:41:06","createUserID": 0,"updateTime": "2023-04-03 11:41:06","updateUserID": 0}}
     * @apiPermission mdmbase:DescribeSensor
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor")
    @PostMapping(value = "/SensorInfo", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sensorInfo(@RequestBody @Validated SensorInfoRequest request) {
        return sensorService.sensorInfo(request);
    }

    /**
     * @api {POST} /DeleteSensor 删除传感器
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName DeleteSensor
     * @apiParam (请求体) {Object[]} sensorIDList 传感器ID列表
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiSuccess (响应结果) none
     * @apiPermission mdmbase:DescribeSensor
     */
    @LogParam(moduleName = "传感器管理", operationName = "删除传感器", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteBaseSensor")
    @PostMapping(value = "/DeleteSensor", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object deleteSensor(@RequestBody @Validated DeleteSensorRequest request) {
        sensorService.deleteSensor(request);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateSensor 更新传感器
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName UpdateSensor
     * @apiParam (请求体) {Int} sensorID 传感器ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [imagePath] 传感器图片base64 带前缀形如 data:image/png;base64,iVBORw0AAA
     * @apiParam (请求体) {String} [alias] 传感器别名
     * @apiParam (请求体) {Boolean} [enable] 是否启用, 不启用将不会接收数据
     * @apiParam (请求体) {Object[]} [exFields] 扩展配置列表 (不传=不修改，修改时每次传全部)
     * @apiParam (请求体) {Int} exFields.id 扩展配置参数ID
     * @apiParam (请求体) {String} exFields.value 扩展配置参数值
     * @apiParam (请求体) {Object[]} [paramFields] 参数列表 (不传=不修改，修改时每次传全部)
     * @apiParam (请求体) {Int} paramFields.id 参数ID
     * @apiParam (请求体) {String} paramFields.value 参数值
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.id 传感器ID
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"id": 10086}}
     * @apiPermission mdmbase:UpdateSensor
     */
    @LogParam(moduleName = "传感器管理", operationName = "更新传感器", operationProperty = OperationProperty.UPDATE)
    @Permission(permissionName = "mdmbase:UpdateBaseSensor")
    @PostMapping(value = "/UpdateSensor", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object updateSensor(@RequestBody @Validated UpdateSensorRequest request) {
        return sensorService.updateSensor(request);
    }

    /**
     * @api {POST} /QueryTryingParam 获取试运行参数
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName QueryTryingParam
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} templateID 监测类型模板ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.calType 计算类型 1公式计算 2脚本计算 3外部HTTP计算 -1不计算
     * @apiSuccess (响应结果) {Object[]} data.fieldList 监测类型字段列表
     * @apiSuccess (响应结果) {Int} data.fieldList.id 字段ID
     * @apiSuccess (响应结果) {String} [data.fieldList.formula] 计算公式 仅当calType=1时存在
     * @apiSuccess (响应结果) {String} [data.fieldList.displayFormula] 显示公式 仅当calType=1时存在
     * @apiSuccess (响应结果) {String} data.fieldList.monitorType 监测类型
     * @apiSuccess (响应结果) {String} data.fieldList.fieldToken 字段标识
     * @apiSuccess (响应结果) {String} data.fieldList.fieldName 字段名称
     * @apiSuccess (响应结果) {String} data.fieldList.fieldDataType 字段数据类型
     * @apiSuccess (响应结果) {String} data.fieldList.fieldClass 字段分类
     * @apiSuccess (响应结果) {String} data.fieldList.fieldDesc 字段描述
     * @apiSuccess (响应结果) {String} data.fieldList.fieldUnitID 字段单位ID
     * @apiSuccess (响应结果) {String} data.fieldList.parentID 父级ID
     * @apiSuccess (响应结果) {String} data.fieldList.createType 创建类型
     * @apiSuccess (响应结果) {String} data.fieldList.exValues 扩展配置
     * @apiSuccess (响应结果) {String} data.fieldList.display 是否显示
     * @apiSuccess (响应结果) {String} [data.script] 脚本 仅当calType=2时存在
     * @apiSuccess (响应结果) {Object[]} data.paramList 参数列表
     * @apiSuccess (响应结果) {String} data.paramList.name 参数名称
     * @apiSuccess (响应结果) {String} data.paramList.unit 参数单位
     * @apiSuccess (响应结果) {String} data.paramList.origin 源参数
     * @apiSuccess (响应结果) {String} data.paramList.token 参数标识
     * @apiSuccess (响应结果) {String} data.paramList.value 参数值 (部分参数拥有默认值)
     * @apiSuccess (响应结果) {String} data.paramList.exValues 参数扩展字段
     * @apiSuccess (响应结果) {String} data.paramList.dataType 参数数据类型
     * @apiSuccess (响应结果) {String} data.paramList.type 参数类型 IOT-物模型数据源类型、MON-监测传感器数据类型、SLEF-自身数据、HISTORY-自身历史数据、CONS-常量、PARAM-参数、EX-扩展配置
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"calType": 0,"fieldList": [{"value": "","formula": "${iot:201_a.Temp} - ${param:pvalue}","id": 0,"monitorType": 0,"fieldToken": "","fieldName": "","fieldDataType": "","fieldClass": 0,"fieldDesc": "","fieldUnitID": 0,"parentID": 0,"createType": 0,"exValues": "","displayOrder": 0}],"script": "","paramList": [{"name": "字段中文名","unit": "mm","origin": "${iot:201_a.Temp}","token": "Temp","type": "IOT"}]}}
     * @apiPermission mdmbase:DescribeSensor
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor")
    @PostMapping(value = "/QueryTryingParam", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getTryingParam(@RequestBody @Validated QueryTryingParamRequest request) {
        return sensorService.getTryingParam(request);
    }

    /**
     * @api {POST} /Trying 试运行
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName Trying
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} fieldID 监测类型字段ID
     * @apiParam (请求体) {Int} templateID 监测类型模板ID
     * @apiParam (请求体) {Object[]} [paramList] 参数列表
     * @apiParam (请求体) {String} paramList.value 参数值
     * @apiParam (请求体) {String} paramList.origin 源参数
     * @apiSuccess (响应结果) {Object[]} data
     * @apiSuccess (响应结果) {Double} data.value
     * @apiSuccess (响应结果) {String} data.fieldToken
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": [{"value": "11.5","fieldToken": "Q"}]}
     * @apiPermission mdmbase:DescribeSensor
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor")
    @PostMapping(value = "/Trying", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object trying(@RequestBody @Validated TryingRequest request) {
        return sensorService.trying(request);
    }

    /**
     * @api {POST} /QuerySensorDataSource  查询传感器数据源
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName QuerySensorDataSource
     * @apiDescription 查询传感器数据源 (!仅限应用调用)
     * @apiParam (请求参数) {Int} projectID  项目ID
     * @apiParam (请求参数) {String} dataSourceToken 数据源标识
     * @apiParam (请求参数) {String} [dataSourceComposeType] 模板数据来源类型
     * @apiSuccess (返回结果) {Object[]} dataSourceList  传感器数据源列表
     * @apiSuccess (返回结果) {Int} dataSourceList.ID  传感器数据源ID
     * @apiSuccess (返回结果) {String} dataSourceList.dataSourceID 数据源分布式唯一ID
     * @apiSuccess (返回结果) {Int} dataSourceList.dataSourceType  数据源类型 1 - 物联网传感器 2 - 监测传感器
     * @apiSuccess (返回结果) {String} dataSourceList.dataSourceToken  数据源标识
     * @apiSuccess (返回结果) {Int} dataSourceList.dataSourceComposeType  模板数据源标识
     * @apiSuccess (返回结果) {String} [dataSourceList.exValues]  拓展字段
     * @apiSuccess (返回结果) {Object[]} dataSourceList.sensorInfoList  传感器列表
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.ID  传感器ID
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.projectID  项目ID
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.templateID 模板ID
     * @apiSuccess (返回结果) {String} dataSourceList.sensorInfoList.dataSourceID 数据源分布式唯一ID
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.dataSourceComposeType 模板数据源标识
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.monitorType 监测类型
     * @apiSuccess (返回结果) {String} dataSourceList.sensorInfoList.name 传感器名称
     * @apiSuccess (返回结果) {String} dataSourceList.sensorInfoList.alias 传感器别名
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.kind 传感器类型　1-自动化传感器 2-融合传感器 3-人工传感器
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.displayOrder 排序字段
     * @apiSuccess (返回结果) {Int} [dataSourceList.sensorInfoList.monitorPointID] 关联监测点ID
     * @apiSuccess (返回结果) {String} [dataSourceList.sensorInfoList.configFieldValue] 存储监测类型拓展配置值
     * @apiSuccess (返回结果) {String} [dataSourceList.sensorInfoList.exValues] 拓展字段
     * @apiSuccess (返回结果) {Int} dataSourceList.sensorInfoList.status 传感器状态
     * @apiSuccess (返回结果) {Bool} dataSourceList.sensorInfoList.warnNoData 无数据报警
     * @apiSuccess (返回结果) {DateTime} [dataSourceList.sensorInfoList.monitorBeginTime] 开始监测时间
     * @apiSampleRequest off
     * @apiPermission 项目权限+应用权限
     */
    @Permission(permissionName = "mdmbase:DescribeBaseSensor", allowApplication = true, allowUser = false)
    @PostMapping(value = "/QuerySensorDataSource", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object querySensorDataSource(@RequestBody @Validated SourceWithSensorRequest request) {
        return sensorService.querySensorDataSource(request);
    }

    /**
     * @api {POST} /UpdateSensorStatusAndMonitorBeginTime  更新传感器状态与开始监测时间
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName UpdateSensorStatusAndMonitorBeginTime
     * @apiDescription 更新传感器状态与开始监测时间 (!仅限应用调用)
     * @apiParam (请求参数) {Int} projectID  项目ID
     * @apiParam (请求参数) {Int} sensorID 传感器ID
     * @apiParam (请求参数) {Int} [sensorStatus] 传感器状态
     * @apiParam (请求参数) {DateTime} [monitorBeginTime] 开始监测时间
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限+应用权限
     */
    @Permission(permissionName = "mdmbase:UpdateBaseSensor", allowApplication = true, allowUser = false)
    @PostMapping(value = "/UpdateSensorStatusAndMonitorBeginTime", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateSensorStatusAndMonitorBeginTime(@RequestBody @Validated UpdateSensorStatusRequest request) {
        sensorService.updateSensorStatusAndMonitorBeginTime(request);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {GET} /QueryAllSensorID  查询全部的传感器ID
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName QueryAllSensorID
     * @apiDescription 查询全部的传感器ID
     * @apiSuccess (返回结果) {Object} map key为Type, value为传感器列表
     * @apiSampleRequest off
     * @apiPermission 项目权限+应用权限mdmbase:ManagerMDMBaseSensorData
     */
    @Permission(permissionName = "mdmbase:ManagerMDMBaseSensorData", allowApplication = true)
    @RequestMapping(value = "/QueryAllSensorID", method = RequestMethod.GET, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateSensorStatusAndMonitorBeginTime() {
        return sensorService.queryAllSensorID();
    }

    /**
     * @api {POST} /QueryManualSensorListByMonitor 根据监测类型查询工程下所有人工传感器
     * @apiDescription 根据监测类型查询工程下所有人工传感器
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName QueryManualSensorListByMonitor
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} dataList.sensorAlias 传感器名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    @Permission(permissionName = "mdmbase:ListBaseSensor")
    @PostMapping(value = "/QueryManualSensorListByMonitor", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryManualSensorListByMonitor(@Valid @RequestBody QueryManualSensorListByMonitorParam param) {
        return sensorService.queryManualSensorListByMonitor(param);
    }
}
