package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-20 16:28
 **/
@RestController
@AllArgsConstructor
public class MonitorTypeController {
    /**
     * @api {POST} /AddMonitorType 新增监测类型
     * @apiVersion 1.0.0
     * @apiGroup 监测类型模块
     * @apiName AddMonitorType
     * @apiDescription 新增监测类型
     * @apiParam (请求参数) {Int} [id] 输水建筑参数ID
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {String} code 构筑物代码(100)
     * @apiParam (请求参数) {String} name 输水建筑名称(50)
     * @apiParam (请求参数) {Double} designFlow 设计流量(m³/s)
     * @apiParam (请求参数) {String} structureType 输水道型式(50)
     * @apiParam (请求参数) {Double} sectionSize 断面尺寸(m)
     * @apiParam (请求参数) {Double} structureLength 输水道长度(m)
     * @apiParam (请求参数) {String} crossStructureType 交叉建筑物型式(50)
     * @apiParam (请求参数) {String} [exValue] 拓展字段(500)
     * @apiParam (请求参数) {Int} [displayOrder] 排序字段
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 xx
     */
//    @Permission(permissionName = "xx")
    @PostMapping(value = "/AddMonitorType", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addMonitorType(@RequestBody @Validated Object request) {

        return ResultWrapper.successWithNothing();
    }
}
