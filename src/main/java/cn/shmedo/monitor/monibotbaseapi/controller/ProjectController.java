package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-21 10:39
 **/
@RestController
public class ProjectController {
    /**
     * @api {POST} /AddProject 新增项目
     * @apiVersion 1.0.0
     * @apiGroup 项目模块
     * @apiName SetDeviceModelData
     * @apiDescription 新增项目
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiSuccess (响应结果) {String} none  无
     * @apiSampleRequest off
     * @apiPermission xx权限:
     */
    @RequestMapping(value = "AddProject", method = RequestMethod.POST, produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addProject() {
        return null;
    }
}
