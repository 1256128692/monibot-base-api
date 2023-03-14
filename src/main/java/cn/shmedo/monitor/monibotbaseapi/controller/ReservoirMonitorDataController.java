package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.*;
import cn.shmedo.monitor.monibotbaseapi.service.ReservoirMonitorService;
import cn.shmedo.monitor.monibotbaseapi.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @api {POST} /QueryReservoirMonitorPointList 查询水库项目监测点最新数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName QueryReservoirMonitorPointList
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} [projectTypeID] 工程类型(水库:1 河道:2 提防:3 流域:4 尾矿库:5 基坑:6)
     * @apiParam (请求体) {Int} [monitorType] 环境监测类型(项目) (例如: 水位:XX 流速:XX 水质:XX 雨量:XX ...)
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目类型 (水位:[坝前水位:xx])
     * @apiParam (请求体) {String} [town] 行政区划
     * @apiParamExample 请求体示例
     * {"companyID":1866,"projectTypeID":1,"town":"CYu56","monitorType":11,"monitorItemID":1}
     * @apiSuccess (响应结果) {Object[]} data                监测点数据
     * @apiSuccess (响应结果) {Int} data.monitorPointID      监测项目点ID
     * @apiSuccess (响应结果) {String} data.monitorPointName 监测项目点名称(例如:测点1)
     * @apiSuccess (响应结果) {Int} data.monitorTypeID       监测项目类型ID
     * @apiSuccess (响应结果) {String} data.monitorTypeName  监测项目类型名称(例如:水位)
     * @apiSuccess (响应结果) {Int} data.monitorItemID       监测项目子类型ID
     * @apiSuccess (响应结果) {String} data.monitorItemName  监测项目子类型名称(例如:坝前水位)
     * @apiSuccess (响应结果) {Int} data.projectTypeID       工程类型ID
     * @apiSuccess (响应结果) {Int} data.projectTypeName     工程类型名称(例如:水库)
     * @apiSuccess (响应结果) {Int} data.projectID           工程ID
     * @apiSuccess (响应结果) {String} data.projectName      工程名称(例如:xxx城市项目水库)
     * @apiSuccess (响应结果) {Int} data.sensorID            传感器ID
     * @apiSuccess (响应结果) {Date} data.time               采集时间
     * @apiSuccess (响应结果) {Object[]} regionArea          行政区划
     * @apiSuccess (响应结果) {String} regionArea.name       行政区划名称
     * @apiSuccess (响应结果) {String} regionArea.shortName  行政区划别称
     * @apiSuccess (响应结果) {Object[]} sensorData          传感器数据
     * @apiSuccess (响应结果) {Object} [sensorData.fieldToken1] 传感器物模型字段，值类型为物模型的值类型，比如Long,Double,String,Bool。Object,Array被转换为字符串.Binary被转换为Base64字符串
     * @apiSuccess (响应结果) {Object} [sensorData.fieldToken2] 传感器物模型字段，值类型为物模型的值类型，比如Long,Double,String,Bool。Object,Array被转换为字符串.Binary被转换为Base64字符串
     * @apiSuccessExample 响应结果示例
     * {}
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryReservoirMonitorPointList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryReservoirMonitorPointList(@Validated @RequestBody QueryReservoirMonitorPointListParam pa) {
        return reservoirMonitorService.queryReservoirMonitorPointList(pa);
    }


    /**
     * @api {POST} /QueryReservoirMonitorPointDescribe 查询监测点最新数据详情
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName QueryReservoirMonitorPointDescribe
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParamExample 请求体示例
     * {"companyID":555,"monitorPointID":4664}
     * @apiSuccess (响应结果) {Int} monitorPointID      监测项目点ID
     * @apiSuccess (响应结果) {String} monitorPointName 监测项目点名称
     * @apiSuccess (响应结果) {Int} monitorItemID       监测项目子类型ID
     * @apiSuccess (响应结果) {String} monitorItemName  监测项目子类型名称(例如:坝前水位)
     * @apiSuccess (响应结果) {Int} sensorStatus        传感器状态
     * @apiSuccess (响应结果) {String} sensorStatusName 传感器状态名称
     * @apiSuccess (响应结果) {Int} projectTypeID       工程类型ID
     * @apiSuccess (响应结果) {Int} projectTypeName     工程类型名称(例如:水库)
     * @apiSuccess (响应结果) {Int} projectID           工程ID
     * @apiSuccess (响应结果) {String} projectName      工程名称(例如:xxx城市项目水库)
     * @apiSuccess (响应结果) {Int} sensorID                    传感器ID
     * @apiSuccess (响应结果) {Date} time                       采集时间
     * @apiSuccess (响应结果) {Object[]} sensorData             传感器数据
     * @apiSuccess (响应结果) {Object} [sensorData.fieldToken1] 传感器物模型字段，值类型为物模型的值类型，比如Long,Double,String,Bool。Object,Array被转换为字符串.Binary被转换为Base64字符串
     * @apiSuccess (响应结果) {Object} [sensorData.fieldToken2] 传感器物模型字段，值类型为物模型的值类型，比如Long,Double,String,Bool。Object,Array被转换为字符串.Binary被转换为Base64字符串
     * @apiSuccessExample 响应结果示例
     * {}
     */
    @RequestMapping(value = "/QueryReservoirMonitorPointDescribe", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryReservoirMonitorPointDescribe(@Validated @RequestBody QueryReservoirMonitorPointDescribeParam pa) {
        return null;
    }


    /**
     * @api {POST} /StatisticsReservoirMonitorPointType 统计当前公司下的水库监测点类型数量以及项目状态数量
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName StatisticsReservoirMonitorPointType
     * @apiParam (请求体) {Int} companyID
     * @apiParamExample 请求体示例
     * {"companyID":7986}
     * @apiSuccess (响应结果) {Int} waterLevelCount     水位类型的项目数量
     * @apiSuccess (响应结果) {Int} flowRateCount       流速类型的项目数量
     * @apiSuccess (响应结果) {Int} waterQualityCount   水质类型的项目数量
     * @apiSuccess (响应结果) {Int} rainfallCount       雨量类型的项目数量
     * @apiSuccess (响应结果) {Int} temperatureCount    温度类型的项目数量
     * @apiSuccess (响应结果) {Int} humidityCount       湿度类型的项目数量
     * @apiSuccess (响应结果) {Int} windSpeedCount      风速类型的项目数量
     * @apiSuccess (响应结果) {Int} shangQingCount      墒情类型的项目数量
     * @apiSuccess (响应结果) {Int} sandContentCount    含沙量类型的项目数量
     * @apiSuccess (响应结果) {Int} normalCount         正常的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelOneCount   一级警报的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelTwoCount   二级警报的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelThreeCount 三级警报的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelFourCount  四级警报的项目数量
     * @apiSuccessExample 响应结果示例
     * {}
     */
    @RequestMapping(value = "/StatisticsReservoirMonitorPointType", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object statisticsReservoirMonitorPointType(@Validated @RequestBody StatisticsReservoirMonitorPointTypeParam pa) {
        return null;
    }


    /**
     * @api {POST} /QueryReservoirMonitorPointSensorDataList 查询水库监测点下传感器的数据
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName QueryReservoirMonitorPointSensorDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {Date} begin 开始时间
     * @apiParam (请求体) {Date} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2021-09-27 00:00:00","end":"2021-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Int} sensorID                    传感器ID
     * @apiSuccess (响应结果) {Date} time                       采集时间
     * @apiSuccess (响应结果) {Object[]} sensorData             传感器数据
     * @apiSuccess (响应结果) {Object} [sensorData.fieldToken1] 传感器物模型字段，值类型为物模型的值类型，比如Long,Double,String,Bool。Object,Array被转换为字符串.Binary被转换为Base64字符串
     * @apiSuccess (响应结果) {Object} [sensorData.fieldToken2] 传感器物模型字段，值类型为物模型的值类型，比如Long,Double,String,Bool。Object,Array被转换为字符串.Binary被转换为Base64字符串
     * @apiSuccessExample 响应结果示例
     * {}
     */
    @RequestMapping(value = "/QueryReservoirMonitorPointSensorDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryReservoirMonitorPointSensorDataList(@Validated @RequestBody QueryReservoirMonitorPointSensorDataListParam pa) {
        return null;
    }

}


