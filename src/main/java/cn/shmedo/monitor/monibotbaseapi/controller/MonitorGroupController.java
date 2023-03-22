package cn.shmedo.monitor.monibotbaseapi.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorGroupController {
    /**
     * @api {POST} /AddMonitorGroup 新建监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName AddMonitorGroup
     * @apiDescription 新建监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} [parentID] 父监测组ID
     * @apiParam (请求体) {String} name 监测组名称
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    public void addMonitorGroup(){
    }

    /**
     * @api {POST} /UpdateMonitorGroup 修改监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName UpdateMonitorGroup
     * @apiDescription 修改监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiParam (请求体) {String} name 监测组名称
     * @apiParam (请求体) {Bool} enable 是否启用
     * @apiParam (请求体) {Int[]} [monitorItemIDList] 监测项目ID列表
     * @apiParam (请求体) {Int[]} [monitorPointIDList] 监测点ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    public void updateMonitorMainGroup(){
    }

    /**
     * @api {POST} /UploadMonitorGroupImage 上传监测组底图
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName UploadMonitorGroupImage
     * @apiDescription 修改监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiParam (请求体) {String} fileName 文件名称
     * @apiParam (请求体) {String} filePrefix 文件后缀
     * @apiParam (请求体) {String} fileContent 文件base64内容
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    public void uploadMonitorGroupImage(){
    }

    /**
     * @api {POST} /DeleteMonitorGroup 删除监测组
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName DeleteMonitorGroup
     * @apiDescription 删除监测组
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int[]} groupIDList 监测组ID列表
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     * */
    public Object deleteMonitorGroup(){
        return null;
    }

    /**
     * @api {POST} /ConfigMonitorPointImageLocation 配置监测点底图定位
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName ConfigMonitorPointImageLocation
     * @apiDescription 配置监测点底图定位
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {Int} groupID 监测组ID
     * @apiParam (请求体) {Object[]} pointLocationList 监测点位置列表
     * @apiParam (请求体) {Int} pointLocationList.pointID 监测点ID
     * @apiParam (请求体) {String} pointLocationList.location 监测点位置
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     * */
    public Object configMonitorPointImageLocation(){
        return null;
    }

    /**
     * @api {POST} /QueryMonitorGroupList 分页查询监测组列表
     * @apiVersion 1.0.0
     * @apiGroup 监测组模块
     * @apiName QueryMonitorGroupList
     * @apiDescription 分页查询监测组列表
     * @apiParam (请求体) {Int} projectID 工程项目ID
     * @apiParam (请求体) {String} [name] 监测组名称
     * @apiParam (请求体) {Int} [monitorItemID] 监测项目ID
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总页数
     * @apiSuccess (返回结果) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Object[]} data 数据列表
     * @apiSuccess (返回结果) {Int} data.groupID 监测组ID
     * @apiSuccess (返回结果) {String} data.groupName 监测组名称
     * @apiSuccess (返回结果) {Boolean} data.enable 是否启用
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
     * @apiPermission 项目权限
     * */
    public Object queryMonitorGroupList(){
        return null;
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
     * @apiPermission 项目权限
     */
    public Object queryMonitorGroupDetail(){
        return null;
    }
}
