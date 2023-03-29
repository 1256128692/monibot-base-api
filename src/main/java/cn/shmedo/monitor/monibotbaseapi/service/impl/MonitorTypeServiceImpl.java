package cn.shmedo.monitor.monibotbaseapi.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorTypeFieldClass;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.AddCustomizedMonitorTypeParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.QueryMonitorTypePageParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.MonitorTypeTemplateDatasourceToken;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorType4web;
import cn.shmedo.monitor.monibotbaseapi.model.response.TbMonitorTypeTemplate4Web;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.TypeAndCount;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-24 17:02
 **/
@Service
@AllArgsConstructor
@Slf4j
public class MonitorTypeServiceImpl extends ServiceImpl<TbMonitorTypeMapper, TbMonitorType> implements MonitorTypeService {
    private final TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper;
    private final TbMonitorTypeMapper tbMonitorTypeMapper;
    private final TbMonitorTypeTemplateMapper tbMonitorTypeTemplateMapper;
    private final TbTemplateDataSourceMapper tbTemplateDataSourceMapper;
    private final TbTemplateScriptMapper tbTemplateScriptMapper;
    private final TbTemplateFormulaMapper tbTemplateFormulaMapper;

    @Override
    public PageUtil.Page<TbMonitorType4web> queryMonitorTypePage(QueryMonitorTypePageParam pa) {
        Page<TbMonitorType4web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        List<Integer> typeList;
        if (ObjectUtil.isAllEmpty(pa.getFuzzyFieldName(), pa.getFuzzyFieldToken())){
            typeList = null;
        }else {
            typeList = tbMonitorTypeFieldMapper.queryMonitorTypeByFuzzyNameAndFuzzyToken(pa.getFuzzyFieldName(), pa.getFuzzyFieldToken());
            if (ObjectUtil.isEmpty(typeList)){
                return PageUtil.Page.empty();
            }
        }
        IPage<TbMonitorType4web> pageData =  tbMonitorTypeMapper.queryPage(page, pa.getCompanyID(), pa.getCreateType(), pa.getFuzzyTypeName(), typeList);
        if (ObjectUtil.isEmpty(pageData.getRecords())){
            return PageUtil.Page.empty();
        }
        // 处理字段和数据源统计
        List<Integer> monitorTypeList = pageData.getRecords().stream().map(TbMonitorType4web::getMonitorType).collect(Collectors.toList());
        List<TbMonitorTypeField> temp = tbMonitorTypeFieldMapper.queryByMonitorTypes(monitorTypeList, pa.getAllFiled());
        Map<Integer, List<TbMonitorTypeField>> typeMap = temp.stream().collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));
        List<TypeAndCount>  countList= tbMonitorTypeTemplateMapper.countGroupByMonitorType(monitorTypeList);
        Map<Integer, Integer> countMap = countList.stream().collect(Collectors.toMap(TypeAndCount::getType, TypeAndCount::getCount));
        pageData.getRecords().forEach(item ->{
            item.setDatasourceCount(countMap.get(item.getMonitorType()));
            item.setFieldList(typeMap.get(item.getMonitorType()));
        });
        return new PageUtil.Page<>(pageData.getPages(),pageData.getRecords(),pageData.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomizedMonitorType(AddCustomizedMonitorTypeParam pa, Integer userID) {
        QueryWrapper<TbMonitorType> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("monitorType").last("limit 1");
        TbMonitorType temp = tbMonitorTypeMapper.selectOne(wrapper);
        Integer type;
        if (temp == null || temp.getMonitorType()<=20000){
            type = 20001;
        }else {
            type = temp.getMonitorType() +1;
        }
        TbMonitorType tbMonitorType = Param2DBEntityUtil.fromAddCustomizedMonitorTypeParam2tbMonitorType(pa, userID, type);
        tbMonitorTypeMapper.insert(tbMonitorType);
        List<TbMonitorTypeField> list = Param2DBEntityUtil.fromAddCustomizedMonitorTypeParam2TbMonitorTypeFieldList(pa, userID, type);
        tbMonitorTypeFieldMapper.insertBatch(list);
    }

    @Override
    public MonitorTypeDetail queryMonitorTypeDetail(Integer monitorType) {
        QueryWrapper<TbMonitorType> wrapper = new QueryWrapper<>();
        wrapper.eq("monitorType", monitorType);
        TbMonitorType temp = tbMonitorTypeMapper.selectOne(wrapper);
        MonitorTypeDetail monitorTypeDetail = new MonitorTypeDetail();
        BeanUtil.copyProperties(temp, monitorTypeDetail, false);
        List<TbMonitorTypeField> list = tbMonitorTypeFieldMapper.queryByMonitorTypes(List.of(monitorType), true);
        monitorTypeDetail.setFieldList(list.stream().filter(item ->!item.getFieldClass().equals(MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass())).collect(Collectors.toList()));
        monitorTypeDetail.setClass3FieldList(list.stream().filter(item ->item.getFieldClass().equals(MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass())).collect(Collectors.toList()));
        QueryWrapper<TbMonitorTypeTemplate> templateQueryWrapper = new QueryWrapper<>();
        templateQueryWrapper.eq("monitorType", monitorType);
        List<TbMonitorTypeTemplate> tbMonitorTypeTemplates = tbMonitorTypeTemplateMapper.selectList(templateQueryWrapper);
        if (ObjectUtil.isNotEmpty(tbMonitorTypeTemplates)){
            List<String> templateDataSourceIDList = tbMonitorTypeTemplates.stream().map(TbMonitorTypeTemplate::getTemplateDataSourceID).collect(Collectors.toList());
            List<Integer> templateIDList = tbMonitorTypeTemplates.stream().map(TbMonitorTypeTemplate::getID).toList();

            monitorTypeDetail.setTemplateList(BeanUtil.copyToList(tbMonitorTypeTemplates, TbMonitorTypeTemplate4Web.class));
            QueryWrapper<TbTemplateDataSource> dataSourceQueryWrapper = new QueryWrapper<>();
            dataSourceQueryWrapper.in("templateDataSourceID", templateDataSourceIDList);
            List<TbTemplateDataSource> tbTemplateDataSources = tbTemplateDataSourceMapper.selectList(dataSourceQueryWrapper);
            if (ObjectUtil.isNotEmpty(tbTemplateDataSources)){
                // TODO 需要处理物模型的其他数据
                Map<String, List<TbTemplateDataSource>> collect = tbTemplateDataSources.stream().collect(Collectors.groupingBy(TbTemplateDataSource::getTemplateDataSourceID));
                monitorTypeDetail.getTemplateList().forEach(item -> {
                    item.setTokenList(MonitorTypeTemplateDatasourceToken.valueOf(collect.get(item.getTemplateDataSourceID())));
                });
            }
            QueryWrapper<TbTemplateScript> scriptQueryWrapper = new QueryWrapper<>();
            scriptQueryWrapper.in("TemplateID", templateIDList);
            List<TbTemplateScript> scriptList = tbTemplateScriptMapper.selectList(scriptQueryWrapper);
            if (ObjectUtil.isNotEmpty(scriptList)){
                Map<Integer, TbTemplateScript> collect = scriptList.stream().collect(Collectors.toMap(TbTemplateScript::getTemplateID, Function.identity()));
                monitorTypeDetail.getTemplateList().forEach(item -> {
                    item.setScript(collect.get(item.getID()).getScript());
                });
            }
            QueryWrapper<TbTemplateFormula> formulaQueryWrapper = new QueryWrapper<>();
            formulaQueryWrapper.in("TemplateID", templateIDList);
            List<TbTemplateFormula> formulaList = tbTemplateFormulaMapper.selectList(formulaQueryWrapper);
            if (ObjectUtil.isNotEmpty(formulaList)){
                Map<Integer, List<TbTemplateFormula>> collect = formulaList.stream().collect(Collectors.groupingBy(TbTemplateFormula::getTemplateID));
                monitorTypeDetail.getTemplateList().forEach(item -> {
                    item.setFormulaList(collect.get(item.getID()));
                });
            }
        }
        return monitorTypeDetail;
    }
}
