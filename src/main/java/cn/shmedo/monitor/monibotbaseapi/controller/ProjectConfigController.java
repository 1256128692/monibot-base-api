package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.*;
import cn.shmedo.monitor.monibotbaseapi.service.ITbProjectConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 17:00
 * @desc: 通用配置接口
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProjectConfigController {
    private final ITbProjectConfigService tbProjectConfigService;

    /**
     * @api {POST} /SetProjectConfig 设置自定义配置
     * @apiVersion 1.0.0
     * @apiGroup 自定义配置模块
     * @apiName SetProjectConfig
     * @apiDescription 设置额外配置
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} [configID] 配置ID,为空时认为是新增配置
     * @apiParam (请求参数) {String} group 分组
     * @apiParam (请求参数) {String} key 对应的key,最终入库的key将和对应级别的ID拼接成这样 key::ID
     * @apiParam (请求参数) {String} value 值
     * @apiParam (请求参数) {Int} [monitorGroupID] 监测点分组ID
     * @apiParam (请求参数) {Int} [monitorPointID] 监测点ID
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @PostMapping(value = "/SetProjectConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    @Permission(permissionName = "")
    public Object setProjectConfig(@Valid @RequestBody SetProjectConfigParam param) {
        tbProjectConfigService.setProjectConfig(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /BatchSetProjectConfig 批量自定义配置
     * @apiVersion 1.0.0
     * @apiGroup 自定义配置模块
     * @apiName BatchSetProjectConfig
     * @apiDescription 批量额外配置
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Object[]} dataList 对象
     * @apiParam (请求参数) {Int} dataList.projectID 项目ID
     * @apiParam (请求参数) {Int} [dataList.configID] 配置ID,为空时认为是新增配置
     * @apiParam (请求参数) {String} dataList.group 分组
     * @apiParam (请求参数) {String} dataList.key 对应的key
     * @apiParam (请求参数) {String} dataList.value 值
     * @apiParam (请求参数) {Int} [dataList.monitorGroupID] 监测点分组ID
     * @apiParam (请求参数) {Int} [dataList.monitorPointID] 监测点ID
     * @apiSuccess (返回结果) {String} none 空
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @PostMapping(value = "/BatchSetProjectConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    @Permission(permissionName = "")
    public Object batchSetProjectConfig(@Valid @RequestBody BatchSetProjectConfigParam param) {
        tbProjectConfigService.batchSetProjectConfig(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /ListProjectConfig 查询自定义配置
     * @apiVersion 1.0.0
     * @apiGroup 自定义配置模块
     * @apiName ListProjectConfig
     * @apiDescription 查询额外配置
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {String} group 分组
     * @apiParam (请求参数) {String} [key] key前缀,跟设置额外配置时入参key相同,如果要查看全部可填空或填”all“
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.configID 配置ID
     * @apiSuccess (返回结果) {Int} dataList.projectID 项目ID
     * @apiSuccess (返回结果) {Int} dataList.ID 配置对象ID
     * @apiSuccess (返回结果) {String} dataList.group 分组
     * @apiSuccess (返回结果) {String} dataList.key key
     * @apiSuccess (返回结果) {String} dataList.value value
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    @PostMapping(value = "/ListProjectConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    @Permission(permissionName = "")
    public Object listProjectConfig(@Valid @RequestBody ListProjectConfigParam param) {
        return tbProjectConfigService.listProjectConfig(param);
    }
}
