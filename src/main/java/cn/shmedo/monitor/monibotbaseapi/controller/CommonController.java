package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.PermissionScope;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.GetLocationParam;
import cn.shmedo.monitor.monibotbaseapi.service.RegionAreaService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class CommonController {

    private final FileConfig fileConfig;

    private final RegionAreaService regionAreaService;

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
     * @apiParam (请求体) {Int} code 地区编号
     * @apiSuccess (返回结果) {String} name 地区名称
     * @apiSuccess (返回结果) {Double} lat 纬度
     * @apiSuccess (返回结果) {Double} lon 经度
     * @apiSampleRequest off
     * @apiPermission 登录权限
     */
    @Permission(permissionScope = PermissionScope.LOGGED)
    @RequestMapping(value = "/GetLocation", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object GetLocation(@RequestBody @Validated GetLocationParam params) {
        return ResultWrapper.success(regionAreaService.getLocationById(params));
    }
}
