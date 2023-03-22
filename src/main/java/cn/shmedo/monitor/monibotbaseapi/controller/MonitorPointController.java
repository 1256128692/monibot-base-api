package cn.shmedo.monitor.monibotbaseapi.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorPointController {
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
     * @apiPermission 项目权限
     */
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
     * @apiPermission 项目权限
     */
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
     * @apiPermission 项目权限
     */
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
     * @apiPermission 项目权限
     */
    public void configMonitorPointSensors() {
    }

    /**
     * @api {POST} /QueryMonitorPointPageList 分页查询监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName QueryMonitorPointPageList
     * @apiDescription 分页查询监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} [Name] 监测点名称
     * @apiParam (请求体) {Int} [MonitorItemID] 监测项目ID
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 数据总量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (响应结果) {Object[]} dataList 监测点分页列表
     * @apiSuccess (响应结果) {Int} dataList.pointID 监测点ID
     * @apiSuccess (响应结果) {Int} dataList.monitorType 监测类型
     * @apiSuccess (响应结果) {Int} dataList.monitorTypeName 监测类型名称
     * @apiSuccess (响应结果) {Int} dataList.monitorTypeAlias 监测类型别名
     * @apiSuccess (响应结果) {Int} dataList.monitorItemID 监测项目ID
     * @apiSuccess (响应结果) {Int} dataList.monitorItemName 监测项目名称
     * @apiSuccess (响应结果) {Int} dataList.monitorItemAlias 监测项目别名
     * @apiSuccess (响应结果) {String} dataList.pointName 监测点名称
     * @apiSuccess (响应结果) {String} [dataList.installLocation] 安装位置
     * @apiSuccess (响应结果) {String} [dataList.gpsLocation] 地图位置
     * @apiSuccess (响应结果) {String} [dataList.imageLocation] 底图位置
     * @apiSuccess (响应结果) {String} [dataList.overallViewLocation] 全景位置
     * @apiSuccess (响应结果) {String} [dataList.spatialLocation] 三维位置
     * @apiSuccess (响应结果) {Bool} dataList.enable 是否启用
     * @apiSuccess (响应结果) {String} [dataList.exValues] 拓展字段
     * @apiSuccess (响应结果) {Int} dataList.displayOrder 排序字段
     * @apiSuccess (响应结果) {Object[]} dataList.sensorList 监测传感器列表
     * @apiSuccess (响应结果) {Int} dataList.sensorList.sensorID 传感器ID
     * @apiSuccess (响应结果) {String} dataList.sensorList.name 传感器名称
     * @apiSuccess (响应结果) {String} dataList.sensorList.alias 传感器别名
     * @apiSuccess (响应结果) {Int} dataList.sensorList.kind 传感器类型
     * @apiSuccess (响应结果) {Int} dataList.sensorList.status 传感器状态
     * @apiSuccess (响应结果) {Int} dataList.sensorList.status 传感器状态
     * @apiSampleRequest off
     * @apiPermission 项目权限
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
     * @apiPermission 项目权限
     */
    public Object queryMonitorPointSimpleList(){
        return null;
    }
}
