package cn.shmedo.monitor.monibotbaseapi.controller;


import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
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

    /**
     * @api {POST} /UpdateMonitorClass 编辑监测类别配置
     * @apiVersion 1.0.0
     * @apiGroup 监测类别模块
     * @apiName UpdateMonitorClass
     * @apiDescription 编辑监测类别配置, 比如配置工程下的监测类别的密度配置
     * @apiParam (请求参数) {Int} projectID 工程项目ID
     * @apiParam (请求参数) {Int} monitorClass 监测类别(0:环境监测 1:安全监测 2:工情监测 3:防洪调度指挥监测 4:视频监测)
     * @apiParam (请求参数) {Boolean} enable 是否启用
     * @apiParam (请求参数) {String} density 密度,例如(30分钟:30m  1小时:1h  2小时:2h)
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:UpdateMonitorClass
     */
//    @Permission(permissionName = "mdmbase:UpdateMonitorClass")
    @PostMapping(value = "/UpdateMonitorClass", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updateMonitorClass(@RequestBody @Validated Object request) {
        return ResultWrapper.successWithNothing();
    }

}
