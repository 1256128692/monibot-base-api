package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    private FileConfig fileConfig;

    @Autowired
    public CommonController(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    /**
     * @api {get} /ApiVersion 获取服务版本
     * @apiDescription 获取服务版本信息
     * @apiVersion 1.0.0
     * @apiGroup 通用模块
     * @apiName ApiVersion
     * @apiSuccess (返回结果) {String} apiVersion 服务版本
     * @apiSampleRequest off
     * @apiPermission 公共权限
     */
    @RequestMapping(value = "/ApiVersion", method = RequestMethod.GET, produces = CommonVariable.JSON)
    public Object apiVersion() {
        return fileConfig.getApiVersion();
    }
}
