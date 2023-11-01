package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.QueryMonitorItemPointListParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryDmDataPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryStDataParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryDmDataParam;
import cn.shmedo.monitor.monibotbaseapi.service.IThematicDataAnalysisService;
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
public class ThematicDataAnalysisController {
    private final IThematicDataAnalysisService thematicDataAnalysisService;

    /**
     * @apiIgnore
     * @api {POST} /QueryStGroupRealData 浸润线专题
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryStGroupRealData
     * @apiDescription 浸润线专题
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorGroupID 监测组ID
     * @apiParam (请求参数) {Int} [density] 密度 1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求参数) {String} group 监测点自定义配置信息group
     * @apiParam (请求参数) {String} key 监测点自定义配置信息key
     * @apiParam (请求参数) {String} upperName 浸润线规则警戒值的upperName
     * @apiParam (请求参数) {DateTime} [startTime] 查询时段开始时间
     * @apiParam (请求参数) {DateTime} [endTime] 查询时段结束时间
     * @apiSuccess (返回结果) {Int} monitorPointGroupID 监测组ID
     * @apiSuccess (返回结果) {String} monitorPointGroupName 监测组名称
     * @apiSuccess (返回结果) {String} groupImage 底图路径
     * @apiSuccess (返回结果) {String} groupConfig 监测组自定义配置,包含组内各监测点顺序等
     * @apiSuccess (返回结果) {Object} newData 最新浸润线数据
     * @apiSuccess (返回结果) {Double} newData.distance 库水位,坝前水位监测项目监测点数据或多个坝前水位监测项目监测点数据均值
     * @apiSuccess (返回结果) {Object[]} newData.dataList 监测点数据
     * @apiSuccess (返回结果) {Int} newData.dataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} newData.dataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {DateTime} newData.dataList.time 数据上行时间
     * @apiSuccess (返回结果) {Int} newData.dataList.distance 监测点绑定的传感器上行水位数据(该类监测点仅允许绑定单传感器)
     * @apiSuccess (返回结果) {Double} [newData.dataList.upperLimit] 警戒值,若为null时不显示警戒线
     * @apiSuccess (返回结果) {String} [newData.dataList.pointConfig] 监测点配置,包含管高、管宽、渲染样式等
     * @apiSuccess (返回结果) {Object[]} avgData 平均浸润线数据
     * @apiSuccess (返回结果) {DateTime} avgData.time 时间
     * @apiSuccess (返回结果) {Double} avgData.distance 库水位
     * @apiSuccess (返回结果) {Object[]} avgData.dataList 监测点数据
     * @apiSuccess (返回结果) {Int} avgData.dataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} avgData.dataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Int} avgData.dataList.distance 监测点绑定的传感器上行水位数据(该类监测点仅允许绑定单传感器)
     * @apiSuccess (返回结果) {String} [avgData.dataList.pointConfig] 监测点配置,包含管高、管宽、渲染样式等
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
//    @PostMapping(value = "/QueryStGroupRealData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    public Object queryStGroupRealData(@Valid @RequestBody QueryStDataParam param) {
//        return thematicDataAnalysisService.queryStGroupRealData(param);
//    }

    /**
     * @apiIgnore
     * @api {POST} /QueryStGroupRealDataPage 浸润线专题分页 TODO
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryStGroupRealDataPage
     * @apiDescription 浸润线专题分页
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiSuccess (返回结果) {String} [avgData.dataList.pointConfig] 监测点配置,包含管高、管宽、渲染样式等
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
//    @PostMapping(value = "/QueryStGroupRealDataPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    public Object queryStGroupRealDataPage(@Valid @RequestBody Object param) {
//        return null;
//    }

    /**
     * @apiIgnore
     * @api {POST} /QueryDmAnalysisData 内部变形专题
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryDmAnalysisData
     * @apiDescription 内部变形专题
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorPointID 监测点ID
     * @apiParam (请求参数) {Int} [fieldToken] 数据轴token,默认是id最小的fieldToken
     * @apiParam (请求参数) {Int} density 密度 0.全部;1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求参数) {String} group 传感器自定义配置信息group
     * @apiParam (请求参数) {String} key 传感器自定义配置信息key
     * @apiParam (请求参数) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求参数) {DateTime} endTime 查询时段结束时间
     * @apiSuccess (返回结果) {Int} monitorPointGroupID 监测组ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测组名称
     * @apiSuccess (返回结果) {Object[]} historyData 历史内部变形数据
     * @apiSuccess (返回结果) {DateTime} historyData.time 数据上行时间
     * @apiSuccess (返回结果) {Object[]} historyData.dataList 内部变形数据
     * @apiSuccess (返回结果) {Int} historyData.dataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} historyData.dataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} historyData.dataList.fieldToken 数据源token
     * @apiSuccess (返回结果) {Double} historyData.dataList.totalValue 累计位移
     * @apiSuccess (返回结果) {Double} historyData.dataList.segmentValue 阶段位移
     * @apiSuccess (返回结果) {String} historyData.dataList.config 传感器自定义配置,其中至少有传感器深度(m)的配置
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
//    @PostMapping(value = "/QueryDmAnalysisData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    public Object queryDmAnalysisData(@Valid @RequestBody QueryDmDataParam param) {
//        return thematicDataAnalysisService.queryDmAnalysisData(param);
//    }

    /**
     * @apiIgnore
     * @api {POST} /QueryDmAnalysisDataPage 内部变形专题分页
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryDmAnalysisData
     * @apiDescription 内部变形专题分页
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorPointID 监测点ID
     * @apiParam (请求参数) {Int} [fieldToken] 数据轴token,默认是id最小的fieldToken
     * @apiParam (请求参数) {Int} density 密度 0.全部;1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求参数) {String} group 传感器自定义配置信息group
     * @apiParam (请求参数) {String} key 传感器自定义配置信息key
     * @apiParam (请求参数) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求参数) {DateTime} endTime 查询时段结束时间
     * @apiParam (请求参数) {DateTime[]} [dataList] 数据列表中选中的时间
     * @apiSuccess (返回结果) {Int} monitorPointGroupID 监测组ID
     * @apiSuccess (返回结果) {String} monitorPointName 监测组名称
     * @apiSuccess (返回结果) {Object[]} historyData 历史内部变形数据
     * @apiSuccess (返回结果) {DateTime} historyData.time 数据上行时间
     * @apiSuccess (返回结果) {Object[]} historyData.dataList 内部变形数据
     * @apiSuccess (返回结果) {Int} historyData.dataList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} historyData.dataList.sensorName 传感器名称
     * @apiSuccess (返回结果) {String} historyData.dataList.fieldToken 数据源token
     * @apiSuccess (返回结果) {Double} historyData.dataList.totalValue 累计位移
     * @apiSuccess (返回结果) {Double} historyData.dataList.segmentValue 阶段位移
     * @apiSuccess (返回结果) {String} historyData.dataList.config 传感器自定义配置,其中至少有传感器深度(m)的配置
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
//    @Permission(permissionName = "")
//    @PostMapping(value = "/QueryDmAnalysisDataPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    public Object queryDmAnalysisDataPage(@Valid @RequestBody QueryDmDataPageParam param) {
//        return thematicDataAnalysisService.queryDmAnalysisDataPage(param);
//    }

    /**
     * @apiIgnore
     * @api {POST} /QueryDmPageDataList 内部变形专题数据列表
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryDmPageDataList
     * @apiDescription 内部变形专题查询用户可选的数据列表
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiParam (请求参数) {Int} monitorPointID 监测点ID
     * @apiParam (请求参数) {Int} [fieldToken] 数据轴token,默认是id最小的fieldToken
     * @apiParam (请求参数) {Int} density 密度 0.全部;1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求参数) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求参数) {DateTime} endTime 查询时段结束时间
     * @apiSuccess (返回结果) {DateTime[]} dataList 用户可选的数据列表
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    //    @Permission(permissionName = "")
//    @PostMapping(value = "/QueryDmPageDataList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
//    public Object queryDmPageDataList(@Valid @RequestBody QueryDmDataParam param) {
//        return thematicDataAnalysisService.queryDmPageDataList(param);
//    }

    /**
     * @api {POST} /QueryThematicMonitorPoint 查询专题分析监测点位
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryThematicMonitorPoint
     * @apiDescription 查询专题分析监测点位
     * @apiParam (请求参数) {Int} projectID 项目ID
     * @apiSuccess (返回结果) {Object[]} data 数据
     * @apiSuccess (返回结果) {Int} data.thematicType 主题类型枚举1.水雨情; 2.浸润线; 3.内部变形
     * @apiSuccess (返回结果) {Object[]} data.thematicDataList 主题类型数据
     * @apiSuccess (返回结果) {String} data.thematicDataList.monitorItemDesc 监测项目描述
     * @apiSuccess (返回结果) {Object[]} data.thematicDataList.dataList 监测点位数据
     * @apiSuccess (返回结果) {Int} data.thematicDataList.dataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} data.thematicDataList.dataList.monitorPointID 监测点位ID
     * @apiSuccess (返回结果) {String} data.thematicDataList.dataList.monitorPointName 监测点位名称
     * @apiSampleRequest off
     * @apiPermission 项目权限
     */
    //    @Permission(permissionName = "")
    @PostMapping(value = "/QueryThematicMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryThematicMonitorPoint(@Valid @RequestBody QueryMonitorItemPointListParam param) {
        return thematicDataAnalysisService.queryThematicMonitorPointByProjectID(param.getProjectID());
    }
}
