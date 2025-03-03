package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoPresetPoint;
import cn.shmedo.monitor.monibotbaseapi.service.ITbVideoPresetPointService;
import cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint.*;
import cn.shmedo.monitor.monibotbaseapi.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-29 16:16
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VideoPresetPointController {
    private final ITbVideoPresetPointService tbVideoPresetPointService;
    private final VideoService videoService;

    /**
     * @api {POST} /QueryPresetPointList 查询设备预置点列表
     * @apiDescription 查询设备预置点列表
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName QueryPresetPointList
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} videoDeviceID 视频设备ID
     * @apiParam (请求体) {Int} channelNo 通道号
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.presetPointID 预置点ID
     * @apiSuccess (返回结果) {String} dataList.presetPointName 预置点名称
     * @apiSuccess (返回结果) {Int} dataList.presetPointIndex 预置点位置
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:ListBasePresetPoint
     */
    @Permission(permissionName = "mdmbase:ListBasePresetPoint")
    @PostMapping(value = "/QueryPresetPointList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryPresetPointList(@Valid @RequestBody QueryPresetPointListParam param) {
        return ResultWrapper.success(this.tbVideoPresetPointService.list(new LambdaQueryWrapper<TbVideoPresetPoint>()
                        .select(TbVideoPresetPoint::getID, TbVideoPresetPoint::getPresetPointName, TbVideoPresetPoint::getPresetPointIndex)
                        .eq(TbVideoPresetPoint::getVideoDeviceID, param.getVideoDeviceID())).stream()
                .map(u -> Map.of("presetPointID", u.getID(), "presetPointName", u.getPresetPointName(), "presetPointIndex", u.getPresetPointIndex())).toList());
    }

    /**
     * @api {POST} /AddPresetPoint 新增预置点
     * @apiDescription 新增预置点
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName AddPresetPoint
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} videoDeviceID 视频设备ID
     * @apiParam (请求体) {Int} channelNo 通道号
     * @apiParam (请求体) {String} presetPointName 预置点名称
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:AddBasePresetPoint
     */
    @Permission(permissionName = "mdmbase:AddBasePresetPoint")
    @PostMapping(value = "/AddPresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addPresetPoint(@Valid @RequestBody AddPresetPointParam param) {
        return videoService.addPresetPoint(param);
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
     * @apiPermission 系统权限 mdmbase:UpdateBasePreset
     */
    @Permission(permissionName = "mdmbase:UpdateBasePreset")
    @PostMapping(value = "/UpdatePresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object updatePresetPoint(@Valid @RequestBody UpdatePresetPointParam param) {
        this.tbVideoPresetPointService.update(new LambdaUpdateWrapper<TbVideoPresetPoint>()
                .eq(TbVideoPresetPoint::getID, param.getPresetPointID())
                .set(TbVideoPresetPoint::getPresetPointName, param.getPresetPointName()));
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
     * @apiPermission 系统权限 mdmbase:DeleteBasePresetPoint
     */
    @Permission(permissionName = "mdmbase:DeleteBasePresetPoint")
    @PostMapping(value = "/DeletePresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object deletePresetPoint(@Valid @RequestBody DeletePresetPointParam param) {
        return videoService.deletePresetPoint(param.getTbPresetPointWithDeviceInfoList());
    }

    /**
     * @api {POST} /MovePresetPoint 移动到该预置点
     * @apiDescription 移动到该预置点
     * @apiVersion 1.0.0
     * @apiGroup 预置点模块
     * @apiName MovePresetPoint
     * @apiParam (请求体) {Int} companyID 公司ID
     * @apiParam (请求体) {Int} presetPointID 预置点ID
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 系统权限 mdmbase:MoveBasePresetPoint
     */
    @Permission(permissionName = "mdmbase:MoveBasePresetPoint")
    @PostMapping(value = "/MovePresetPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object movePresetPoint(@Valid @RequestBody MovePresetPointParam param) {
        return videoService.movePresetPoint(param.getPresetPointWithDeviceInfo());
    }
}