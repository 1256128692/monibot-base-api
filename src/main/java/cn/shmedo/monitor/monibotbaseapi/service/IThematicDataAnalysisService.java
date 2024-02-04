package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 15:59
 */
public interface IThematicDataAnalysisService {
    @Deprecated
    StThematicAnalysisInfo queryStGroupRealData(QueryStDataParam param);

    @Deprecated
    DmThematicAnalysisInfo queryDmAnalysisData(QueryDmDataParam param);

    @Deprecated
    PageUtil.Page<DmThematicAnalysisPageInfo> queryDmAnalysisDataPage(QueryDmDataPageParam param);

    @Deprecated
    List<Date> queryDmPageDataList(QueryDmDataParam param);

    List<ThematicMonitorPointInfo> queryThematicMonitorPointByProjectID(Integer projectID);

    List<ThematicGroupPointListInfo> queryThematicGroupPointList(QueryThematicGroupPointListParam param);

    List<ThematicQueryTransverseInfo> queryTransverseList(QueryTransverseListParam param);

    WetLineConfigInfo queryWetLineConfig(QueryWetLineConfigParam param);

    List<Map<String, Object>> queryLongitudinalList(QueryLongitudinalListParam param);

    ThematicRainWaterAnalysisInfo queryRainWaterData(QueryRainWaterDataParam param);

    PageUtil.Page<ThematicRainWaterDataInfo> queryRainWaterPageData(QueryRainWaterDataPageParam param);

    List<ThematicDryBeachInfo> queryDryBeachDataList(QueryDryBeachDataListParam param);

    DryBeachDataInfo queryDryBeachData(QueryDryBeachDataParam param);

    void addManualDataBatch(AddManualDataBatchParam param);

    void getImportManualTemplate(GetImportManualTemplateParam param, HttpServletResponse response);

    ResultWrapper<Object> importManualDataBatch(Integer projectID, Integer monitorType, MultipartFile file);

    CompareAnalysisDataInfo queryCompareAnalysisData(QueryCompareAnalysisDataParam param);

    void flushWetLineConfig(Integer projectID);
}
