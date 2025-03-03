package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.QueryMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.UpdateWtMonitorClassParam;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorClassService;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * rjx
 */
@RestController
@AllArgsConstructor
public class MonitorClassController {

    private final MonitorClassService monitorClassService;

    /**
     * @api {POST} /UpdateWtMonitorClass 编辑监测类别配置
     * @apiVersion 1.0.0
     * @apiGroup 监测类别模块
     * @apiName UpdateWtMonitorClass
     * @apiDescription 编辑监测类别配置, 比如配置工程下的监测类别的密度配置
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {Int} monitorClass 监测类别(0:环境监测 1:安全监测 2:工情监测 3:防洪调度指挥监测 4:视频监测)
     * @apiParam (请求参数) {Int[]} monitorItemIDList 监测项目ID列表
     * @apiParam (请求参数) {Boolean} enable 是否启用
     * @apiParam (请求参数) {String} density 密度,例如(30分钟:30m  1小时:1h  2小时:2h 4小时:4h 全部:all)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateMonitorClass
     */
    @Permission(permissionName = "mdmbase:UpdateMonitorClass")
    @PostMapping(value = "/UpdateWtMonitorClass", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateWtMonitorClass(@RequestBody @Validated UpdateWtMonitorClassParam request) {
        monitorClassService.updateWtMonitorClass(request);
        return ResultWrapper.successWithNothing();
    }


    /**
     * @api {POST} /QueryMonitorClassList 查询工程的监测类别列表
     * @apiVersion 1.0.0
     * @apiGroup 监测类别模块
     * @apiName QueryMonitorClassList
     * @apiDescription 编辑监测类别配置, 比如配置工程下的监测类别的密度配置
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {Boolean} [enable] 是否启用
     * @apiSuccess (返回结果) {Object} data   结果数据
     * @apiSuccess (返回结果) {Int} data.projectID 工程项目ID
     * @apiSuccess (返回结果) {Int} data.monitorClass 监测类别(0:环境监测 1:安全监测 2:工情监测 3:防洪调度指挥监测 4:视频监测)
     * @apiSuccess (返回结果) {Boolean} data.enable 是否启用
     * @apiSuccess (返回结果) {String} data.density 密度
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:ListBaseMonitorClass
     */
    @Permission(permissionName = "mdmbase:ListBaseMonitorClass")
    @PostMapping(value = "/QueryMonitorClassList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object QueryMonitorClassList(@RequestBody @Validated QueryMonitorClassParam request) {
        return monitorClassService.queryMonitorClassList(request);
    }

}
