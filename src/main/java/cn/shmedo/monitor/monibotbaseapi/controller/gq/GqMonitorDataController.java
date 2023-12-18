package cn.shmedo.monitor.monibotbaseapi.controller.gq;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqMonitorPointDataPushParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqQueryMonitorPointDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.GqQueryMonitorPointStatisticsDataPageParam;
import cn.shmedo.monitor.monibotbaseapi.service.GqMonitorPointService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 灌区模块
 */
@RestController
@AllArgsConstructor
public class GqMonitorDataController {


    private GqMonitorPointService gqMonitorPointService;


    /**
     * @api {POST} /GqQueryMonitorPointDataList 查询灌区监测点单日数据列表
     * @apiVersion 1.0.0
     * @apiGroup 灌区监测数据模块
     * @apiName GqQueryMonitorPointDataList
     * @apiDescription 跨监测项目, 查询灌区监测点单日数据列表, 只统计单日的, 返回8, 12, 16, 20点数据每个时刻的最新数据
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectTypeID 工程类型,(水闸:11,渠系:10)
     * @apiParam (请求体) {Int} [kind] 数据来源,1 - 自动化传感器 3 - 人工传感器, null - 查询全部
     * @apiParam (请求体) {Int} token 量水类型,(水工建筑量水:3)
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParamExample 请求体示例
     * {"companyID":1,"kind":"1","token":"sss","monitorPointName":"测测","projectTypeID":10,
     * "begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31"}
     * @apiSuccess (响应结果) {Object} data                 结果
     * @apiSuccess (响应结果) {Object[]} data.sensorInfoList  传感器信息列表
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.companyID       公司ID
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} data.sensorInfoList.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.monitorType          监测类型
     * @apiSuccess (响应结果) {String} data.sensorInfoList.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} data.sensorInfoList.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.sensorID    传感器ID
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.projectID 工程ID
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.projectTypeID 工程类型
     * @apiSuccess (响应结果) {String} data.sensorInfoList.projectName 工程名称
     * @apiSuccess (响应结果) {String} data.sensorInfoList.projectTypeName 工程类型名称
     * @apiSuccess (响应结果) {String} data.sensorInfoList.waterMeasuringTypeName 量水类型名称
     * @apiSuccess (响应结果) {String} data.sensorInfoList.sensorName  传感器名称
     * @apiSuccess (响应结果) {String} data.sensorInfoList.sensorAlias  传感器别名
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.kind  传感器数据来源,1 - 自动化传感器 3 - 人工传感器
     * @apiSuccess (响应结果) {Object[]} data.sensorInfoList.sensorDataList   传感器数据
     * @apiSuccess (响应结果) {Int} data.sensorInfoList.sensorDataList.sensorID   传感器ID
     * @apiSuccess (响应结果) {DateTime} data.sensorInfoList.sensorDataList.time  数据采集时间
     * @apiSuccess (响应结果) {T} data.sensorInfoList.sensorData.data  传感器数据(动态值)，参考监测项目属性字段列表,如:土壤含水量(%)等
     * @apiSuccess (响应结果) {Object[]} data.fieldInfoList   监测类型属性字段列表
     * @apiSuccess (响应结果) {String} data.fieldInfoList.fieldToken  字段标志
     * @apiSuccess (响应结果) {String} data.fieldInfoList.fieldName   字段名称
     * @apiSuccess (响应结果) {String} data.fieldInfoList.engUnit 英文单位
     * @apiSuccess (响应结果) {String} data.fieldInfoList.chnUnit 中文单位
     * @apiSuccess (响应结果) {String} data.fieldInfoList.unitClass  单位类型
     * @apiSuccess (响应结果) {String} data.fieldInfoList.unitDesc  单位类型描述
     * @apiSuccess (响应结果) {Int} data.fieldInfoList.displayOrder  字段排序
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryMonitorPointDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryMonitorPointDataList(@Validated @RequestBody GqQueryMonitorPointDataParam pa) {
        return gqMonitorPointService.gqQueryMonitorPointDataList(pa);
    }


    /**
     * @api {POST} /GqQueryMonitorPointStatisticsDataPage 查询灌区监测点统计数据分页
     * @apiVersion 1.0.0
     * @apiGroup 灌区监测数据模块
     * @apiName GqQueryMonitorPointStatisticsDataPage
     * @apiDescription 跨监测项目, 查询灌区监测点统计数据分页, 目前只支持:水闸:11,渠系:10
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectTypeID 工程类型,(水闸:11,渠系:10)
     * @apiParam (请求体) {Int} [kind] 数据来源,1 - 自动化传感器 3 - 人工传感器
     * @apiParam (请求体) {Int} token 量水类型,(水工建筑量水:3)
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Boolean} [dataSort]  数据排序,为空默认倒序,true为正序,false为倒序
     * @apiParam (请求体) {Int} densityType 密度,(日:3 月:5 年:6)
     * @apiParam (请求体) {Int} statisticsType 统计方式,(平均:2)
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiParamExample 请求体示例
     * {"companyID":1,"kind":"1","token":"sss","monitorPointName":"测测","projectTypeID":10,"densityType":1
     * "statisticsType":1,"begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31","pageSize":10,"currentPage":1}
     * @apiSuccess (响应结果) {Int} totalCount 数据总量
     * @apiSuccess (响应结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} currentPageData 项目信息列表
     * @apiSuccess (响应结果) {Int} currentPageData.companyID       公司ID
     * @apiSuccess (响应结果) {Int} currentPageData.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} currentPageData.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} currentPageData.monitorType          监测类型
     * @apiSuccess (响应结果) {String} currentPageData.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} currentPageData.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Int} currentPageData.sensorID    传感器ID
     * @apiSuccess (响应结果) {Int} currentPageData.projectID 工程ID
     * @apiSuccess (响应结果) {Int} currentPageData.projectTypeID 工程类型
     * @apiSuccess (响应结果) {String} currentPageData.projectName 工程名称
     * @apiSuccess (响应结果) {String} currentPageData.projectTypeName 工程类型名称
     * @apiSuccess (响应结果) {String} currentPageData.waterMeasuringTypeName 量水类型名称
     * @apiSuccess (响应结果) {String} currentPageData.sensorName  传感器名称
     * @apiSuccess (响应结果) {Date} currentPageData.time  数据时间
     * @apiSuccess (响应结果) {Int} currentPageData.kind  传感器数据来源,1 - 自动化传感器 3 - 人工传感器
     * @apiSuccess (响应结果) {Object} currentPageData.data   传感器数据
     * @apiSuccess (响应结果) {Double} currentPageData.data.avgWaterLevel  平均水位
     * @apiSuccess (响应结果) {Double} currentPageData.data.avgWaterFlow  平均流量
     * @apiSuccess (响应结果) {Double} currentPageData.data.waterTotal  总放水量
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryMonitorPointStatisticsDataPage", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryMonitorPointStatisticsDataPage(@Validated @RequestBody GqQueryMonitorPointStatisticsDataPageParam pa) {
        return gqMonitorPointService.gqQueryMonitorPointStatisticsDataPage(pa);
    }


    /**
     * @api {POST} /GqQueryMonitorPointStatisticsDataList 查询灌区监测点统计数据列表
     * @apiVersion 1.0.0
     * @apiGroup 灌区监测数据模块
     * @apiName GqQueryMonitorPointStatisticsDataList
     * @apiDescription 跨监测项目, 查询灌区监测点统计数据列表, 目前只支持:水闸:11,渠系:10
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} projectTypeID 工程类型,(水闸:11,渠系:10)
     * @apiParam (请求体) {Int} [kind] 数据来源,1 - 自动化传感器 3 - 人工传感器
     * @apiParam (请求体) {Int} token 量水类型,(水工建筑量水:3)
     * @apiParam (请求体) {String} [monitorPointName] 监测点名称,模糊查询
     * @apiParam (请求体) {DateTime} begin 开始时间
     * @apiParam (请求体) {DateTime} end   结束时间
     * @apiParam (请求体) {Boolean} [dataSort]  数据排序,为空默认倒序,true为正序,false为倒序
     * @apiParam (请求体) {Int} densityType 密度,(日:3 月:5 年:6)
     * @apiParam (请求体) {Int} statisticsType 统计方式,(平均:2)
     * @apiParamExample 请求体示例
     * {"companyID":1,"kind":"1","token":"sss","monitorPointName":"测测","projectTypeID":10,"densityType":1
     * "statisticsType":1,"begin":"2023-10-06 16:29:31","end":"2023-10-07 16:29:31"}
     * @apiSuccess (响应结果) {Object[]} sensorInfoList  传感器信息列表
     * @apiSuccess (响应结果) {Int} sensorInfoList.companyID       公司ID
     * @apiSuccess (响应结果) {Int} sensorInfoList.monitorPointID       监测点ID
     * @apiSuccess (响应结果) {String} sensorInfoList.monitorPointName  监测点名称
     * @apiSuccess (响应结果) {Int} sensorInfoList.monitorType          监测类型
     * @apiSuccess (响应结果) {String} sensorInfoList.monitorTypeName   监测类型名称
     * @apiSuccess (响应结果) {String} sensorInfoList.monitorTypeAlias  监测类型别名
     * @apiSuccess (响应结果) {Int} sensorInfoList.sensorID    传感器ID
     * @apiSuccess (响应结果) {Int} sensorInfoList.projectID 工程ID
     * @apiSuccess (响应结果) {Int} sensorInfoList.projectTypeID 工程类型
     * @apiSuccess (响应结果) {String} sensorInfoList.projectName 工程名称
     * @apiSuccess (响应结果) {String} sensorInfoList.projectTypeName 工程类型名称
     * @apiSuccess (响应结果) {String} sensorInfoList.waterMeasuringTypeName 量水类型名称
     * @apiSuccess (响应结果) {String} sensorInfoList.sensorName  传感器名称
     * @apiSuccess (响应结果) {Int} sensorInfoList.kind  传感器数据来源,1 - 自动化传感器 3 - 人工传感器
     * @apiSuccess (响应结果) {Date} sensorInfoList.time  时间
     * @apiSuccess (响应结果) {Int} sensorInfoList.data  数据
     * @apiSuccess (响应结果) {Double} sensorInfoList.data.avgWaterLevel  平均水位
     * @apiSuccess (响应结果) {Double} sensorInfoList.data.avgWaterFlow  平均流量
     * @apiSuccess (响应结果) {Double} sensorInfoList.data.waterTotal  总放水量
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqQueryMonitorPointStatisticsDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqQueryMonitorPointStatisticsDataList(@Validated @RequestBody Object pa) {
        return null;
    }


    /**
     * @api {POST} /GqMonitorPointDataPush 监测点数据补录
     * @apiVersion 1.0.0
     * @apiGroup 灌区监测数据模块
     * @apiName GqMonitorPointDataPush
     * @apiDescription 监测点数据补录
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Object[]} dataList 数据列表
     * @apiParam (请求体) {Int} dataList.sid 传感器ID
     * @apiParam (请求体) {DateTime} dataList.time 补录时间
     * @apiParam (请求体) {Object[]} dataList.sensorData 数据列表
     * @apiParam (请求体) {String} dataList.sensorData.fieldToken 子类型字段
     * @apiParam (请求体) {Object} dataList.sensorData.value 数据
     * @apiParamExample 请求体示例
     * {
     * "companyID": 138,
     * "monitorType": 60,
     * "dataList": [
     * {
     * "sid": "999",
     * "time": "2023-12-15 08:00:01",
     * "sensorDataList": [
     * {
     * "fieldToken": "frontwater",
     * "value":1.1
     * },
     * {
     * "fieldToken": "afterwater",
     * "value":2.2
     * },
     * {
     * "fieldToken": "flowRate",
     * "value":32.1
     * },
     * {
     * "fieldToken": "totalFlow",
     * "value":32.1
     * }
     * ]
     * }
     * ]
     * }
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBaseProject
     */
    @Permission(permissionName = "mdmbase:ListBaseProject")
    @RequestMapping(value = "/GqMonitorPointDataPush", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object gqMonitorPointDataPush(@Validated @RequestBody GqMonitorPointDataPushParam pa) {
        return gqMonitorPointService.gqMonitorPointDataPush(pa);
    }

}
