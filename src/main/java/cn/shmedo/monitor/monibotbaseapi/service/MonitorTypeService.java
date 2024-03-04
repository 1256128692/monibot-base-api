package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeFieldListV2Info;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeFieldWithFormula;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorType4web;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.QueryFormulaParamsResult;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-20 16:53
 **/
public interface MonitorTypeService extends IService<TbMonitorType> {
    PageUtil.Page<TbMonitorType4web> queryMonitorTypePage(QueryMonitorTypePageParam request);

    void addCustomizedMonitorType(AddCustomizedMonitorTypeParam pa, Integer userID);

    MonitorTypeDetail queryMonitorTypeDetail(Integer monitorType,  Integer companyID);

     void addTemplate(AddTemplateParam pa, Integer userID);

    void setParam(SetParamParam pa);

    void setFormula(SetFormulaParam pa);

    List<TbParameter> queryParam(QueryParamParam pa);

    List<MonitorTypeFieldWithFormula> queryMonitorTypeFieldWithFormula(QueryMonitorTypeFieldWithFormulaParam pa);

    void updateCustomizedMonitorType(UpdateCustomizedMonitorTypeParam pa);

    void updateCustomizedMonitorTypeField(UpdateCustomizedMonitorTypeFieldParam pa);

    void addMonitorTypeField(AddMonitorTypeFieldParam pa);

    void deleteTemplateBatch(List<Integer> templateIDList);

    void deleteMonitorTypeBatch(List<Integer> monitorTypeList);

    void deleteMonitorTypeFieldBatch(Integer monitorType, List<Integer> fieldIDList);

    void addPredefinedMonitorType(AddPredefinedMonitorTypeParam pa, Integer usrID);

    Object querySimpleMonitorTypeList(QuerySimpleMonitorTypeListParam pa);

    QueryFormulaParamsResult queryFormulaParams(QueryFormulaParamsRequest request);

    void refreshMonitorTypeCache(RefreshMonitorTypeCacheParam pa);

    List<MonitorTypeFieldListV2Info> queryMonitorTypeFieldListV2(QueryMonitorTypeFieldListV2Param param);
}
