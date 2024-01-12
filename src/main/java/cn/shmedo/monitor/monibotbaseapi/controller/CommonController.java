package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.PermissionScope;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.base.CommonVariable;
import cn.shmedo.monitor.monibotbaseapi.cache.DataUnitCache;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.GetLocationParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.region.ListAreaCodeByLocationNameParam;
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
    private final DataUnitCache dataUnitCache;

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
    public Object getLocation(@RequestBody @Validated GetLocationParam params) {
        return ResultWrapper.success(regionAreaService.getLocationById(params));
    }

    /**
     * @api {POST} /ListAreaCodeByLocationName 根据地区名称批量获取地区编号
     * @apiDescription 根据地区名称批量获取地区编号, 最多支持四级地区名称
     * @apiVersion 1.0.0
     * @apiGroup 通用模块
     * @apiName ListAreaCodeByLocationName
     * @apiParam (请求体) {String[]} locationNameList 地区名称列表,示例: 浙江,杭州,余杭,仓前街道
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {String} dataList.locationName 地区名称
     * @apiSuccess (返回结果) {String} dataList.code 地区编号,示例: "{\"province\":330000,\"city\":330100,\"area\":330110,\"town\":330110012}"
     * @apiSampleRequest off
     * @apiPermission 登录权限
     */
    @Permission(permissionScope = PermissionScope.LOGGED)
    @RequestMapping(value = "/ListAreaCodeByLocationName", method = RequestMethod.POST, produces = CommonVariable.JSON)
    public Object listAreaCodeByLocationName(@RequestBody @Validated ListAreaCodeByLocationNameParam param) {
        return ResultWrapper.success(regionAreaService.listAreaCodeByLocationName(param));
    }

    /**
     * @api {GET} /GetDataUnit 获取数据单位
     * @apiDescription 系统中的数据单位
     * @apiVersion 1.0.0
     * @apiGroup 通用模块
     * @apiName GetDataUnit
     * @apiSuccess (返回结果) {Object[]} list
     * @apiSuccess (返回结果) {Int} list.id
     * @apiSuccess (返回结果) {String} list.engUnit 单位英文名称
     * @apiSuccess (返回结果) {String} list.chnUnit 单位中文名称
     * @apiSuccess (返回结果) {String} list.unitClass 单位类别
     * @apiSuccess (返回结果) {String} list.unitDesc 单位描述
     * @apiSampleRequest off
     * @apiPermission 登录权限
     */
    @Permission(permissionScope = PermissionScope.LOGGED)
    @RequestMapping(value = "/GetDataUnit", method = RequestMethod.GET, produces = CommonVariable.JSON)
    public Object getDataUnit() {
        return DataUnitCache.dataUnits;
    }
}
