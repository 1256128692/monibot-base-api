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
     * @api {GET} /ApiVersion 获取服务版本
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

    /**
     * @api {POST} /GetLocation 根据地区编号获取经纬度
     * @apiDescription 根据地区编号获取经纬度
     * @apiVersion 1.0.0
     * @apiGroup 通用模块
     * @apiName GetLocation
     * @apiParam (请求体) {String} code 地区编号
     * @apiSuccess (返回结果) {String} name 地区名称
     * @apiSuccess (返回结果) {Double} lat 经度
     * @apiSuccess (返回结果) {Double} lon 纬度
     * @apiSampleRequest off
     * @apiPermission 公共权限
     */
    @RequestMapping(value = "/GetLocation", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object GetLocation() {
        return null;
    }
}
