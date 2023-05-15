package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 17:41
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StResourceController {
    //wetting line was defined in the md-net project as SATURATURE,I followed

    /**
     * @api {POST} /QueryStGroupRealData 浸润线专题
     * @apiVersion 1.0.0
     * @apiGroup 浸润线专题模块
     * @apiName QueryStGroupRealData
     * @apiDescription 浸润线专题
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorGroupID 检测组ID
     * @apiParam (请求参数) {Int} density 密度 1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求参数) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求参数) {DateTime} endTime 查询时段结束时间
     * @apiSuccess (返回结果) {String} none TODO,返参需要等待产品回复
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
    @PostMapping(value = "/QueryStGroupRealData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryStGroupRealData(@Valid @RequestBody Object param) {
        return null;
    }
}
