package cn.shmedo.monitor.monibotbaseapi.controller.wt;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.report.WtQueryReportParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 10:29
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WtReportController {
    private final ITbReportService tbReportService;

    /**
     * @api {POST} /QueryReport 水务局简报
     * @apiVersion 1.0.0
     * @apiGroup 监测简报模块
     * @apiName QueryReport
     * @apiDescription 水务局简报
     * @apiParam (请求参数) {Int} companyID 公司ID
     * @apiParam (请求参数) {Int} reportType 简报类型 0自定义 1周报 2月报 4年报
     * @apiParam (请求参数) {DateTime} startTime 开始时间
     * @apiParam (请求参数) {DateTime} endTime 结束时间
     * @apiParam (请求参数) {Int[]} [projectIDList] 工程概述ID list
     * @apiSuccess (返回结果) {DateTime} startTime 时间范围开始时间
     * @apiSuccess (返回结果) {DateTime} endTime 时间范围结束时间
     * @apiSuccess (返回结果) {String} period 期数
     * @apiSuccess (返回结果) {String} companyName 编制单位名称
     * @apiSuccess (返回结果) {Int} total 监测点总计
     * @apiSuccess (返回结果) {Int} monitorTypeClassSize 监测类型的类别数量(三类数据监测点)
     * @apiSuccess (返回结果) {String[]} monitorTypeClassList 监测类型的类别名称list
     * @apiSuccess (返回结果) {Object[]} dataList 详情数据
     * @apiSuccess (返回结果) {String} dataList.monitorTypeClass 监测类型的类别
     * @apiSuccess (返回结果) {Int} dataList.total 监测点总计
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeSize 监测类型名称数量
     * @apiSuccess (返回结果) {String[]} dataList.monitorTypeList 监测类型名称list
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeCountList 统计数据list
     * @apiSuccess (返回结果) {String} dataList.monitorTypeCountList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.total 监测点总计
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.normal 正常监测点总计
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.abnormal 异常监测点总计
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.noData 无数据监测点总计
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.rate 正常率
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeCountList.warnCountList 报警统计数据
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.warnCountList.warnLevel 报警等级
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeCountList.warnCountList.total 报警点位数量
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeDetailList 详情数据list
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeDetailList.monitorItemList 点位详情数据
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeDetailList.monitorItemList.total 点位总计
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeDetailList.monitorItemList.noData 无数据总计
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeDetailList.monitorItemList.warnCountList 报警统计数据
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeDetailList.monitorItemList.warnCountList.warnLevel 报警等级
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.warnCountList.warnName 报警名称
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeDetailList.monitorItemList.warnCountList.total 报警总计
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeDetailList.monitorItemList.formList 表格数据
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.monitorPointName 测点名称
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.monitorItemName 监测项目名称
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.projectTypeName 工程类型名称
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.projectName 工程名称
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.areaName 行政区划名称
     * @apiSuccess (返回结果) {DateTime} dataList.monitorTypeDetailList.monitorItemList.formList.time 采集时间
     * @apiSuccess (返回结果) {Object[]} dataList.monitorTypeDetailList.monitorItemList.formList.fieldDataList 附加列list
     * @apiSuccess (返回结果) {String} dataList.monitorTypeDetailList.monitorItemList.formList.fieldDataList.key 列名
     * @apiSuccess (返回结果) {Int} dataList.monitorTypeDetailList.monitorItemList.formList.fieldDataList.value 值
     * @apiSuccess (返回结果) {Object[]} [projectDataList] 工程概述list
     * @apiSuccess (返回结果) {String} projectDataList.projectName 工程名称
     * @apiSuccess (返回结果) {Int} projectDataList.total 点位总计
     * @apiSuccess (返回结果) {Int} projectDataList.monitorTypeSize 监测类型名称数量
     * @apiSuccess (返回结果) {Object[]} projectDataList.monitorTypeList 监测类型名称list
     * @apiSuccess (返回结果) {Object[]} projectDataList.monitorTypeCountList 监测数据list
     * @apiSuccess (返回结果) {String} projectDataList.monitorTypeCountList.monitorTypeName 监测类型名称
     * @apiSuccess (返回结果) {Int} projectDataList.monitorTypeCountList.total 点位总计
     * @apiSuccess (返回结果) {Int} projectDataList.monitorTypeCountList.noData 无数据监测点总计
     * @apiSuccess (返回结果) {Object[]} projectDataList.monitorTypeCountList.warnCountList 监测类型报警统计数据list
     * @apiSuccess (返回结果) {Int} projectDataList.monitorTypeCountList.warnCountList.warnLevel 报警等级
     * @apiSuccess (返回结果) {String} projectDataList.monitorTypeCountList.warnCountList.warnName 报警名称
     * @apiSuccess (返回结果) {Int} projectDataList.monitorTypeCountList.warnCountList.total 报警总计
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
//    @Permission(permissionName = "")
    @PostMapping(value = "/QueryReport", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryReport(@Valid @RequestBody WtQueryReportParam param) {
        return tbReportService.queryReport(param);
    }
}
