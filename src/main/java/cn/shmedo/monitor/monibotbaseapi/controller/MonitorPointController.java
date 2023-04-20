package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorPointBaseInfoListParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorPointService;
import cn.shmedo.monitor.monibotbaseapi.service.WtMonitorService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class MonitorPointController {
    private WtMonitorService reservoirMonitorService;
    private MonitorPointService monitorPointService;

    /**
     * @api {POST} /AddMonitorPoint 新增监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName AddMonitorPoint
     * @apiDescription 新增监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Int} monitorItemID 监测项目ID
     * @apiParam (请求体) {String} name 监测点名称(max = 50)
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {String} [gpsLocation] 地图位置
     * @apiParam (请求体) {String} [imageLocation] 底图位置
     * @apiParam (请求体) {String} [overallViewLocation] 全景位置
     * @apiParam (请求体) {String} [spatialLocation] 三维位置
     * @apiParam (请求体) {String} [exValues] 额外属性（500）
     * @apiParam (请求体) {Int} [displayOrder] 排序
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
//    @Permission(permissionName = "mdmbase:UpdateMonitorPoint")
    @PostMapping(value = "/AddMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorPoint(@Validated @RequestBody AddMonitorPointParam pa) {
        monitorPointService.addMonitorPoint(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /AddMonitorPointBatch 批量新增监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName AddMonitorPointBatch
     * @apiDescription 批量新增监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiParam (请求体) {Object[]} addPointItemList 新增监测点列表(max = 100)
     * @apiParam (请求体) {String} addPointItemList.name 监测点名称(max = 50)
     * @apiParam (请求体) {Int} addPointItemList.monitorItemID 监测项目ID
     * @apiParam (请求体) {Bool} addPointItemList.enable 是否启用
     * @apiParam (请求体) {String} [addPointItemList.gpsLocation] 地图位置
     * @apiParam (请求体) {String} [addPointItemList.imageLocation] 底图位置
     * @apiParam (请求体) {String} [addPointItemList.overallViewLocation] 全景位置
     * @apiParam (请求体) {String} [addPointItemList.spatialLocation] 三维位置
     * @apiParam (请求体) {String} [addPointItemList.exValues] 额外属性（500）
     * @apiParam (请求体) {Int} [addPointItemList.displayOrder] 排序
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
//    @Permission(permissionName = "mdmbase:UpdateMonitorPoint")
    @PostMapping(value = "/AddMonitorPointBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorPointBatch(@Validated @RequestBody AddMonitorPointBatchParam pa) {
        monitorPointService.addMonitorPointBatch(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
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
     * @apiParam (请求体) {String} [exValues] 额外属性（500）
     * @apiParam (请求体) {Int} [displayOrder] 排序
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
    //    @Permission(permissionName = "mdmbase:UpdateMonitorPoint")
    @PostMapping(value = "/UpdateMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorPoint(@Validated @RequestBody UpdateMonitorPointParam pa) {
        monitorPointService.updateMonitorPoint(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateMonitorPointBatch 批量修改监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName UpdateMonitorPointBatch
     * @apiDescription 批量修改监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Object[]} updatePointItemList 修改监测点列表(max = 100)
     * @apiParam (请求体) {Int} updatePointItemList.pointID 监测点ID
     * @apiParam (请求体) {String} updatePointItemList.name 监测点名称
     * @apiParam (请求体) {Bool} updatePointItemList.enable 是否启用
     * @apiParam (请求体) {String} [updatePointItemList.gpsLocation] 地图位置
     * @apiParam (请求体) {String} [updatePointItemList.imageLocation] 底图位置
     * @apiParam (请求体) {String} [updatePointItemList.overallViewLocation] 全景位置
     * @apiParam (请求体) {String} [updatePointItemList.spatialLocation] 三维位置
     * @apiParam (请求体) {String} [updatePointItemList.exValues] 额外属性（500）
     * @apiParam (请求体) {Int} [updatePointItemList.displayOrder] 排序
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
    //    @Permission(permissionName = "mdmbase:UpdateMonitorPoint")
    @PostMapping(value = "/UpdateMonitorPointBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorPointBatch(@Validated @RequestBody UpdateMonitorPointBatchParam pa) {
        monitorPointService.updateMonitorPointBatch(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeleteMonitorPoint 删除监测点
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName DeleteMonitorPoint
     * @apiDescription 删除监测点, 会判断点下传感器
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int[]} pointIDList 监测点ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteBaseMonitorPoint
     */
    //    @Permission(permissionName = "mdmbase:DeleteMonitorPoint")
    @PostMapping(value = "/DeleteMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorPoint(@Validated @RequestBody DeleteMonitorPointParam pa) {
        monitorPointService.deleteMonitorPoint(pa.getPointIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /ConfigMonitorPointSensors 配置监测点传感器
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName ConfigMonitorPointSensors
     * @apiDescription 配置监测点传感器
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} pointID 监测点ID
     * @apiParam (请求体) {Int[]} sensorIDList 配置传感器列表(max = 100)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorPoint
     */
    @PostMapping(value = "/ConfigMonitorPointSensors", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object configMonitorPointSensors(@Validated @RequestBody ConfigMonitorPointSensorsParam pa) {
        monitorPointService.configMonitorPointSensors(pa.getPointID(), pa.getSensorIDList(), CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
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
     * @apiSuccess (返回结果) {Int} dataList.id 监测点ID
     * @apiSuccess (返回结果) {Int} dataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeAlias 监测类型别名
     * @apiSuccess (返回结果) {Boolean} dataList.monitorTypeMultiSensor 监测类型是否多传感器
     * @apiSuccess (返回结果) {Int} dataList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} dataList.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {String} dataList.name 监测点名称
     * @apiSuccess (返回结果) {String} [dataList.installLocation] 安装位置
     * @apiSuccess (返回结果) {String} [dataList.gpsLocation] 地图位置
     * @apiSuccess (返回结果) {String} [dataList.imageLocation] 底图位置
     * @apiSuccess (返回结果) {String} [dataList.overallViewLocation] 全景位置
     * @apiSuccess (返回结果) {String} [dataList.spatialLocation] 三维位置
     * @apiSuccess (返回结果) {Bool} dataList.enable 是否启用
     * @apiSuccess (返回结果) {String} [dataList.exValues] 拓展字段
     * @apiSuccess (返回结果) {Int} dataList.displayOrder 排序字段
     * @apiSuccess (返回结果) {Object[]} dataList.sensorList 监测传感器列表
     * @apiSuccess (返回结果) {Int} dataList.sensorList.id 传感器ID
     * @apiSuccess (返回结果) {String} dataList.sensorList.name 传感器名称
     * @apiSuccess (返回结果) {String} dataList.sensorList.alias 传感器别名
     * @apiSuccess (返回结果) {Int} dataList.sensorList.kind 传感器类型
     * @apiSuccess (返回结果) {Int} dataList.sensorList.status 传感器状态
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @PostMapping(value = "/QueryMonitorPointPageList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)

    public Object queryMonitorPointPageList(@Validated @RequestBody QueryMonitorPointPageListParam pa) {
        return monitorPointService.queryMonitorPointPageList(pa);
    }

    /**
     * @api {POST} /QueryMonitorPointSimpleList 查询监测点简要信息列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName QueryMonitorPointSimpleList
     * @apiDescription 查询监测点简要信息列表
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} [groupID] 监测组ID
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiSuccess (返回结果) {Object[]} monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} monitorPointList.id 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointList.name 监测点名称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @PostMapping(value = "/QueryMonitorPointSimpleList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorPointSimpleList(@Validated @RequestBody QueryMonitorPointSimpleListParam pa) {
        return monitorPointService.queryMonitorPointSimpleList(pa);
    }

    /**
     * @api {POST} /QueryMonitorItemPointList 查询监测项目下监测点列表
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiName QueryMonitorItemPointList
     * @apiDescription 查询监测项目下监测点列表
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiSuccess (返回结果) {Object[]} list
     * @apiSuccess (返回结果) {Int} list.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} list.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} list.monitorItemAlias 监测项目别名
     * @apiSuccess (返回结果) {Int} list.monitorType 监测类型
     * @apiSuccess (返回结果) {String} list.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} list.monitorTypeAlias 监测类型别名
     * @apiSuccess (返回结果) {Object[]} list.monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} list.monitorPointList.id 监测点ID
     * @apiSuccess (返回结果) {String} list.monitorPointList.name 监测点名称
     * @apiSuccess (返回结果) {String} [list.monitorPointList.installLocation] 安装位置
     * @apiSuccess (返回结果) {String} [list.monitorPointList.gpsLocation] 地图位置
     * @apiSuccess (返回结果) {String} [list.monitorPointList.imageLocation] 底图位置
     * @apiSuccess (返回结果) {String} [list.monitorPointList.overallViewLocation] 全景位置
     * @apiSuccess (返回结果) {String} [list.monitorPointList.spatialLocation] 三维位置
     * @apiSuccess (返回结果) {Bool} list.monitorPointList.enable 是否启用
     * @apiSuccess (返回结果) {String} [list.monitorPointList.exValues] 拓展字段
     * @apiSuccess (返回结果) {Int} [list.monitorPointList.displayOrder] 排序字段
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorPoint
     */
    @PostMapping(value = "/QueryMonitorItemPointList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)

    public Object queryMonitorItemPointList(@Validated @RequestBody QueryMonitorItemPointListParam pa) {
        return monitorPointService.queryMonitorItemPointList(pa);
    }


    /**
     * @api {POST} /QueryMonitorPointBaseInfoList 查询监测点基本信息
     * @apiVersion 1.0.0
     * @apiGroup 监测点模块
     * @apiDescription 查询监测点基本信息
     * @apiName QueryMonitorPointBaseInfoList
     * @apiParam (请求体) {Int} projectID  工程ID
     * @apiParam (请求体) {Int} monitorClass  监测类别
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
        return reservoirMonitorService.queryMonitorPointBaseInfoList(pa);
    }
}
