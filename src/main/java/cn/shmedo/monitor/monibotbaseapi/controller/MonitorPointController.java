package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointBaseInfoListParam;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MonitorPointController {
    private WtMonitorService reservoirMonitorService;

    /**
     * @api {POST} /AddMonitorPoint 新增监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName AddMonitorPoint
     * @apiDescription 新增监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {String} name 监测点名称
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {String} [gpsLocation] 地图位置
     * @apiParam (请求体) {String} [imageLocation] 底图位置
     * @apiParam (请求体) {String} [overallViewLocation] 全景位置
     * @apiParam (请求体) {String} [spatialLocation] 三维位置
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
//    @Permission(permissionName = "mdmbase:UpdateMonitorPoint")
    @PostMapping(value = "/AddMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public void addMonitorPoint() {
    }

    /**
     * @api {POST} /UpdateMonitorPoint 修改监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName UpdateMonitorPoint
     * @apiDescription 修改监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} pointID 监测点ID
     * @apiParam (请求体) {String} name 监测点名称
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {String} [gpsLocation] 地图位置
     * @apiParam (请求体) {String} [imageLocation] 底图位置
     * @apiParam (请求体) {String} [overallViewLocation] 全景位置
     * @apiParam (请求体) {String} [spatialLocation] 三维位置
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
    //    @Permission(permissionName = "mdmbase:UpdateMonitorPoint")
    @PostMapping(value = "/UpdateMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public void updateMonitorPoint() {
    }

    /**
     * @api {POST} /DeleteMonitorPoint 删除监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName DeleteMonitorPoint
     * @apiDescription 删除监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int[]} pointIDList 监测点ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteBaseMonitorPoint
     */
    //    @Permission(permissionName = "mdmbase:DeleteMonitorPoint")
    @PostMapping(value = "/DeleteMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public void deleteMonitorPoint() {
    }

    /**
     * @api {POST} /ConfigMonitorPointSensors 配置监测点传感器
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName ConfigMonitorPointSensors
     * @apiDescription 配置监测点传感器
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} pointID 监测点ID
     * @apiParam (请求体) {Int[]} sensorIDList 配置传感器列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
    @PostMapping(value = "/ConfigMonitorPointSensors", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public void configMonitorPointSensors() {
    }

    /**
     * @api {POST} /QueryMonitorPointPageList 分页查询监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName QueryMonitorPointPageList
     * @apiDescription 分页查询监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} [pointName] 监测点名称,模糊查询
     * @apiParam (请求体) {Int} [monitorType] 监测项目
     * @apiParam (请求体) {String} [monitorItemName] 监测项目名称
     * @apiParam (请求体) {Int} [sensorID] 传感器ID
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} dataList 监测点分页列表
     * @apiSuccess (返回结果) {Int} dataList.pointID 监测点ID
     * @apiSuccess (返回结果) {Int} dataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeAlias 监测类型别名
     * @apiSuccess (返回结果) {Int} dataList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} dataList.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {String} dataList.pointName 监测点名称
     * @apiSuccess (返回结果) {String} [dataList.installLocation] 安装位置
     * @apiSuccess (返回结果) {String} [dataList.gpsLocation] 地图位置
     * @apiSuccess (返回结果) {String} [dataList.imageLocation] 底图位置
     * @apiSuccess (返回结果) {String} [dataList.overallViewLocation] 全景位置
     * @apiSuccess (返回结果) {String} [dataList.spatialLocation] 三维位置
     * @apiSuccess (返回结果) {Bool} dataList.enable 是否启用
     * @apiSuccess (返回结果) {String} [dataList.exValues] 拓展字段
     * @apiSuccess (返回结果) {Int} dataList.displayOrder 排序字段
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList 监测传感器列表
     * @apiSuccess (返回结果) {Int} dataList.sensorList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.name 传感器名称
     * @apiSuccess (返回结果) {String} dataList.sensorList.alias 传感器别名
     * @apiSuccess (返回结果) {Int} dataList.sensorList.kind 传感器类型
     * @apiSuccess (返回结果) {Int} dataList.sensorList.status 传感器状态
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    public Object queryMonitorPointPageList() {
        return null;
    }

    /**
     * @api {POST} /QueryMonitorPointSimpleList 查询监测点简要信息列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorPointSimpleList
     * @apiDescription 查询监测点简要信息列表
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} [groupID] 监测组ID
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiSuccess (返回结果) {Object[]} monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointName 监测点名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    public Object queryMonitorPointSimpleList(){
        return null;
    }

    /**
     * @api {POST} /QueryMonitorItemPointList 查询监测项目下监测点列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorItemPointList
     * @apiDescription 查询监测项目下监测点列表
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int[]} [monitorItemID] 监测项目ID列表
     * @apiSuccess (返回结果) {Int} monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Object[]} monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {String} [monitorPointList.installLocation] 安装位置
     * @apiSuccess (返回结果) {String} [monitorPointList.gpsLocation] 地图位置
     * @apiSuccess (返回结果) {String} [monitorPointList.imageLocation] 底图位置
     * @apiSuccess (返回结果) {String} [monitorPointList.overallViewLocation] 全景位置
     * @apiSuccess (返回结果) {String} [monitorPointList.spatialLocation] 三维位置
     * @apiSuccess (返回结果) {Bool} monitorPointList.enable 是否启用
     * @apiSuccess (返回结果) {String} [monitorPointList.exValues] 拓展字段
     * @apiSuccess (返回结果) {Int} monitorPointList.displayOrder 排序字段
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    public Object queryMonitorItemPointList(){
        return null;
    }


    /**
     * @api {POST} /QueryMonitorPointBaseInfoList 查询监测点基本信息
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiDescription 查询监测点基本信息
     * @apiName QueryMonitorPointBaseInfoList
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParamExample 请求体示例
     * {"projectID":"1"}
     * @apiSuccess (返回结果) {Object} data   结果数据
     * @apiSuccess (返回结果) {Object[]} data.tbMonitorTypes   监测类型列表
     * @apiSuccess (返回结果) {Int} data.tbMonitorItems.monitorType   监测类型
     * @apiSuccess (返回结果) {String} data.tbMonitorItems.typeName   监测类型名称
     * @apiSuccess (返回结果) {String} data.tbMonitorItems.typeAlias   监测类型别名
     * @apiSuccess (返回结果) {Object[]} data.tbMonitorItems   监测项目列表
     * @apiSuccess (返回结果) {Int} data.tbMonitorItems.id   监测项目id
     * @apiSuccess (返回结果) {String} data.tbMonitorItems.name   监测项目名称
     * @apiSuccess (返回结果) {String} data.tbMonitorItems.alias   监测项目别名
     * @apiSuccess (返回结果) {Object[]} data.tbMonitors   监测点列表
     * @apiSuccess (返回结果) {Int} data.tbMonitors.id   监测点id
     * @apiSuccess (返回结果) {String} data.tbMonitors.name   监测点名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorPoint
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorPoint")
    @RequestMapping(value = "/QueryMonitorPointBaseInfoList", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object queryMonitorPointBaseInfoList(@Validated @RequestBody QueryMonitorPointBaseInfoListParam pa) {
        return reservoirMonitorService.queryMonitorPointBaseInfoList(pa.getProjectID());
    }
}
