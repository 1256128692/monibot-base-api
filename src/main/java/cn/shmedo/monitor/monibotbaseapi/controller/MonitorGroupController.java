package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.LogParam;
import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.OperationProperty;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorgroup.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.QueryMonitorItemPointListParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorGroupService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class MonitorGroupController {
    private final MonitorGroupService monitorGroupService;

    /**
     * @api {POST} /AddMonitorGroup 新建监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName AddMonitorGroup
     * @apiDescription 新建监测组, 1级组只能加监测项目，二级组只能加监测点
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} [parentID] 父监测组ID
     * @apiParam (请求体) {String} name 监测组名称(50)
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表(50)
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表(50)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorGroup
     */
    @LogParam(moduleName = "监测组模块", operationName = "新建监测组", operationProperty = OperationProperty.ADD)
    @Permission(permissionName = "mdmbase:UpdateBaseMonitorGroup")
    @PostMapping(value = "/AddMonitorGroup", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorGroup(@RequestBody @Validated AddMonitorGroupParam pa) {
        monitorGroupService.addMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdateMonitorGroup 修改监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName UpdateMonitorGroup
     * @apiDescription 修改监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiParam (请求体) {String} name 监测组名称(50)
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:UpdateBaseMonitorGroup")
    @LogParam(moduleName = "监测组模块", operationName = "修改监测组", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "/UpdateMonitorGroup", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorGroup(@RequestBody @Validated UpdateMonitorGroupParam pa) {
        monitorGroupService.updateMonitorGroup(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UploadMonitorGroupImage 上传监测组底图
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName UploadMonitorGroupImage
     * @apiDescription 修改监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiParam (请求体) {String} fileName 文件名称(100)
     * @apiParam (请求体) {String} imageSuffix 文件后缀
     * @apiParam (请求体) {String} imageContent 文件base64内容
     * @apiParam (请求体) {Boolean} [cleanLocation] 清理该组的点位置
     * @apiSuccess (返回结果) {String} url 可用图片地址
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:UpdateBaseMonitorGroup")
    @LogParam(moduleName = "监测组模块", operationName = "上传监测组底图", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "/UploadMonitorGroupImage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object uploadMonitorGroupImage(@RequestBody @Validated UploadMonitorGroupImageParam pa) {
        return monitorGroupService.uploadMonitorGroupImage(pa, CurrentSubjectHolder.getCurrentSubject().getSubjectID());
    }

    /**
     * @api {POST} /DeleteMonitorGroup 删除监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName DeleteMonitorGroup
     * @apiDescription 删除监测组, 会连带删除子监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int[]} groupIDList 监测组ID列表(max = 10)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DeleteBaseMonitorGroup
     */
    @LogParam(moduleName = "监测组模块", operationName = "删除监测组", operationProperty = OperationProperty.DELETE)
    @Permission(permissionName = "mdmbase:DeleteMonitorGroup")
    @PostMapping(value = "/DeleteMonitorGroup", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deleteMonitorGroup(@RequestBody @Validated DeleteMonitorGroupParam pa) {
        monitorGroupService.deleteMonitorGroup(pa.getGroupIDList());
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /ConfigMonitorPointImageLocation 配置监测点底图定位
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName ConfigMonitorPointImageLocation
     * @apiDescription 配置监测点底图定位
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiParam (请求体) {Object[]} pointLocationList 监测点位置列表(max=20)
     * @apiParam (请求体) {Int} pointLocationList.pointID 监测点ID
     * @apiParam (请求体) {String} [pointLocationList.location] 监测点位置
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:UpdateBaseMonitorGroup")
    @LogParam(moduleName = "监测组模块", operationName = "配置监测点底图定位", operationProperty = OperationProperty.UPDATE)
    @PostMapping(value = "/ConfigMonitorPointImageLocation", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object configMonitorPointImageLocation(@RequestBody @Validated ConfigMonitorPointImageLocationParam pa) {
        monitorGroupService.configMonitorPointImageLocation(pa);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /QueryMonitorGroupPage 分页查询监测组列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorGroupPage
     * @apiDescription 分页查询监测组列表
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} [groupName] 监测组名称(一级组)
     * @apiParam (请求体) {String} [secondaryGroupName] 监测组名称(二级组)
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Int} currentPageData.groupID 监测组ID
     * @apiSuccess (返回结果) {String} currentPageData.groupName 监测组名称
     * @apiSuccess (返回结果) {Boolean} currentPageData.enable 是否启用
     * @apiSuccess (返回结果) {String} [currentPageData.imagePath] 底图地址
     * @apiSuccess (返回结果) {String} [currentPageData.exValue] 拓展字段
     * @apiSuccess (返回结果) {Int} currentPageData.displayOrder 排序字段
     * @apiSuccess (返回结果) {Object[]} currentPageData.monitorItemList 监测项目列表
     * @apiSuccess (返回结果) {Int} currentPageData.monitorItemList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} currentPageData.monitorItemList.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Object[]} currentPageData.monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.imageLocation 底图位置
     * @apiSuccess (返回结果) {Object[]} currentPageData.childGroupList 子监测组列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorGroup")
    @PostMapping(value = "/QueryMonitorGroupPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorGroupPage(@RequestBody @Validated QueryMonitorGroupPageParam pa) {
        return monitorGroupService.queryMonitorGroupPage(pa);
    }

    /**
     * @api {POST} /QueryMonitorGroupList 查询监测组列表列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorGroupList
     * @apiDescription 查询监测组列表列表, 基于QueryMonitorGroupPage
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} [groupName] 监测组名称(一级组)
     * @apiParam (请求体) {String} [secondaryGroupName] 监测组名称(二级组)
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiSuccess (返回结果) {Object[]} data 数据列表
     * @apiSuccess (返回结果) {Int} data.groupID 监测组ID
     * @apiSuccess (返回结果) {String} data.groupName 监测组名称
     * @apiSuccess (返回结果) {Boolean} data.enable 是否启用
     * @apiSuccess (返回结果) {String} [data.imagePath] 底图地址
     * @apiSuccess (返回结果) {String} [data.exValue] 拓展字段
     * @apiSuccess (返回结果) {Int} data.displayOrder 排序字段
     * @apiSuccess (返回结果) {Object[]} data.monitorItemList 监测项目列表
     * @apiSuccess (返回结果) {Int} data.monitorItemList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} data.monitorItemList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} data.monitorItemList.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Object[]} data.monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} data.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} data.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} data.monitorPointList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} data.monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} data.monitorPointList.imageLocation 底图位置
     * @apiSuccess (返回结果) {Object[]} data.childGroupList 子监测组列表
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorGroup")
    @PostMapping(value = "/QueryMonitorGroupList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorGroupList(@RequestBody @Validated QueryMonitorGroupListParam pa) {
        return monitorGroupService.queryMonitorGroupList(pa);
    }

    /**
     * @api {POST} /QueryMonitorGroupDetail 查询监测组详情
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorGroupDetail
     * @apiDescription 查询监测组详情
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiSuccess (返回结果) {Int} groupID 监测组ID
     * @apiSuccess (返回结果) {String} groupName 监测组名称
     * @apiSuccess (返回结果) {Boolean} enable 是否启用
     * @apiSuccess (返回结果) {String} [imagePath] 底图地址
     * @apiSuccess (返回结果) {String} [exValue] 拓展字段
     * @apiSuccess (返回结果) {Int} displayOrder 排序字段
     * @apiSuccess (返回结果) {Object[]} monitorItemList 监测项目列表
     * @apiSuccess (返回结果) {Int} monitorItemList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorItemList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} monitorItemList.monitorItemAlias 监测项目别称
     * @apiSuccess (返回结果) {Object[]} monitorPointList 监测点列表
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {String} monitorPointList.imageLocation 底图位置
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:DescribeBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:DescribeBaseMonitorGroup")
    @PostMapping(value = "/QueryMonitorGroupDetail", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorGroupDetail(@RequestBody @Validated QueryMonitorGroupDetailParam pa) {
        return monitorGroupService.queryMonitorGroupDetail(pa.getTbMonitorGroup());
    }

    /**
     * @api {POST} /QueryMonitorGroupItemNameList 根据监测项目名称获取监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorGroupItemNameList
     * @apiDescription 返回所有组内含该(模糊匹配名称)监测项目的监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Object[]} data 数据列表
     * @apiSuccess (返回结果) {Int} data.groupID 监测组ID
     * @apiSuccess (返回结果) {String} data.groupName 监测组名称
     * @apiSuccess (返回结果) {Boolean} data.enable 是否启用
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorGroup")
    @PostMapping(value = "/QueryMonitorGroupItemNameList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryMonitorGroupItemNameList(@RequestBody @Validated QueryMonitorGroupItemNameListParam pa) {
        return monitorGroupService.queryMonitorGroupItemNameList(pa);
    }

    /**
     * @api {POST} /QueryProjectGroupInfoList 根据工程ID获取对应监测组信息
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryProjectGroupInfoList
     * @apiDescription 根据工程ID获取工程下监测组别、监测组、监测点、监测点所属传感器信息
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Object[]} data 监测组信息
     * @apiSuccess (返回结果) {Int} data.monitorGroupParentID 监测组别ID
     * @apiSuccess (返回结果) {String} data.monitorGroupParentName 监测组名称
     * @apiSuccess (返回结果) {Boolean} data.monitorGroupParentEnable 监测组别是否启用
     * @apiSuccess (返回结果) {Object[]} data.monitorGroupDataList 监测组数据列表
     * @apiSuccess (返回结果) {Int} data.monitorGroupDataList.monitorGroupID 监测组ID
     * @apiSuccess (返回结果) {String} data.monitorGroupDataList.monitorGroupName 监测组名称
     * @apiSuccess (返回结果) {Boolean} data.monitorGroupDataList.monitorGroupEnable 监测组是否启用
     * @apiSuccess (返回结果) {Object[]} data.monitorGroupDataList.monitorPointDataList 监测点信息
     * @apiSuccess (返回结果) {Int} data.monitorGroupDataList.monitorPointDataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} data.monitorGroupDataList.monitorPointDataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Boolean} data.monitorGroupDataList.monitorPointDataList.monitorPointEnable 监测点是否启用
     * @apiSuccess (返回结果) {Boolean} data.monitorGroupDataList.monitorPointDataList.multiSensor 监测点允许关联多传感器标识
     * @apiSuccess (返回结果) {Int} data.monitorGroupDataList.monitorPointDataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} data.monitorGroupDataList.monitorPointDataList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Object[]} data.monitorGroupDataList.monitorPointDataList.sensorDataList 传感器信息
     * @apiSuccess (返回结果) {Int} data.monitorGroupDataList.monitorPointDataList.sensorDataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} data.monitorGroupDataList.monitorPointDataList.sensorDataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} data.monitorGroupDataList.monitorPointDataList.sensorDataList.sensorAlias 传感器别称
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorGroup
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorGroup")
    @PostMapping(value = "/QueryProjectGroupInfoList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryProjectGroupInfoList(@RequestBody @Validated QueryProjectGroupInfoParam pa) {
        return monitorGroupService.queryProjectGroupInfoList(pa);
    }
}
