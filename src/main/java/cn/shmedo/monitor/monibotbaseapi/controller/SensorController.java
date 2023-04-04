package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorPageResponse;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-21 16:02
 **/
@RestController
@AllArgsConstructor
public class SensorController {

    /**
     * @api {POST} /SensorPage 传感器分页
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName SensorPage
     * @apiParam (请求体) {Int} pageSize 分页大小 (1-100)
     * @apiParam (请求体) {Int} currentPage 当前页码 (大于0)
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [sensorName] 传感器名称 模糊查询
     * @apiParam (请求体) {String} [monitorType] 监测类型名称 模糊查询
     * @apiParam (请求体) {String} [monitorPoint] 关联监测点名称
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.totalCount 总条数
     * @apiSuccess (响应结果) {Int} data.totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} data.currentPageData 当前页数据
     * @apiSuccess (响应结果) {Int} data.currentPageData.ID 传感器ID
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
     * {"code": 0,"msg": null,"data": {"totalPage": 1,"totalCount": 1,"currentPageData": [{"ID": 0,"monitorType": 0,"monitorTypeName": "","name": "","alias": "","displayOrder": 0,"monitorPointID": 0,"monitorPointName": 0,"exValues": "","enable": false}]}}
     * @apiPermission mdmbase:ListSensor
     */
    @LogParam(moduleName = "传感器模块", operationName = "查询传感器", operationProperty = OperationProperty.QUERY)
//    @Permission(permissionName = "mdmbase:ListSensor")
    @PostMapping(value = "/SensorPage", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public PageUtil.Page<SensorPageResponse> sensorPage(@RequestBody @Validated SensorPageRequest request) {
        //传感器分页 传感器名称、监测类型、关联监测点过滤

        return null;
    }

    /**
     * @api {POST} /DataSourceCascade 数据源级联查询
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName DataSourceCascade
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} [monitorType] 监测类型
     * @apiParam (请求参数) {String} [keyword] 检索关键字
     * @apiParam (请求参数) {Object} [previous] 上级选中值, 仅当 level=1 时可为空
     * @apiParam (请求参数) {Int} [level] 层级，默认为 1，最大为 3
     * @apiSuccess (响应结果) {Object[]} data
     * @apiSuccess (响应结果) {String} data.key 选项名称
     * @apiSuccess (响应结果) {Int} data.value 选项值
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": [{"key": "物联网数据","value": 1},{"key": "监测数据源","value": 2},{"key": "API外部数据","value": -1}]}
     * @apiPermission mdmbase:ListDataSource
     */
//    @Permission(permissionName = "mdmbase:ListDataSource")
    @PostMapping(value = "/DataSourceCascade", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object dataSourceCascade(DataSourceCascadeRequest request) {
        //数据源下拉搜索, 三级级联
        return null;
    }

    /**
     * @api {POST} /MonitorTypeCatalog 监测类型(下拉)目录查询
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName MonitorTypeCatalog
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Object[]} [dataSourceList] 数据源列表
     * @apiParam (请求参数) {Int} dataSourceList.value 数据源标识（级联最后一级value）
     * @apiParam (请求参数) {String} dataSourceList.type 数据源类型（级联第一级value） 1-物联网传感器 2-监测传感器 -1-API
     * @apiSuccess (响应结果) {Object} data
     * @apiSuccess (响应结果) {Int} data.ID 监测类型ID
     * @apiSuccess (响应结果) {Int} data.monitorType 监测类型
     * @apiSuccess (响应结果) {String} data.typeName 监测类型名称
     * @apiSuccess (响应结果) {String} data.typeAlias 监测类型别名
     * @apiSuccess (响应结果) {Int} data.displayOrder 显示排序字段
     * @apiSuccess (响应结果) {Boolean} data.multiSensor 是否多传感器
     * @apiSuccess (响应结果) {Boolean} data.apiDatasource 是否API数据源
     * @apiSuccess (响应结果) {Int} data.createType 创建类型
     * @apiSuccess (响应结果) {Int} data.companyID 公司ID
     * @apiSuccess (响应结果) {String} data.exValues 拓展信息
     * @apiSuccess (响应结果) {Object[]} data.exFields 拓展字段
     * @apiSuccess (响应结果) {Int} data.exFields.ID 拓展字段ID
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
     * @apiSuccess (响应结果) {Int} data.paramFields.ID 参数字段ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectID 主体ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectType 主体类型
     * @apiSuccess (响应结果) {String} data.paramFields.dataType 数据类型
     * @apiSuccess (响应结果) {String} data.paramFields.token 字段标识
     * @apiSuccess (响应结果) {String} data.paramFields.name 字段名称
     * @apiSuccess (响应结果) {String} data.paramFields.paValue 字段值
     * @apiSuccess (响应结果) {Int} data.paramFields.paUnitID 字段单位ID
     * @apiSuccess (响应结果) {String} data.paramFields.paDesc 字段描述
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": [{"ID": 0,"monitorType": 0,"typeName": "","typeAlias": "","displayOrder": 0,"multiSensor": false,"apiDatasource": false,"createType": 0,"companyID": 0,"exValues": "","exFields": [{"ID": 0,"monitorType": 0,"fieldToken": "","fieldName": "","fieldDataType": "","fieldClass": 0,"fieldDesc": "","fieldUnitID": 0,"parentID": 0,"createType": 0,"exValues": "","displayOrder": 0}],"paramFields": [{"ID": 0,"subjectID": 0,"subjectType": 0,"dataType": "","token": "","name": "","paValue": "","paUnitID": 0,"paDesc": ""}]}]}
     * @apiPermission mdmbase:ListMonitorType
     */
//    @Permission(permissionName = "mdmbase:ListMonitorType")
    @PostMapping(value = "/MonitorTypeCatalog", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object monitorTypeCatalog(MonitorTypeCatalogRequest request) {
        //MonitorTypeCatalogResponse
        //监测类型下拉搜索
        return null;
    }

    /**
     * @api {POST} /AddSensor 添加传感器
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName AddSensor
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [imagePath] 传感器图片路径
     * @apiParam (请求体) {String} alias 传感器别名
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {String} [dataSourceComposeType] 数据来源类型, 默认为1 <br/>      1单一物模型单一传感器 <br/>      2多个物联网传感器（同一物模型多个或者不同物模型多个）<br/>      3物联网传感器+监测传感器<br/>      4单个监测传感器<br/>      5多个监测传感器<br/>      100API 推送
     * @apiParam (请求体) {Object[]} dataSourceList 数据源列表, 仅当dataSourceComposeType为100时不需要
     * @apiParam (请求体) {Int} dataSourceList.value 数据源标识（级联最后一级value）
     * @apiParam (请求体) {String} dataSourceList.type 数据源类型（级联第一级value） 1-物联网传感器 2-监测传感器 -1-API
     * @apiParam (请求体) {Object[]} [exFields] 扩展配置列表，由监测类型决定是否需要
     * @apiParam (请求体) {Int} exFields.ID 扩展配置参数ID
     * @apiParam (请求体) {String} exFields.value 扩展配置参数值
     * @apiParam (请求体) {Object[]} [paramFields] 参数列表，由监测类型决定是否需要
     * @apiParam (请求体) {Int} paramFields.ID 参数ID
     * @apiParam (请求体) {String} paramFields.value 参数值
     * @apiParamExample {json} 请求体示例
     * {"projectID": 0,"imagePath": "","name": "","alias": "","monitorType": 0,"dataSourceComposeType": 1,"dataSourceList": [{"type": 1,"value": 0}],"exFields": [{"ID": "","value": ""}],"paramFields": [{"ID": "","value": ""}]}
     */
    @LogParam(moduleName = "传感器模块", operationName = "新增传感器", operationProperty = OperationProperty.ADD)
//    @Permission(permissionName = "mdmbase:UpdateSensor")
    @PostMapping(value = "/AddSensor", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Integer addSensor(@RequestBody @Validated SaveSensorRequest request) {
        //新增传感器
        return null;
    }

    /**
     * @api {POST} /SensorInfo 传感器详情
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName SensorInfo
     * @apiParam (请求体) {Int} sensorID 传感器ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.ID 传感器ID
     * @apiSuccess (响应结果) {Int} data.projectID 项目ID
     * @apiSuccess (响应结果) {Int} data.templateID 模板ID
     * @apiSuccess (响应结果) {String[]} data.dataSourceNames 数据源名称（如: 物联网数据>SN12345>传1）
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
     * @apiSuccess (响应结果) {Boolean} [enable] 是否启用, 不启用将不会接收数据
     * @apiSuccess (响应结果) {Object[]} data.exFields 拓展字段
     * @apiSuccess (响应结果) {Int} data.exFields.ID 拓展字段ID
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
     * @apiSuccess (响应结果) {Int} data.paramFields.ID 参数字段ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectID 主体ID
     * @apiSuccess (响应结果) {Int} data.paramFields.subjectType 主体类型
     * @apiSuccess (响应结果) {String} data.paramFields.dataType 数据类型
     * @apiSuccess (响应结果) {String} data.paramFields.token 字段标识
     * @apiSuccess (响应结果) {String} data.paramFields.name 字段名称
     * @apiSuccess (响应结果) {String} data.paramFields.paValue 字段值
     * @apiSuccess (响应结果) {Int} data.paramFields.paUnitID 字段单位ID
     * @apiSuccess (响应结果) {String} data.paramFields.paDesc 字段描述
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"exFields": [{"value": "","ID": 0,"monitorType": 0,"fieldToken": "","fieldName": "","fieldDataType": "","fieldClass": 0,"fieldDesc": "","fieldUnitID": 0,"parentID": 0,"createType": 0,"exValues": "","displayOrder": 0}],"paramFields": [{"value": "","ID": 0,"subjectID": 0,"subjectType": 0,"dataType": "","token": "","name": "","paValue": "","paUnitID": 0,"paDesc": ""}],"ID": 0,"projectID": 0,"templateID": 0,"dataSourceID": "","dataSourceComposeType": 0,"monitorType": 0,"name": "","alias": "","kind": 0,"displayOrder": 0,"monitorPointID": 0,"configFieldValue": "","exValues": "","status": 0,"warnNoData": false,"monitorBeginTime": "2023-04-03 11:41:06","imagePath": "","createTime": "2023-04-03 11:41:06","createUserID": 0,"updateTime": "2023-04-03 11:41:06","updateUserID": 0}}
     * @apiPermission mdmbase:DescribeSensor
     */
//    @Permission(permissionName = "mdmbase:DescribeSensor")
    @PostMapping(value = "/SensorInfo", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object sensorInfo(@RequestBody @Validated SensorInfoRequest request) {
        //传感器详情
        return null;
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
//    @Permission(permissionName = "mdmbase:DeleteSensor")
    @PostMapping(value = "/DeleteSensor", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object deleteSensor(@RequestBody @Validated DeleteSensorRequest request) {
        //删除传感器
        return null;
    }

    /**
     * @api {POST} /UpdateSensor 更新传感器
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName UpdateSensor
     * @apiParam (请求体) {Int} sensorID 传感器ID
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {String} [imagePath] 传感器图片路径
     * @apiParam (请求体) {String} [alias] 传感器别名
     * @apiParam (请求体) {Boolean} [enable] 是否启用, 不启用将不会接收数据
     * @apiParam (请求体) {Object[]} [exFields] 扩展配置列表 (不传=不修改，修改时每次传全部)
     * @apiParam (请求体) {Int} exFields.ID 扩展配置参数ID
     * @apiParam (请求体) {String} exFields.value 扩展配置参数值
     * @apiParam (请求体) {Object[]} [paramFields] 参数列表 (不传=不修改，修改时每次传全部)
     * @apiParam (请求体) {Int} paramFields.ID 参数ID
     * @apiParam (请求体) {String} paramFields.value 参数值
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.ID 传感器ID
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"ID": 10086}}
     * @apiPermission mdmbase:UpdateSensor
     */
    @LogParam(moduleName = "传感器管理", operationName = "更新传感器", operationProperty = OperationProperty.UPDATE)
//    @Permission(permissionName = "mdmbase:UpdateSensor")
    @PostMapping(value = "/UpdateSensor", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object updateSensor(@RequestBody @Validated UpdateSensorRequest request) {
        //传感器下拉搜索
        return null;
    }

    /**
     * @api {POST} /QueryTryingParam 获取试运行参数
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName QueryTryingParam
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiSuccess (响应结果) {Object} data 响应结果
     * @apiSuccess (响应结果) {Int} data.calType 计算类型 1公式计算 2脚本计算 3外部HTTP计算 -1不计算
     * @apiSuccess (响应结果) {Object[]} data.fieldList 监测类型字段列表
     * @apiSuccess (响应结果) {Int} data.fieldList.ID 字段ID
     * @apiSuccess (响应结果) {String} [data.fieldList.formula] 公式 仅当calType=1时存在
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
     * @apiSuccess (响应结果) {String} data.paramList.token 物模型字段标识
     * @apiSuccess (响应结果) {String} data.paramList.type 参数类型 IOT-物模型数据源类型、MON-监测传感器数据类型、SLEF-自身数据、HISTORY-自身历史数据、CONS-常量、PARAM-参数、EX-扩展配置
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": {"calType": 0,"fieldList": [{"value": "","formula": "${iot:201_a.Temp} - ${param:pvalue}","ID": 0,"monitorType": 0,"fieldToken": "","fieldName": "","fieldDataType": "","fieldClass": 0,"fieldDesc": "","fieldUnitID": 0,"parentID": 0,"createType": 0,"exValues": "","displayOrder": 0}],"script": "","paramList": [{"name": "字段中文名","unit": "mm","origin": "${iot:201_a.Temp}","token": "Temp","type": "IOT"}]}}
     */
//    @Permission(permissionName = "mdmbase:DescribeSensor")
    @PostMapping(value = "/QueryTryingParam", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object getTryingParam(@RequestBody @Validated QueryTryingParamRequest request) {
        //获取试运行参数
        return null;
    }

    /**
     * @api {POST} /Trying 试运行
     * @apiVersion 1.0.0
     * @apiGroup 传感器模块
     * @apiName Trying
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {String} fieldToken 字段标识
     * @apiParam (请求体) {Int} calType 计算类型
     * @apiParam (请求体) {Object[]} paramList 参数列表
     * @apiParam (请求体) {String} paramList.value 参数值
     * @apiParam (请求体) {String} paramList.origin 源参数
     * @apiSuccess (响应结果) {Object[]} data
     * @apiSuccess (响应结果) {Double} data.value
     * @apiSuccess (响应结果) {String} data.fieldToken
     * @apiSuccessExample {json} 响应结果示例
     * {"code": 0,"msg": null,"data": [{"value": "11.5","fieldToken": "Q"}]}
     */
//    @Permission(permissionName = "mdmbase:DescribeSensor")
    @PostMapping(value = "/Trying", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Object trying(@RequestBody @Validated TryingRequest request) {
        return null;
    }
}
