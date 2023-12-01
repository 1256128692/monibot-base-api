package cn.shmedo.monitor.monibotbaseapi.controller;

import cn.shmedo.iot.entity.annotations.Permission;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.core.annotation.ResourceSymbol;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint.QueryMonitorItemPointListParam;
import cn.shmedo.monitor.monibotbaseapi.service.IThematicDataAnalysisService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorGroupID 监测组ID
     * @apiParam (请求体) {Int} [density] 密度 1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求体) {String} group 监测点自定义配置信息group
     * @apiParam (请求体) {String} key 监测点自定义配置信息key
     * @apiParam (请求体) {String} upperName 浸润线规则警戒值的upperName
     * @apiParam (请求体) {DateTime} [startTime] 查询时段开始时间
     * @apiParam (请求体) {DateTime} [endTime] 查询时段结束时间
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
     * @api {POST} /QueryStGroupRealDataPage 浸润线专题分页(未完成)
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryStGroupRealDataPage
     * @apiDescription 浸润线专题分页
     * @apiParam (请求体) {Int} projectID 项目ID
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
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {Int} [fieldToken] 数据轴token,默认是id最小的fieldToken
     * @apiParam (请求体) {Int} density 密度 0.全部;1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求体) {String} group 传感器自定义配置信息group
     * @apiParam (请求体) {String} key 传感器自定义配置信息key
     * @apiParam (请求体) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求体) {DateTime} endTime 查询时段结束时间
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
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {Int} [fieldToken] 数据轴token,默认是id最小的fieldToken
     * @apiParam (请求体) {Int} density 密度 0.全部;1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求体) {String} group 传感器自定义配置信息group
     * @apiParam (请求体) {String} key 传感器自定义配置信息key
     * @apiParam (请求体) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求体) {DateTime} endTime 查询时段结束时间
     * @apiParam (请求体) {DateTime[]} [dataList] 数据列表中选中的时间
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
     * @apiParam (请求体) {Int} projectID 项目ID
     * @apiParam (请求体) {Int} monitorPointID 监测点ID
     * @apiParam (请求体) {Int} [fieldToken] 数据轴token,默认是id最小的fieldToken
     * @apiParam (请求体) {Int} density 密度 0.全部;1.日平均; 2.月平均; 3.年平均
     * @apiParam (请求体) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求体) {DateTime} endTime 查询时段结束时间
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
     * @apiParam (请求体) {Int} projectID 项目ID
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
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryThematicMonitorPoint", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryThematicMonitorPoint(@Valid @RequestBody QueryMonitorItemPointListParam param) {
        return thematicDataAnalysisService.queryThematicMonitorPointByProjectID(param.getProjectID());
    }

    /**
     * @api {POST} /QueryThematicGroupPointList 查询专题分析监测点组和监测点数据
     * @apiDescription 查询专题分析监测点组和监测点数据
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryThematicGroupPointList
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} [thematicType] 专题类型 1.浸润线 默认1.浸润线
     * @apiParam (请求体) {Int[]} [monitorGroupIDList] 监测点组IDList
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorGroupID 监测点组ID
     * @apiSuccess (返回结果) {String} dataList.monitorGroupName 监测点组名称
     * @apiSuccess (返回结果) {Boolean} dataList.monitorGroupEnable 监测点组是否启用
     * @apiSuccess (返回结果) {Object[]} dataList.monitorPointDataList 监测点数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataList.monitorPointDataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Boolean} dataList.monitorPointDataList.monitorPointEnable 监测点是否启用
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} dataList.monitorPointDataList.sensorID 传感器ID
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryThematicGroupPointList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryThematicGroupPointList(@Valid @RequestBody QueryThematicGroupPointListParam param) {
        return thematicDataAnalysisService.queryThematicGroupPointList(param);
    }

    /**
     * @api {POST} /QueryTransverseList 浸润线分析-横剖面(不分页)
     * @apiDescription 浸润线分析-横剖面(不分页)
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryTransverseList
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorGroupID 监测点组ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点IDList
     * @apiParam (请求体) {Int} queryDataType 属性 1.管内水位高程 2.空管距离
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {Int} statisticalMethod 统计方式 1.最新一条 2.平均值 3.阶段累计 4.阶段变化
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiParam (请求体) {Object} [datumPoint] 基准点配置
     * @apiParam (请求体) {Int} datumPoint.monitorPointID 基准监测点ID
     * @apiParam (请求体) {Double} datumPoint.upper 向上波动(米)
     * @apiParam (请求体) {Double} datumPoint.lower 向下波动(米)
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.time 时间
     * @apiSuccess (返回结果) {Object} [dataList.datumPointData] 基准点数据,入参有datumPoint基准点配置时才有该项
     * @apiSuccess (返回结果) {Int} dataList.datumPointData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataList.datumPointData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Double} dataList.datumPointData.value 值
     * @apiSuccess (返回结果) {Double} dataList.datumPointData.upper 波动区间上限
     * @apiSuccess (返回结果) {Double} dataList.datumPointData.lower 波动区间下限
     * @apiSuccess (返回结果) {Object[]} dataList.monitorPointList 监测点数据列表
     * @apiSuccess (返回结果) {Int} dataList.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataList.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Double} dataList.monitorPointList.value 值
     * @apiSuccess (返回结果) {Double} [dataList.monitorPointList.abnormalValue] 异常值,入参有datumPoint基准点配置且当前值为异常值时才有该项;<br>为负值时表示该点值超出波动下限,为正值时表示该点超出波动上限
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryTransverseList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryTransverseList(@Valid @RequestBody QueryTransverseListParam param) {
        return thematicDataAnalysisService.queryTransverseList(param);
    }

    /**
     * @api {POST} /QueryTransversePage 浸润线分析-横剖面(分页)
     * @apiDescription 浸润线分析-横剖面(分页)
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryTransversePage
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorGroupID 监测点组ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点IDList
     * @apiParam (请求体) {Int} queryDataType 属性 1.管内水位高程 2.空管距离
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {Int} statisticalMethod 统计方式 1.最新一条 2.平均值 3.阶段累计 4.阶段变化
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiParam (请求体) {Object} [datumPoint] 基准点配置
     * @apiParam (请求体) {Int} datumPoint.monitorPointID 基准监测点ID
     * @apiParam (请求体) {Double} datumPoint.upper 向上波动(米)
     * @apiParam (请求体) {Double} datumPoint.lower 向下波动(米)
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {DateTime} currentPageData.time 时间
     * @apiSuccess (返回结果) {Object} [currentPageData.datumPointData] 基准点数据,入参有datumPoint基准点配置时才有该项
     * @apiSuccess (返回结果) {Int} currentPageData.datumPointData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.datumPointData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Double} currentPageData.datumPointData.value 值
     * @apiSuccess (返回结果) {Double} currentPageData.datumPointData.upper 波动区间上限
     * @apiSuccess (返回结果) {Double} currentPageData.datumPointData.lower 波动区间下限
     * @apiSuccess (返回结果) {Object[]} currentPageData.monitorPointList 监测点数据列表
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Double} currentPageData.monitorPointList.value 值
     * @apiSuccess (返回结果) {Double} [currentPageData.monitorPointList.abnormalValue] 异常值,入参有datumPoint基准点配置且当前值为异常值时才有该项;<br>为负值时表示该点值超出波动下限,为正值时表示该点超出波动上限
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryTransversePage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryTransversePage(@Valid @RequestBody QueryTransversePageParam param) {
        return ResultWrapper.success(PageUtil.page(thematicDataAnalysisService.queryTransverseList(param), param.getPageSize(), param.getCurrentPage()));
    }

    /**
     * @api {POST} /QueryWetLineConfig 查询浸润线纵剖面配置
     * @apiDescription 查询浸润线纵剖面配置
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryWetLineConfig
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorGroupID 监测点组ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点ID列表
     * @apiSuccess (返回结果) {Int} monitorGroupID 监测点组ID
     * @apiSuccess (返回结果) {String} monitorGroupName 监测点组名称
     * @apiSuccess (返回结果) {Boolean} monitorGroupEnable 监测点组是否启用
     * @apiSuccess (返回结果) {String} monitorGroupImagePath 监测点组底图配置
     * @apiSuccess (返回结果) {Object[]} monitorGroupConfigList 监测点组自定义配置列表
     * @apiSuccess (返回结果) {Int} monitorGroupConfigList.configID 配置ID
     * @apiSuccess (返回结果) {String} monitorGroupConfigList.group 分组
     * @apiSuccess (返回结果) {String} monitorGroupConfigList.key key
     * @apiSuccess (返回结果) {String} monitorGroupConfigList.value value
     * @apiSuccess (返回结果) {Object[]} monitorPointList 监测点数据列表
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Boolean} monitorPointList.monitorPointEnable 监测是否启用
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorItemID 监测项目ID
     * @apiSuccess (返回结果) {Int} monitorPointList.sensorID 传感器ID
     * @apiSuccess (返回结果) {String} monitorPointList.sensorExValues 传感器拓展信息(e.g. {"安装高程":200})
     * @apiSuccess (返回结果) {Double} monitorPointList.emptyPipeDistance 空管距离
     * @apiSuccess (返回结果) {Double} monitorPointList.levelElevation 水位高程
     * @apiSuccess (返回结果) {Double} monitorPointList.nozzleElevation 管口高程
     * @apiSuccess (返回结果) {Object[]} monitorPointList.monitorPointConfigList 监测点自定义配置列表
     * @apiSuccess (返回结果) {Int} monitorPointList.monitorPointConfigList.configID 配置ID
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointConfigList.group 分组
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointConfigList.key key
     * @apiSuccess (返回结果) {String} monitorPointList.monitorPointConfigList.value value
     * @apiSuccess (返回结果) {Object[]} monitorPointList.eigenValueList 监测点关联特征值列表
     * @apiSuccess (返回结果) {Int} monitorPointList.eigenValueList.eigenValueID 特征值ID
     * @apiSuccess (返回结果) {String} monitorPointList.eigenValueList.eigenValueName 特征值名称
     * @apiSuccess (返回结果) {Double} monitorPointList.eigenValueList.eigenValue 特征值
     * @apiSuccess (返回结果) {String} monitorPointList.eigenValueList.chnUnit 中文单位
     * @apiSuccess (返回结果) {String} monitorPointList.eigenValueList.engUnit 英文单位
     * @apiSuccess (返回结果) {Object[]} monitorPointList.paramFieldList 参数列表
     * @apiSuccess (返回结果) {Int} monitorPointList.paramFieldList.id 参数ID
     * @apiSuccess (返回结果) {Int} monitorPointList.paramFieldList.subjectID 参数主体ID
     * @apiSuccess (返回结果) {Int} monitorPointList.paramFieldList.subjectType 参数主体类型
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.dataType 参数数据类型
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.token 参数标识
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.name 参数名称
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.paValue 参数默认值
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.paDesc 参数描述
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.chnUnit 参数中文单位
     * @apiSuccess (返回结果) {String} monitorPointList.paramFieldList.engUnit 参数英文单位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    @PostMapping(value = "/QueryWetLineConfig", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryWetLineConfig(@Valid @RequestBody QueryWetLineConfigParam param) {
        return thematicDataAnalysisService.queryWetLineConfig(param);
    }

    /**
     * @api {POST} /QueryLongitudinalList 浸润线分析-纵剖面(不分页)
     * @apiDescription 浸润线分析-纵剖面(不分页)
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryLongitudinalList
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorGroupID 监测点组ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点IDList
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {Int} statisticalMethod 统计方式 1.最新一条 2.平均值 3.阶段累计 4.阶段变化
     * @apiParam (请求体) {Object} [cutoffWallConfig] 防渗墙配置
     * @apiParam (请求体) {Double} cutoffWallConfig.value 阈值
     * @apiParam (请求体) {Int[]} cutoffWallConfig.monitorPointIDList 防渗墙两侧监测点IDList<br>防渗墙两侧监测点monitorPointIDList必定是监测点组下的监测点。如果不是,否则说明该监测点已经从监测点组中删除,此时需要重新配置防渗效果监测的数据
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.time 时间
     * @apiSuccess (返回结果) {Object[]} dataList.pipeDataList 管道数据列表
     * @apiSuccess (返回结果) {Int} dataList.pipeDataList.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} dataList.pipeDataList.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Double} dataList.pipeDataList.emptyPipeDistance 空管距离(m)
     * @apiSuccess (返回结果) {Object} dataList.pipeDataList.levelElevation 水位高程
     * @apiSuccess (返回结果) {Double} dataList.pipeDataList.levelElevation.value 值(m)
     * @apiSuccess (返回结果) {Double} [dataList.pipeDataList.levelElevation.osmoticValue] '渗压管高程差'减去'阈值'的结果，防渗墙两侧监测点配置有'阈值'且该值小于等于0时才有该项<br>为0时表示两侧渗压管水位高程差等于阈值,为负值时表示两侧渗压管水位高程差小于阈值
     * @apiSuccess (返回结果) {Object} [dataList.pipeDataList.levelElevation.eigenValue] 特征值异常数据 配置有'特征值'时且当前值超出特征值时才有该项,优先选取最大的'特征值'
     * @apiSuccess (返回结果) {Int} dataList.pipeDataList.levelElevation.eigenValue.eigenValueID 特征值ID
     * @apiSuccess (返回结果) {String} dataList.pipeDataList.levelElevation.eigenValue.eigenValueName 特征值名称
     * @apiSuccess (返回结果) {String} dataList.pipeDataList.levelElevation.eigenValue.chnUnit 特征值中文单位
     * @apiSuccess (返回结果) {String} dataList.pipeDataList.levelElevation.eigenValue.engUnit 特征值英文单位
     * @apiSuccess (返回结果) {Double} dataList.pipeDataList.levelElevation.eigenValue.abnormalValue 特征值异常值 为正值时表示水位高程超过该特征值
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryLongitudinalList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryLongitudinalList(@Valid @RequestBody QueryLongitudinalListParam param) {
        return thematicDataAnalysisService.queryLongitudinalList(param);
    }

    /**
     * @api {POST} /QueryLongitudinalPage 浸润线分析-纵剖面(分页)
     * @apiDescription 浸润线分析-纵剖面(分页)
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryLongitudinalPage
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorGroupID 监测点组ID
     * @apiParam (请求体) {Int[]} monitorPointIDList 监测点IDList
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {Int} statisticalMethod 统计方式 1.最新一条 2.平均值 3.阶段累计 4.阶段变化
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiParam (请求体) {Object} [cutoffWallConfig] 防渗墙配置
     * @apiParam (请求体) {Double} cutoffWallConfig.value 阈值
     * @apiParam (请求体) {Int[]} cutoffWallConfig.monitorPointIDList 防渗墙两侧监测点IDList<br>防渗墙两侧监测点monitorPointIDList必定是监测点组下的监测点。如果不是,否则说明该监测点已经从监测点组中删除,此时需要重新配置防渗效果监测的数据
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {DateTime} currentPageData.time 时间
     * @apiSuccess (返回结果) {Object[]} currentPageData.pipeDataList 管道数据列表
     * @apiSuccess (返回结果) {Int} currentPageData.monitorPointID 监测点ID
     * @apiSuccess (返回结果) {String} currentPageData.monitorPointName 监测点名称
     * @apiSuccess (返回结果) {Double} currentPageData.pipeDataList.emptyPipeDistance 空管距离(m)
     * @apiSuccess (返回结果) {Object} currentPageData.pipeDataList.levelElevation 水位高程
     * @apiSuccess (返回结果) {Double} currentPageData.pipeDataList.levelElevation.value 值(m)
     * @apiSuccess (返回结果) {Double} [currentPageData.pipeDataList.levelElevation.osmoticValue] '渗压管高程差'减去'阈值'的结果，防渗墙两侧监测点配置有'阈值'且该值小于等于0时才有该项<br>为0时表示两侧渗压管水位高程差等于阈值,为负值时表示两侧渗压管水位高程差小于阈值
     * @apiSuccess (返回结果) {Object} [currentPageData.pipeDataList.levelElevation.eigenValue] 特征值数据 配置有'特征值'时且当前值超出特征值时才有该项,优先选取最大的'特征值'
     * @apiSuccess (返回结果) {Int} currentPageData.pipeDataList.levelElevation.eigenValue.eigenValueID 特征值ID
     * @apiSuccess (返回结果) {String} currentPageData.pipeDataList.levelElevation.eigenValue.eigenValueName 特征值名称
     * @apiSuccess (返回结果) {String} currentPageData.pipeDataList.levelElevation.eigenValue.chnUnit 特征值中文单位
     * @apiSuccess (返回结果) {String} currentPageData.pipeDataList.levelElevation.eigenValue.engUnit 特征值英文单位
     * @apiSuccess (返回结果) {Double} currentPageData.pipeDataList.levelElevation.eigenValue.abnormalValue 特征值异常值 为正值时表示水位高程超过该特征值
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryLongitudinalPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryLongitudinalPage(@Valid @RequestBody QueryLongitudinalPageParam param) {
        return ResultWrapper.success(PageUtil.page(thematicDataAnalysisService.queryLongitudinalList(param), param.getPageSize(), param.getCurrentPage()));
    }

    /**
     * @api {POST} /QueryRainWaterData 水雨情分析
     * @apiDescription 水雨情分析
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryRainWaterData
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} rainfallMonitorPointID 降雨量监测点ID
     * @apiParam (请求体) {Int} distanceMonitorPointID 库水位监测点ID
     * @apiParam (请求体) {Int} [volumeFlowInputMonitorPointID] 入库流量监测点ID
     * @apiParam (请求体) {Int} [volumeFlowOutputMonitorPointID] 出库流量监测点ID
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {Int[]} [eigenvalueIDList] 特征值IDList
     * @apiParam (请求体) {Int[]} [dataEventIDList] 大事件IDList
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiSuccess (返回结果) {Object[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.time 时间
     * @apiSuccess (返回结果) {Double} dataList.rainfall 降雨量(mm)
     * @apiSuccess (返回结果) {Double} dataList.distance 库水位(m)
     * @apiSuccess (返回结果) {Double} [dataList.volumeFlowInput] 入库流量(m³/s)
     * @apiSuccess (返回结果) {Double} [dataList.volumeFlowOutput] 出库流量(m³/s)
     * @apiSuccess (返回结果) {Object[]} maxDataList 最大数据列表
     * @apiSuccess (返回结果) {Int} maxDataList.key 标识枚举 1.降雨量(mm) 2.distance库水位(m) 3.volumeFlowInput入库流量(m³/s) 4.volumeFlowOutput出库流量(m³/s)
     * @apiSuccess (返回结果) {Double} maxDataList.value 值
     * @apiSuccess (返回结果) {DateTime} maxDataList.time 时间
     * @apiSuccess (返回结果) {Object[]} [eigenvalueDataList] 特征值数据列表
     * @apiSuccess (返回结果) {Int} eigenvalueDataList.monitorType 监测类型
     * @apiSuccess (返回结果) {Int} eigenvalueDataList.eigenValueID 特征值ID
     * @apiSuccess (返回结果) {String} eigenvalueDataList.eigenValueName 特征值名称
     * @apiSuccess (返回结果) {Double} eigenvalueDataList.eigenValue 值
     * @apiSuccess (返回结果) {String} eigenvalueDataList.engUnit 单位英文名称
     * @apiSuccess (返回结果) {String} eigenvalueDataList.chnUnit 单位中文名称
     * @apiSuccess (返回结果) {Object[]} [dataEventDataList] 大事件数据列表
     * @apiSuccess (返回结果) {Int} dataEventDataList.eventID 大事件ID
     * @apiSuccess (返回结果) {String} dataEventDataList.eventName 大事件名称
     * @apiSuccess (返回结果) {String} dataEventDataList.timeRange 时间范围
     * @apiSampleRequest off
     * @apiSuccessExample {json} 响应结果示例
     * {"dataList":[{"time":"2023-11-01 00:00:00","rainfall":0.0,"distance":0.0,"volumeFlowInput":0.0,"volumeFlowOutput":0.0}],"maxDataList":[{"value":4.71,"key":1,"time":"2023-11-21 05:00:00"},{"value":10.0,"key":2,"time":"2023-11-17 16:00:00"},{"value":9.98,"key":3,"time":"2023-11-21 06:00:00"},{"value":9.96,"key":4,"time":"2023-11-21 06:00:00"}],"eigenvalueDataList":[{"monitorType":2,"eigenValueID":11,"eigenValueName":"214库水位特征值","eigenValue":5.0,"chnUnit":"毫米","engUnit":"mm"}],"dataEventDataList":[{"eventID":12,"eventName":"库水位大事件","timeRange":"[{\"startTime\": \"2023-10-18 00:00:00\", \"endTime\": \"2023-10-30 23:59:59\"}, {\"startTime\": \"2023-11-20 00:00:00\", \"endTime\": \"2023-11-21 23:59:59\"}]"}]}
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryRainWaterData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryRainWaterData(@Valid @RequestBody QueryRainWaterDataParam param) {
        return thematicDataAnalysisService.queryRainWaterData(param);
    }

    /**
     * @api {POST} /QueryRainWaterPageData 水雨情分析-列表数据(分页)
     * @apiDescription 水雨情分析-列表数据(分页)
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryRainWaterPageData
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} rainfallMonitorPointID 降雨量监测点ID
     * @apiParam (请求体) {Int} distanceMonitorPointID 库水位监测点ID
     * @apiParam (请求体) {Int} [volumeFlowInputMonitorPointID] 入库流量监测点ID
     * @apiParam (请求体) {Int} [volumeFlowOutputMonitorPointID] 出库流量监测点ID
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {DateTime} currentPageData.time 时间
     * @apiSuccess (返回结果) {Double} currentPageData.rainfall 降雨量(mm)
     * @apiSuccess (返回结果) {Double} currentPageData.distance 库水位(m)
     * @apiSuccess (返回结果) {Double} [currentPageData.volumeFlowInput] 入库流量(m³/s)
     * @apiSuccess (返回结果) {Double} [currentPageData.volumeFlowOutput] 出库流量(m³/s)
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryRainWaterPageData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryRainWaterPageData(@Valid @RequestBody QueryRainWaterDataPageParam param) {
        return thematicDataAnalysisService.queryRainWaterPageData(param);
    }

    /**
     * @api {POST} /QueryDryBeachDataPage 干滩分析(分页)
     * @apiDescription 干滩分析
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryDryBeachDataPage
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} dryBeachMonitorPointID 干滩监测点ID
     * @apiParam (请求体) {Int} distanceMonitorPointID 库水位监测点ID
     * @apiParam (请求体) {Int} rainfallMonitorPointID 降雨量监测点ID
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求体) {DateTime} endTime 查询时段结束时间
     * @apiParam (请求体) {Int} pageSize 页大小
     * @apiParam (请求体) {Int} currentPage 当前页
     * @apiSuccess (返回结果) {Int} totalCount 总数量
     * @apiSuccess (返回结果) {Int} totalPage 总页数
     * @apiSuccess (返回结果) {Object[]} currentPageData 当前页数据
     * @apiSuccess (返回结果) {Double} currentPageData.slopeRratio 坡度比
     * @apiSuccess (返回结果) {Double} currentPageData.rainfall 降雨量(mm)
     * @apiSuccess (返回结果) {Object} currentPageData.dryBeach 干滩数据
     * @apiSuccess (返回结果) {Double} currentPageData.dryBeach.value 滩长(m)
     * @apiSuccess (返回结果) {Double} [currentPageData.dryBeach.abnormalValue] 异常值 配置了特征值'最小干滩长度'且当前值为异常值时才有该项<br>为负值时表示该点值超出最小干滩长度
     * @apiSuccess (返回结果) {Object} currentPageData.distance 库水位数据
     * @apiSuccess (返回结果) {Double} currentPageData.distance.value 库水位(m)
     * @apiSuccess (返回结果) {Double} [currentPageData.distance.abnormalValue] 异常值 配置了特征值'设计洪水位'且当前值为异常值时才有该项<br>为正值时表示该点值超出设计洪水位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryDryBeachDataPage", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDryBeachDataPage(@Valid @RequestBody QueryDryBeachDataPageParam param) {
        return PageUtil.page(thematicDataAnalysisService.queryDryBeachDataList(param), param.getPageSize(), param.getCurrentPage());
    }

    /**
     * @api {POST} /QueryDryBeachDataList 干滩分析(不分页)
     * @apiDescription 干滩分析
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryDryBeachDataList
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} dryBeachMonitorPointID 干滩监测点ID
     * @apiParam (请求体) {Int} distanceMonitorPointID 库水位监测点ID
     * @apiParam (请求体) {Int} rainfallMonitorPointID 降雨量监测点ID
     * @apiParam (请求体) {Int} displayDensity 显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年
     * @apiParam (请求体) {DateTime} startTime 查询时段开始时间
     * @apiParam (请求体) {DateTime} endTime 查询时段结束时间
     * @apiSuccess (返回结果) {Objcet[]} dataList 数据列表
     * @apiSuccess (返回结果) {DateTime} time 时间
     * @apiSuccess (返回结果) {Double} dataList.slopeRratio 坡度比
     * @apiSuccess (返回结果) {Double} dataList.rainfall 降雨量(mm)
     * @apiSuccess (返回结果) {Object} dataList.dryBeach 干滩数据
     * @apiSuccess (返回结果) {Double} dataList.dryBeach.value 滩长(m)
     * @apiSuccess (返回结果) {Double} [dataList.dryBeach.abnormalValue] 异常值 配置了特征值'最小干滩长度'且当前值为异常值时才有该项<br>为负值时表示该点值超出最小干滩长度
     * @apiSuccess (返回结果) {Object} dataList.distance 库水位数据
     * @apiSuccess (返回结果) {Double} dataList.distance.value 库水位(m)
     * @apiSuccess (返回结果) {Double} [dataList.distance.abnormalValue] 异常值 配置了特征值'设计洪水位'且当前值为异常值时才有该项<br>为正值时表示该点值超出设计洪水位
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryDryBeachDataList", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDryBeachDataList(@Valid @RequestBody QueryDryBeachDataListParam param) {
        return thematicDataAnalysisService.queryDryBeachDataList(param);
    }

    /**
     * @api {POST} /QueryDryBeachData 干滩分析-最新数据
     * @apiDescription 干滩分析
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryDryBeachData
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} dryBeachMonitorPointID 干滩监测点ID
     * @apiParam (请求体) {Int} distanceMonitorPointID 库水位监测点ID
     * @apiParam (请求体) {Int} rainfallMonitorPointID 降雨量监测点ID
     * @apiSuccess (返回结果) {Double} slopeRratio 坡度比
     * @apiSuccess (返回结果) {Double} rainfall 降雨量(mm)
     * @apiSuccess (返回结果) {Double} dryBeach 滩长(m)
     * @apiSuccess (返回结果) {Double} distance 库水位(m)
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryDryBeachData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryDryBeachData(@Valid @RequestBody QueryDryBeachDataParam param) {
        return thematicDataAnalysisService.queryDryBeachData(param);
    }

    /**
     * @api {POST} /AddManualDataBatch 数据比测-批量人工录入
     * @apiDescription 数据比测-批量人工录入
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName AddManualDataBatch
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Object[]} dataList 数据列表
     * @apiParam (请求体) {Int} dataList.sensorID 传感器ID
     * @apiParam (请求体) {DateTime} dataList.time 时间
     * @apiParam (请求体) {String} dataList.fieldToken 属性标识
     * @apiParam (请求体) {String} dataList.value 值字符串,将根据匹配到的属性字段类型解析成对应的值
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/AddManualDataBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object addManualDataBatch(@Valid @RequestBody AddManualDataBatchParam param) {
        thematicDataAnalysisService.addManualDataBatch(param);
        return ResultWrapper.successWithNothing();
    }

    /**
     * @api {POST} /GetImportManualTemplate 数据比测-获取批量导入模板
     * @apiDescription 数据比测-获取批量导入模板
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName GetImportManualTemplate
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {Blob} file 文件流
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/GetImportManualTemplate", consumes = DefaultConstant.JSON)
    public void getImportManualTemplate(@Valid @RequestBody GetImportManualTemplateParam param, HttpServletResponse response) {
        thematicDataAnalysisService.getImportManualTemplate(param, response);
    }

    /**
     * @api {POST} /ImportManualDataBatch 数据比测-批量导入
     * @apiDescription 数据比测-批量导入
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName ImportManualDataBatch
     * @apiParam (请求参数) {MultipartFile} file 导入文件
     * @apiParam (请求参数) {Int} projectID 工程ID
     * @apiParam (请求参数) {Int} monitorType 监测类型
     * @apiSuccess (返回结果) {String} none 无
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/ImportManualDataBatch", produces = DefaultConstant.JSON, consumes = DefaultConstant.MULTIPART_FORM_DATA)
    public Object importManualDataBatch(@Valid @ResourceSymbol(ResourceType.BASE_PROJECT) @Positive(message = "工程ID不能小于1")
                                        @RequestParam("projectID") Integer projectID,
                                        @Positive(message = "监测类型不能为空") @RequestParam("monitorType") Integer monitorType,
                                        @RequestParam MultipartFile file) {
        return thematicDataAnalysisService.importManualDataBatch(projectID, monitorType, file);
    }

    /**
     * @api {POST} /QueryCompareAnalysisData 查询数据比测人工数据
     * @apiDescription 查询数据比测人工数据
     * @apiVersion 1.0.0
     * @apiGroup 专题模块
     * @apiName QueryCompareAnalysisData
     * @apiParam (请求体) {Int} projectID 工程ID
     * @apiParam (请求体) {String} fieldToken 属性标识
     * @apiParam (请求体) {DateTime} startTime 开始时间
     * @apiParam (请求体) {DateTime} endTime 结束时间
     * @apiParam (请求体) {Int} autoSensorID 自动传感器ID
     * @apiParam (请求体) {Int} manualSensorID 手动传感器ID
     * @apiParam (请求体) {Int} intervalType 分析间隔类型 1.小时 2.天
     * @apiParam (请求体) {Double} intervalValue 分析间隔值，分析间隔值只能精确到毫秒。
     * @apiSuccess (返回结果) {Int} autoCount 自动化记录数
     * @apiSuccess (返回结果) {Int} manualCount 人工记录数
     * @apiSuccess (返回结果) {Int} abnormalCount 异常自动化记录数
     * @apiSuccess (返回结果) {Double} abnormalRatio 误差率（%）,为-1时表示‘人工数据0组，无法判断人工数据误差比率’
     * @apiSuccess (返回结果) {Object[]} dataList 人工数据列表
     * @apiSuccess (返回结果) {DateTime} dataList.autoTime 自动化数据时间
     * @apiSuccess (返回结果) {DateTime} dataList.manualTime 人工数据时间
     * @apiSuccess (返回结果) {Boolean} dataList.normal 是否正常 true正常|false异常
     * @apiSuccess (返回结果) {Double} dataList.autoData 自动化传感器数据
     * @apiSuccess (返回结果) {Double} dataList.manualData 人工传感器数据
     * @apiSuccess (返回结果) {String} dataList.chnUnit 中文单位
     * @apiSuccess (返回结果) {String} dataList.engUnit 英文单位
     * @apiSuccess (返回结果) {Double} [dataList.abnormalValue] 超限值
     * @apiSampleRequest off
     * @apiPermission 项目权限 mdmbase:
     */
    //@Permission(permissionName = "")
    @PostMapping(value = "/QueryCompareAnalysisData", produces = DefaultConstant.JSON, consumes = DefaultConstant.JSON)
    public Object queryCompareAnalysisData(@Valid @RequestBody QueryCompareAnalysisDataParam param) {
        return thematicDataAnalysisService.queryCompareAnalysisData(param);
    }
}
