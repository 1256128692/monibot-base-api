package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoPresetPointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-29 16:16
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VideoPresetPointController {
    private final ITbVideoPresetPointService tbVideoPresetPointService;

    /**
     * @api {POST} /QueryPresetPointList 查询设备预置点列表
     * @apiDescription 查询设备预置点列表
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName QueryPresetPointList
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} deviceVideoID 视频设备ID
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.presetPointID 预置点ID
     * @apiSuccess (返回结果) {String} dataList.presetPointName 预置点名称
     * @apiSuccess (返回结果) {Int} dataList.presetPointIndex 预置点位置
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/QueryPresetPointList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryPresetPointList(@Valid @RequestBody Object param) {
        //
        return null;
    }

    /**
     * @api {POST} /AddPresetPoint 新增预置点
     * @apiDescription 新增预置点
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName AddPresetPoint
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} deviceVideoID 视频设备ID
     * @apiParam (请求体) {String} presetPointName 预置点名称
     * @apiParam (请求体) {Int} presetPointIndex 预置点位置
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/AddPresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addPresetPoint(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /UpdatePresetPoint 修改预置点
     * @apiDescription 修改预置点
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName UpdatePresetPoint
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} presetPointID 预置点ID
     * @apiParam (请求体) {String} presetPointName 预置点名称
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/UpdatePresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updatePresetPoint(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /DeletePresetPoint 批量删除预置点
     * @apiDescription 批量删除预置点
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName DeletePresetPoint
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int[]} presetPointIDList 预置点IDList
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:
     */
//    @Permission(permissionName = "mdmbase:")
    @PostMapping(value = "/DeletePresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deletePresetPoint(@Valid @RequestBody Object param) {
        //
        return ResultWrapper.successWithNothing();
    }
}