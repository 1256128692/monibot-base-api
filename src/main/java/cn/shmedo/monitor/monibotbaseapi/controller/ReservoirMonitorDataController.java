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
     * @api {POST} /QueryMonitorPointList 查询水库项目监测点最新数据列表
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName QueryMonitorPointList
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
     * @apiSuccess (响应结果) {Int} data.projectTypeID       工程项目类型ID
     * @apiSuccess (响应结果) {Int} data.projectTypeName     工程项目类型名称(例如:水库)
     * @apiSuccess (响应结果) {Int} data.projectID           项目ID
     * @apiSuccess (响应结果) {String} data.projectName      项目名称(例如:xxx城市项目水库)
     * @apiSuccess (响应结果) {Object[]} regionArea          行政区划
     * @apiSuccess (响应结果) {String} regionArea.name       行政区划名称
     * @apiSuccess (响应结果) {String} regionArea.shortName  行政区划别称
     * @apiSuccess (响应结果) {Object[]} sensorDataList         传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorDataList.sensorID     传感器ID
     * @apiSuccess (响应结果) {Date} sensorDataList.time        采集时间
     * @apiSuccess (响应结果) {Map} sensorDataList.data         传感器数据键值对
     * @apiSuccess (响应结果) {Object} [data.key] 传感器类型名称
     * @apiSuccess (响应结果) {Object} [data.value] 传感器最新数据值
     * @apiSuccessExample 响应结果示例
     * {}
     */
//    @Permission(permissionName = "mdmbase:DescribeBaseProject")
    @RequestMapping(value = "/QueryMonitorPointList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointList(@Validated @RequestBody QueryMonitorPointListParam pa) {
        return reservoirMonitorService.queryMonitorPointList(pa);
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
     * @apiSuccess (响应结果) {Int} projectTypeID       工程项目类型ID
     * @apiSuccess (响应结果) {Int} projectTypeName     工程项目类型名称(例如:水库)
     * @apiSuccess (响应结果) {Int} projectID           项目ID
     * @apiSuccess (响应结果) {String} projectName      项目名称(例如:xxx城市项目水库)
     * @apiSuccess (响应结果) {Object[]} sensorDataList         传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorDataList.sensorID     传感器ID
     * @apiSuccess (响应结果) {Date} sensorDataList.time        采集时间
     * @apiSuccess (响应结果) {Map} sensorDataList.data         传感器数据键值对
     * @apiSuccess (响应结果) {Object} [data.key] 传感器类型名称
     * @apiSuccess (响应结果) {Object} [data.value] 传感器最新数据值
     * @apiSuccessExample 响应结果示例
     * {}
     */
    @RequestMapping(value = "/QueryReservoirMonitorPointDescribe", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryReservoirMonitorPointDescribe(@Validated @RequestBody QueryReservoirMonitorPointDescribeParam pa) {
        return null;
    }


    /**
     * @api {POST} /StatisticsMonitorPointType 统计当前公司下的监测点类型数量以及项目状态数量
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName StatisticsMonitorPointType
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} queryType 查询类型(0:环境监测, 1:安全监测, 2:工情监测 3:防洪调度指挥监测 4:视频监测)
     * @apiParamExample 请求体示例
     * {"companyID":7986,"queryType":0}
     * @apiSuccess (响应结果) {Object[]} data           具体查询类型的对应内容
     * @apiSuccess (响应结果) {Int} normalCount         正常的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelOneCount   一级警报的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelTwoCount   二级警报的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelThreeCount 三级警报的项目数量
     * @apiSuccess (响应结果) {Int} warnLevelFourCount  四级警报的项目数量
     * @apiSuccessExample 响应结果示例
     * {}
     */
    @RequestMapping(value = "/StatisticsMonitorPointType", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object statisticsMonitorPointType(@Validated @RequestBody StatisticsMonitorPointTypeParam pa) {
        return null;
    }


    /**
     * @api {POST} /QueryMonitorPointSensorDataList 查询监测点下传感器的数据
     * @apiVersion 1.0.0
     * @apiGroup 水库模块
     * @apiName QueryMonitorPointSensorDataList
     * @apiParam (请求体) {Int} projectID  项目ID
     * @apiParam (请求体) {Int} monitorType  监测类型
     * @apiParam (请求体) {Int} monitorPointIDList 监测点ID列表,监测点的监测类型必须保持一致
     * @apiParam (请求体) {Date} begin 开始时间
     * @apiParam (请求体) {Date} end   结束时间
     * @apiParam (请求体) {String} [density] 密度,(2h:2小时一组的密度  2d:2天一组的密度),null:查全部, 不为null时,结尾必须是h或者d,前面数字可以任意改变
     * @apiParamExample 请求体示例
     * {"monitorPointID":9182,"density":"2h","begin":"2021-09-27 00:00:00","end":"2021-09-28 00:00:00","projectID":5861}
     * @apiSuccess (响应结果) {Object[]} sensorDataList         传感器数据列表
     * @apiSuccess (响应结果) {Int} sensorDataList.sensorID     传感器ID
     * @apiSuccess (响应结果) {Date} sensorDataList.time        采集时间
     * @apiSuccess (响应结果) {Map} sensorDataList.data         传感器数据键值对
     * @apiSuccess (响应结果) {Object} [data.key] 传感器类型名称
     * @apiSuccess (响应结果) {Object} [data.value] 传感器最新数据值
     * @apiSuccessExample 响应结果示例
     * {"sensorDataList":[
     * {"sensorID":1,"time":"2021-09-27 00:00:00","data":["value":1.5]},
     * {"sensorID":2,"time":"2021-09-27 00:00:00","data":["flow":1.5,"speed":2.8]}]
     * }
     */
    @RequestMapping(value = "/QueryMonitorPointSensorDataList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointSensorDataList(@Validated @RequestBody QueryMonitorPointSensorDataListParam pa) {
        return null;
    }

}


