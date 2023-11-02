package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.PermissionScope;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointListParam;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WtMonitorGroupDataController {

    private WtMonitorService wtMonitorService;

    /**
     * @api {POST} /QueryMonitorPointDataList 查询监测点数据列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName QueryMonitorPointDataList
     * @apiDescription 查询监测点数据列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Int} [densityType] 密度,(0:全部 1:小时 2:日 3:周 4:月 5:年)
     * @apiParam (请求体) {Int} [statisticsType] 统计方式,(0:最新一条 1:平均值 2:阶段累积 3:阶段变化)
     * @apiParam (请求体) {Int[]} [eigenvalueIDList] 特征值ID列表
     * @apiParam (请求体) {Int[]} [eventIDList] 大事记ID列表
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"monitorType":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31"}
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
     * @apiSuccess (响应结果) {Object[]} data.sensorDataList       传感器最新数据，流量流速数据示例:{"sid":1,"time":"2023-03-01 00:00:00","flow":100.2,"speed":40.5}
     * @apiSuccess (响应结果) {Int} data.sensorDataList.sensorID         传感器ID
     * @apiSuccess (响应结果) {DateTime} data.sensorDataList.time       数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorDataList.data              传感器数据(动态值)，参考监测项目属性字段列表
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
     * @apiSuccess (响应结果) {Object[]} [data.eigenvalueList] 特征值列表
     * @apiSuccess (响应结果) {Int} [data.eigenvalueList.id] 特征值列表
     * @apiSuccess (响应结果) {Int} [data.eigenvalueList.projectID] 工程ID
     * @apiSuccess (响应结果) {Int} [data.eigenvalueList.scope] 使用范围,0:专题分析 1:历史数据
     * @apiSuccess (响应结果) {String} [data.eigenvalueList.scopeStr] 使用范围描述
     * @apiSuccess (响应结果) {Int} [data.eigenvalueList.monitorItemID] 监测项目ID
     * @apiSuccess (响应结果) {Int} [data.eigenvalueList.monitorTypeFieldID] 属性(监测子类型ID)
     * @apiSuccess (响应结果) {String} [data.eigenvalueList.monitorTypeFieldName] 属性名称
     * @apiSuccess (响应结果) {String} [data.eigenvalueList.name] 特征值
     * @apiSuccess (响应结果) {Double} [data.eigenvalueList.value] 数值
     * @apiSuccess (响应结果) {Int} [data.eigenvalueList.unitID] 单位ID
     * @apiSuccess (响应结果) {String} [data.eigenvalueList.engUnit] 单位英文描述
     * @apiSuccess (响应结果) {String} [data.eigenvalueList.chnUnit] 单位中文描述
     * @apiSuccess (响应结果) {Object[]} [data.eventList] 大事记列表
     * @apiSuccess (响应结果) {Int} [data.eventList.id] 大事记id
     * @apiSuccess (响应结果) {String} [data.eventList.eventName] 大事记名称
     * @apiSuccess (响应结果) {Int} [data.eventList.frequency] 频率
     * @apiSuccess (响应结果) {String} [data.eventList.frequencyStr] 频率描述
     * @apiSuccess (响应结果) {String} [data.eventList.timeRange] 时间范围
     * @apiSuccess (响应结果) {Object[]} data.sensorFilterData 传感器数据最大最小值过滤列表
     * @apiSuccess (响应结果) {Boolean} data.sensorFilterData.flag 标识,最大值:true 最小值:false
     * @apiSuccess (响应结果) {Int} data.sensorFilterData.sensorID 传感器ID
     * @apiSuccess (响应结果) {T} data.sensorFilterData.data 传感器数据(动态值)，参考监测项目属性字段列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorPointDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointDataList(@Validated @RequestBody QueryMonitorPointListParam pa) {
//        return wtMonitorService.queryMonitorPointList(pa);
        return null;
    }



    /**
     * @api {POST} /AddEigenValue 新增数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName AddEigenValue
     * @apiDescription 新增数据特征值
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} scope 作用范围,0:专题分析 1:历史数据
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {Int} monitorTypeFieldID 属性(监测子类型ID)
     * @apiParam (请求体) {String} name 特征值名称
     * @apiParam (请求体) {Double} value 数值
     * @apiParam (请求体) {Int} unitID 单位ID
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"scope":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "monitorTypeFieldID":"1","name":"123","value":"123.123","unitID":"1"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/AddEigenValue", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addEigenValue(@Validated @RequestBody Object pa) {
//        return wtMonitorService.queryMonitorPointList(pa);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /UpdateEigenValue 更新数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName UpdateEigenValue
     * @apiDescription 新增数据特征值
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} eigenValueID 特征ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} scope 作用范围,0:专题分析 1:历史数据
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiParam (请求体) {Int} monitorTypeFieldID 属性(监测子类型ID)
     * @apiParam (请求体) {String} name 特征值名称
     * @apiParam (请求体) {Double} value 数值
     * @apiParam (请求体) {Int} unitID 单位ID
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiParamExample 请求体示例
     * {"eigenValueID":1,"companyID":138,"projectID":1,"scope":"1","monitorItemID":1,"monitorPointIDList":[1,2],
     * "monitorTypeFieldID":"1","name":"123","value":"123.123","unitID":"1"}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/UpdateEigenValue", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateEigenValue(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /DeleteBatchEigenValue 删除数据特征值
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName DeleteBatchEigenValue
     * @apiDescription 删除数据特征值
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} eigenValueIDList 特征ID列表
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"eigenValueIDList":[1,2]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/DeleteBatchEigenValue", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteBatchEigenValue(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryEigenValueList 查询数据特征值列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName QueryEigenValueList
     * @apiDescription 查询数据特征值列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParamExample 请求体示例
     * {"projectID":1}
     * @apiSuccess (响应结果) {Object[]} eigenvalueList 特征值列表
     * @apiSuccess (响应结果) {Int} eigenvalueList.id 特征值列表
     * @apiSuccess (响应结果) {Int} eigenvalueList.projectID 工程ID
     * @apiSuccess (响应结果) {Int} eigenvalueList.scope 使用范围,0:专题分析 1:历史数据
     * @apiSuccess (响应结果) {String} eigenvalueList.scopeStr 使用范围描述
     * @apiSuccess (响应结果) {Int} eigenvalueList.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {Int} eigenvalueList.monitorTypeFieldID 属性(监测子类型ID)
     * @apiSuccess (响应结果) {String} [eigenvalueList.monitorTypeFieldName] 属性名称
     * @apiSuccess (响应结果) {String} eigenvalueList.name 特征值
     * @apiSuccess (响应结果) {Double} eigenvalueList.value 数值
     * @apiSuccess (响应结果) {Int} eigenvalueList.unitID 单位ID
     * @apiSuccess (响应结果) {String} eigenvalueList.engUnit 单位英文描述
     * @apiSuccess (响应结果) {String} eigenvalueList.chnUnit 单位中文描述
     * @apiSuccess (响应结果) {String} [eigenvalueList.exValue] 备注
     * @apiSuccess (响应结果) {Int} eigenvalueList.createUserID 创建人ID
     * @apiSuccess (响应结果) {Int} eigenvalueList.updateUserID 修改人ID
     * @apiSuccess (响应结果) {Date} eigenvalueList.createTime 创建时间
     * @apiSuccess (响应结果) {Date} eigenvalueList.updateTime 修改时间
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryEigenValueList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryEigenValueList(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }



    /**
     * @api {POST} /AddDataEvent 新增大事件
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName AddDataEvent
     * @apiDescription 新增大事件
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {String} name 大事件名称
     * @apiParam (请求体) {Int} frequency 频率,0:单次  1:每年
     * @apiParam (请求体) {String} timeRange 开始-结束时间,json格式
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"name":"1","frequency":1,
     * "TimeRange": [
     *     {
     *       "startTime": "2023-10-18 16:02:52",
     *       "endTime": "2023-10-18 16:02:52"
     *     },
     *     {
     *       "startTime": "2023-10-18 16:02:52",
     *       "endTime": "2023-10-18 16:02:52"
     *     },
     *     {
     *       "startTime": "2023-10-18 16:02:52",
     *       "endTime": "2023-10-18 16:02:52"
     *     }
     *   ]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/AddDataEvent", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object addDataEvent(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /DeleteBatchDataEvent 删除大事件
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName DeleteBatchDataEvent
     * @apiDescription 删除大事件
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} eventIDList 大事件名称
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"eventIDList":[1]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/DeleteBatchDataEvent", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object deleteBatchDataEvent(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryDataEventList 查询大事件列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName QueryDataEventList
     * @apiDescription 查询大事件列表
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [monitorItemID] 项目ID
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"name":"1","monitorItemID":1}
     * @apiSuccess (返回结果) {Object[]} dataList 大事件ID
     * @apiSuccess (返回结果) {Int} dataList.id 大事件ID
     * @apiSuccess (返回结果) {Int} dataList.projectID 工程ID
     * @apiSuccess (返回结果) {String} dataList.name 大事件名称
     * @apiSuccess (返回结果) {Int} dataList.frequency 频率,0:单次  1:每年
     * @apiSuccess (返回结果) {String} dataList.timeRange 开始-结束时间,json格式
     * @apiSuccess (返回结果) {String} [dataList.exValue] 备注
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryDataEventList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryDataEventList(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /UpdateDataEvent 更新大事件
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName UpdateDataEvent
     * @apiDescription 更新大事件
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} id 大事记ID
     * @apiParam (请求体) {String} name 大事件名称
     * @apiParam (请求体) {Int} frequency 频率,0:单次  1:每年
     * @apiParam (请求体) {String} timeRange 开始-结束时间,json格式
     * @apiParam (请求体) {String} [exValue] 备注
     * @apiParamExample 请求体示例
     * {"companyID":138,"projectID":1,"id":1,"name":"1","frequency":1,
     * "TimeRange": [
     *     {
     *       "startTime": "2023-10-18 16:02:52",
     *       "endTime": "2023-10-18 16:02:52"
     *     },
     *     {
     *       "startTime": "2023-10-18 16:02:52",
     *       "endTime": "2023-10-18 16:02:52"
     *     },
     *     {
     *       "startTime": "2023-10-18 16:02:52",
     *       "endTime": "2023-10-18 16:02:52"
     *     }
     *   ]}
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/UpdateDataEvent", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object updateDataEvent(@Validated @RequestBody Object pa) {
        return ResultWrapper.successWithNothing();
    }



    /**
     * @api {POST} /QueryMonitorPointHasDataCount 查询监测点有无数据日期时间列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName QueryMonitorPointHasDataCount
     * @apiDescription 查询监测点有无数据日期时间列表,最低支持到日
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点列表,[1-100],必须为同一种监测类型
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end 结束时间
     * @apiParam (请求体) {Int} density 密度,(0:全部 2:日 3:周 4:月 5:年)
     * @apiParamExample 请求体示例
     * {"projectID":1,"monitorPointIDList":[1,2],"begin":"2023-10-18 16:02:52","end":"2023-10-18 16:02:52","density":1}
     * @apiSuccess (返回结果) {Object} data 结果
     * @apiSuccess (返回结果) {String[]} data.dataList 监测点有数据列表，日格式:yyyy-MM-dd,月格式:yyyy-MM,年格式:yyyy
     * @apiSuccess (返回结果) {Int} data.density 密度,(0:全部 2:日 3:周 4:月 5:年)
     * @apiSampleRequest off
     * @apiPermission 项目权限:mdmbase:DescribeBaseProject
     */
    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryMonitorPointHasDataCount", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorPointHasDataCount(@Valid @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /QueryMonitorTypeConfiguration 查询监测类型的预定义密度与统计方式
     * @apiVersion 1.0.0
     * @apiGroup 监测组别数据数据模块
     * @apiName QueryMonitorTypeConfiguration
     * @apiDescription 查询监测类型的预定义密度与统计方式
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParamExample 请求体示例
     * {"monitorType":1}
     * @apiSuccess (返回结果) {Int} monitorType 监测类型标识
     * @apiSuccess (返回结果) {String} typeName 监测类型名称
     * @apiSuccess (返回结果) {String} exValues 预定义密度与统计方式
     * @apiSampleRequest off
     * @apiPermission 登录权限
     */
    @Permission(permissionScope = PermissionScope.LOGGED)
    @RequestMapping(value = "/QueryMonitorTypeConfiguration", method = RequestMethod.POST,
            produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorTypeConfiguration(@Valid @RequestBody Object pa) {
        return null;
    }
}
