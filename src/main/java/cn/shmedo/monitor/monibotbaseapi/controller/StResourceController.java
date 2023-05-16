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
     * @apiSuccess (返回结果) {Int} monitorPointGroupID 监测组ID
     * @apiSuccess (返回结果) {String} monitorPointGroupName 监测组名称
     * @apiSuccess (返回结果) {String} groupImage 底图路径
     * @apiSuccess (返回结果) {String} groupConfig 监测组自定义配置
     * @apiSuccess (返回结果) {Object} newData 最新浸润线数据
     * @apiSuccess (返回结果) {String} newData.distance 库水位,坝前水位监测项目监测点数据或多个坝前水位监测项目监测点数据均值
     * @apiSuccess (返回结果) {Object[]} newData.dataList 监测点数据
     * @apiSuccess (返回结果) {} [newData.dataList.] 监
     * @apiSuccess (返回结果) {Int} newData.dataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} newData.dataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {DateTime} newData.dataList.time 数据上行时间
     * @apiSuccess (返回结果) {Int} newData.dataList.distance 监测点绑定的传感器上行水位数据(该类监测点仅允许绑定单传感器)
     * @apiSuccess (返回结果) {Int} newData.dataList.upperLimit 警戒值
     * @apiSuccess (返回结果) {String} [newData.dataList.pointConfig] 监测点配置,包含管高、管宽、渲染样式、警戒线等
     * @apiSuccess (返回结果) {Object[]} avgData 平均浸润线数据
     * @apiSuccess (返回结果) {DateTime} avgData.time 时间
     * @apiSuccess (返回结果) {Int} avgData.distance 库水位
     * @apiSuccess (返回结果) {Object[]} avgData.dataList 监测点数据
     * @apiSuccess (返回结果) {Int} avgData.dataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} avgData.dataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} avgData.dataList.distance 监测点绑定的传感器上行水位数据(该类监测点仅允许绑定单传感器)
     * @apiSuccess (返回结果) {String} [avgData.dataList.pointConfig] 监测点配置,包含管高、管宽、渲染样式、警戒线等
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
    @PostMapping(value = "/QueryStGroupRealData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryStGroupRealData(@Valid @RequestBody Object param) {
        return null;
    }
}
