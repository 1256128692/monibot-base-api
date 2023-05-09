package cn.shmedo.monitor.monibotbaseapi.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.ParameterSubjectType;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisConstant;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryModelFieldBatchParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.*;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorType.QueryFormulaParamsResult;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.ModelField;
import cn.shmedo.monitor.monibotbaseapi.model.tempitem.TypeAndCount;
import cn.shmedo.monitor.monibotbaseapi.service.MonitorTypeService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.ThirdHttpService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.Param2DBEntityUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
    private final TbParameterMapper tbParameterMapper;
    private final FileConfig fileConfig;
    private final RedisService redisService;
    private final TbMonitorItemMapper tbMonitorItemMapper;

    @Override
    public PageUtil.Page<TbMonitorType4web> queryMonitorTypePage(QueryMonitorTypePageParam pa) {
        Page<TbMonitorType4web> page = new Page<>(pa.getCurrentPage(), pa.getPageSize());
        List<Integer> typeList;
        if (ObjectUtil.isAllEmpty(pa.getFuzzyFieldName(), pa.getFuzzyFieldToken())) {
            typeList = null;
        } else {
            typeList = tbMonitorTypeFieldMapper.queryMonitorTypeByFuzzyNameAndFuzzyToken(pa.getFuzzyFieldName(), pa.getFuzzyFieldToken());
            if (ObjectUtil.isEmpty(typeList)) {
                return PageUtil.Page.empty();
            }
        }
        IPage<TbMonitorType4web> pageData = tbMonitorTypeMapper.queryPage(page, pa.getCompanyID(), pa.getCreateType(), pa.getFuzzyTypeName(), typeList);
        if (ObjectUtil.isEmpty(pageData.getRecords())) {
            return PageUtil.Page.empty();
        }
        // 处理字段和数据源统计
        List<Integer> monitorTypeList = pageData.getRecords().stream().map(TbMonitorType4web::getMonitorType).collect(Collectors.toList());
        List<TbMonitorTypeField> temp = tbMonitorTypeFieldMapper.queryByMonitorTypes(monitorTypeList, pa.getAllFiled());
        Map<Integer, List<TbMonitorTypeField>> typeMap = temp.stream().collect(Collectors.groupingBy(TbMonitorTypeField::getMonitorType));
        List<TypeAndCount> countList = tbMonitorTypeTemplateMapper.countGroupByMonitorType(monitorTypeList);
        Map<Integer, Integer> countMap = countList.stream().collect(Collectors.toMap(TypeAndCount::getType, TypeAndCount::getCount));
        pageData.getRecords().forEach(item -> {
            item.setDatasourceCount(countMap.get(item.getMonitorType()));
            item.setFieldList(typeMap.get(item.getMonitorType()));
        });
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCustomizedMonitorType(AddCustomizedMonitorTypeParam pa, Integer userID) {
        Integer type;
        if (pa.getMonitorType() != null) {
            type = pa.getMonitorType();
        } else {
            QueryWrapper<TbMonitorType> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("monitorType").last("limit 1");
            TbMonitorType temp = tbMonitorTypeMapper.selectOne(wrapper);
            if (temp == null || temp.getMonitorType() <= 20000) {
                type = 20001;
            } else {
                type = temp.getMonitorType() + 1;
            }
        }

        TbMonitorType tbMonitorType = Param2DBEntityUtil.fromAddCustomizedMonitorTypeParam2tbMonitorType(pa, userID, type);
        tbMonitorTypeMapper.insert(tbMonitorType);
        List<TbMonitorTypeField> list = Param2DBEntityUtil.buildTbMonitorTypeFieldList(pa.getFieldList(), type);
        tbMonitorTypeFieldMapper.insertBatch(list);
        setMonitorTypeCache(tbMonitorType, list);
    }

    private void setMonitorTypeCache(TbMonitorType tbMonitorType, List<TbMonitorTypeField> list) {
        MonitorTypeCacheData cacheData = MonitorTypeCacheData.valueof(tbMonitorType, list);
        redisService.put(RedisKeys.MONITOR_TYPE_KEY, tbMonitorType.getMonitorType().toString(), cacheData);
    }

    @Override
    public MonitorTypeDetail queryMonitorTypeDetail(Integer monitorType, Integer companyID) {
        QueryWrapper<TbMonitorType> wrapper = new QueryWrapper<>();
        wrapper.eq("monitorType", monitorType);
        TbMonitorType temp = tbMonitorTypeMapper.selectOne(wrapper);
        MonitorTypeDetail monitorTypeDetail = new MonitorTypeDetail();
        BeanUtil.copyProperties(temp, monitorTypeDetail, false);
        List<TbMonitorTypeField> list = tbMonitorTypeFieldMapper.queryByMonitorTypes(List.of(monitorType), true);
        monitorTypeDetail.setFieldList(list.stream().filter(item -> !item.getFieldClass().equals(FieldClass.EXTEND_CONFIG.getCode())).collect(Collectors.toList()));
        monitorTypeDetail.setClass3FieldList(list.stream().filter(item -> item.getFieldClass().equals(FieldClass.EXTEND_CONFIG.getCode())).collect(Collectors.toList()));
        QueryWrapper<TbMonitorTypeTemplate> templateQueryWrapper = new QueryWrapper<>();
        templateQueryWrapper.eq("monitorType", monitorType);
        List<TbMonitorTypeTemplate> tbMonitorTypeTemplates = tbMonitorTypeTemplateMapper.selectList(templateQueryWrapper);
        if (ObjectUtil.isNotEmpty(tbMonitorTypeTemplates)) {
            List<String> templateDataSourceIDList = tbMonitorTypeTemplates.stream().map(TbMonitorTypeTemplate::getTemplateDataSourceID).collect(Collectors.toList());
            List<Integer> templateIDList = tbMonitorTypeTemplates.stream().map(TbMonitorTypeTemplate::getID).toList();

            monitorTypeDetail.setTemplateList(BeanUtil.copyToList(tbMonitorTypeTemplates, TbMonitorTypeTemplate4Web.class));
            QueryWrapper<TbTemplateDataSource> dataSourceQueryWrapper = new QueryWrapper<>();
            dataSourceQueryWrapper.in("templateDataSourceID", templateDataSourceIDList);
            List<TbTemplateDataSource> tbTemplateDataSources = tbTemplateDataSourceMapper.selectList(dataSourceQueryWrapper);
            if (ObjectUtil.isNotEmpty(tbTemplateDataSources)) {
                Set<String> modelTokenList = tbTemplateDataSources.stream().filter(item -> item.getDataSourceType().equals(DataSourceType.IOT_SENSOR.getCode()))
                        .map(item -> item.getTemplateDataSourceToken().split("_")[0]).collect(Collectors.toSet());
                Map<String, List<ModelField>> iotModelFieldMap;
                if (ObjectUtil.isNotEmpty(modelTokenList)) {
                    IotService iotService = ThirdHttpService.getInstance(IotService.class, ThirdHttpService.Iot);
                    var thirdParam = new QueryModelFieldBatchParam(companyID, new ArrayList<>(modelTokenList));
                    ResultWrapper<Map<String, List<ModelField>>> resultWrapper = iotService.queryModelFieldBatch(thirdParam, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());
                    if (!resultWrapper.apiSuccess()) {
                        throw new CustomBaseException(resultWrapper.getCode(), resultWrapper.getMsg());
                    } else if (resultWrapper.getData() != null) {
                        iotModelFieldMap = resultWrapper.getData();
                    } else {
                        iotModelFieldMap = Map.of();
                    }

                } else {
                    iotModelFieldMap = Map.of();
                }
                Map<String, List<ModelField>> finalIotModelFieldMap = iotModelFieldMap;

                Map<String, List<TbTemplateDataSource>> collect = tbTemplateDataSources.stream().collect(Collectors.groupingBy(TbTemplateDataSource::getTemplateDataSourceID));
                monitorTypeDetail.getTemplateList().forEach(item -> {
                    item.setTokenList(MonitorTypeTemplateDatasourceToken.valueOf(collect.get(item.getTemplateDataSourceID())));
                });

                if (finalIotModelFieldMap.size() > 0) {
                    monitorTypeDetail.getTemplateList().forEach(
                            item -> {
                                item.getTokenList().forEach(
                                        token -> {
                                            if (token.getIotModelToken() != null) {
                                                token.setIotModelFieldList(finalIotModelFieldMap.get(token.getIotModelToken()));
                                            }
                                        }
                                );
                            }
                    );
                }
            }
            QueryWrapper<TbTemplateScript> scriptQueryWrapper = new QueryWrapper<>();
            scriptQueryWrapper.in("TemplateID", templateIDList);
            List<TbTemplateScript> scriptList = tbTemplateScriptMapper.selectList(scriptQueryWrapper);
            if (ObjectUtil.isNotEmpty(scriptList)) {
                Map<Integer, TbTemplateScript> collect = scriptList.stream().collect(Collectors.toMap(TbTemplateScript::getTemplateID, Function.identity()));
                monitorTypeDetail.getTemplateList().forEach(item -> {
                    if (collect.containsKey(item.getID())) {
                        item.setScript(collect.get(item.getID()).getScript());
                    } else {
                        item.setScript(null);
                    }
                });
            }
            QueryWrapper<TbTemplateFormula> formulaQueryWrapper = new QueryWrapper<>();
            formulaQueryWrapper.in("TemplateID", templateIDList);
            List<TbTemplateFormula> formulaList = tbTemplateFormulaMapper.selectList(formulaQueryWrapper);
            if (ObjectUtil.isNotEmpty(formulaList)) {
                Map<Integer, List<TbTemplateFormula>> collect = formulaList.stream().collect(Collectors.groupingBy(TbTemplateFormula::getTemplateID));
                monitorTypeDetail.getTemplateList().forEach(item -> {
                    item.setFormulaList(collect.get(item.getID()));
                });
            }
        }
        return monitorTypeDetail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTemplate(AddTemplateParam pa, Integer userID) {
        TbMonitorTypeTemplate record = Param2DBEntityUtil.fromAddTemplateParam2TbMonitorTypeTemplate(pa, userID);
        tbMonitorTypeTemplateMapper.insert(record);
        List<TbTemplateDataSource> sources = Param2DBEntityUtil.fromAddTemplateParam2TbTemplateDataSourceList(record.getTemplateDataSourceID(), pa);
        tbTemplateDataSourceMapper.insertBatch(sources);
        TbTemplateScript script = null;
        if (!StringUtils.isBlank(pa.getScript())) {
            script = Param2DBEntityUtil.buildTbMonitorTypeTemplate(record.getID(), pa.getMonitorType(), pa.getScript());
            tbTemplateScriptMapper.insert(script);
        }
        List<TbTemplateFormula> formulaList = null;
        if (ObjectUtil.isNotEmpty(pa.getFormulaList())) {
            formulaList = Param2DBEntityUtil.buildTbTemplateFormulaList(record.getID(), pa.getMonitorType(), pa.getFormulaList());
            tbTemplateFormulaMapper.insertBatch(formulaList);
        }
        // 添加监测类型模板缓存
        addTemplateCache(record, sources, formulaList, ObjectUtil.isEmpty(script) ? null : Arrays.asList(script));
    }

    /**
     * 添加监测类型模板缓存
     *
     * @param template
     * @param sources
     * @param formulaList
     * @param scriptList
     */
    private void addTemplateCache(TbMonitorTypeTemplate template, List<TbTemplateDataSource> sources,
                                  List<TbTemplateFormula> formulaList, List<TbTemplateScript> scriptList) {
        MonitorTypeTemplateCacheData cacheData = MonitorTypeTemplateCacheData.valueOf(template, sources, formulaList, scriptList);
        redisService.put(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY, template.getID().toString(), JSONUtil.toJsonStr(cacheData));
    }

    /**
     * @param idList      删除的
     * @param parameters  新增的
     * @param subjectType 类型
     */
    @Transactional(rollbackFor = Exception.class)
    public void setParamCache(List<Integer> idList, List<TbParameter> parameters, Integer subjectType) {
        Map<String, String> strMap = redisService.getAll(RedisKeys.PARAMETER_PREFIX_KEY + subjectType);
        Map<String, List<ParameterCacheData>> subIDMap = new HashMap<>();
        strMap.entrySet().forEach(
                entry -> {
                    List<ParameterCacheData> list = JSONUtil.toList(JSONUtil.parseArray(entry.getValue()), ParameterCacheData.class);
                    subIDMap.put(entry.getKey(), list);
                }
        );
        if (CollectionUtils.isNotEmpty(idList)) {
            subIDMap.values().forEach(
                    list -> list.removeIf(item -> idList.contains(item.getID()))
            );
        }
        if (CollectionUtils.isNotEmpty(parameters)) {
            Map<String, List<ParameterCacheData>> cacheDataMap = ParameterCacheData.valueof2RedisMap(parameters);
            subIDMap.putAll(cacheDataMap);
        }
        redisService.putAll(RedisKeys.PARAMETER_PREFIX_KEY + subjectType, subIDMap);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setParam(SetParamParam pa) {
        List<Integer> idList = pa.getParamList().stream().map(ParamItem::getID).filter(Objects::nonNull).toList();
        if (ObjectUtil.isNotEmpty(idList)) {
            tbParameterMapper.deleteBatchIds(idList);
        }
        List<TbParameter> parameters = null;
        if (pa.getDeleteOnly() == null || !pa.getDeleteOnly()) {
            parameters = Param2DBEntityUtil.fromSetParamParam2TbParameterList(pa);
            tbParameterMapper.insertBatch(parameters);
        }

        setParamCache(idList, parameters, pa.getSubjectType());

    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setFormula(SetFormulaParam pa) {
        List<TbTemplateFormula> list = Param2DBEntityUtil.buildTbTemplateFormulaList(pa.getTemplateID(), pa.getMonitorType(), pa.getFormulaList());
        tbTemplateFormulaMapper.deleteBatchByFieldIDS(pa.getTemplateID(), list.stream().map(TbTemplateFormula::getFieldID).collect(Collectors.toList()));
        tbTemplateFormulaMapper.insertBatch(list);
        //修改缓存
        updateTemplateFormulaCache(pa.getTemplateID(), null, null, list, null);
    }

    /**
     * 更新监测类型模板公式缓存
     *
     * @param templateID
     * @param template
     * @param sources
     * @param formulaList
     * @param scriptList
     */
    private void updateTemplateFormulaCache(Integer templateID, TbMonitorTypeTemplate template, List<TbTemplateDataSource> sources,
                                            List<TbTemplateFormula> formulaList, List<TbTemplateScript> scriptList) {
        String cacheDataJson = redisService.get(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY, templateID.toString());
        if (StrUtil.isEmpty(cacheDataJson)) {
            return;
        }
        MonitorTypeTemplateCacheData cacheData = JSONUtil.toBean(cacheDataJson, MonitorTypeTemplateCacheData.class);
        if (ObjectUtil.isNotEmpty(template)) {
            BeanUtil.copyProperties(template, cacheData, "templateDataSourceList", "templateFormulaList", "templateScriptList");
        }
        if (CollUtil.isNotEmpty(sources)) {
            List<TemplateDataSourceCacheData> templateDataSourceList = BeanUtil.copyToList(sources, TemplateDataSourceCacheData.class);
            if (CollUtil.isNotEmpty(cacheData.getTemplateDataSourceList())) {
                List<Integer> sourceIDs = sources.stream().map(TbTemplateDataSource::getID).toList();
                List<TemplateDataSourceCacheData> otherSourceList = cacheData.getTemplateDataSourceList()
                        .stream().filter(ds -> !sourceIDs.contains(ds.getID())).toList();
                if (CollUtil.isNotEmpty(otherSourceList)) {
                    templateDataSourceList.addAll(otherSourceList);
                }
            }
            cacheData.setTemplateDataSourceList(templateDataSourceList);
        }
        if (CollUtil.isNotEmpty(formulaList)) {
            List<FormulaCacheData> templateFormulaList = BeanUtil.copyToList(formulaList, FormulaCacheData.class);
            if (CollUtil.isNotEmpty(cacheData.getTemplateFormulaList())) {
                List<Integer> formulaIDs = formulaList.stream().map(TbTemplateFormula::getID).toList();
                List<Integer> fieldIDs = formulaList.stream().map(TbTemplateFormula::getFieldID).toList();
                List<FormulaCacheData> otherFormulaList = cacheData.getTemplateFormulaList()
                        .stream().filter(ds -> !formulaIDs.contains(ds.getID())
                                && !fieldIDs.contains(ds.getFieldID())).toList();
                if (CollUtil.isNotEmpty(otherFormulaList)) {
                    templateFormulaList.addAll(otherFormulaList);
                }
            }
            cacheData.setTemplateFormulaList(templateFormulaList);
        }
        if (CollUtil.isNotEmpty(scriptList)) {
            List<ScriptCacheData> templateScriptList = BeanUtil.copyToList(scriptList, ScriptCacheData.class);
            if (CollUtil.isNotEmpty(cacheData.getTemplateScriptList())) {
                List<Integer> scriptIDs = scriptList.stream().map(TbTemplateScript::getID).toList();
                List<ScriptCacheData> otherScriptList = cacheData.getTemplateScriptList()
                        .stream().filter(ds -> !scriptIDs.contains(ds.getID())).toList();
                if (CollUtil.isNotEmpty(otherScriptList)) {
                    templateScriptList.addAll(otherScriptList);
                }
            }
            cacheData.setTemplateScriptList(templateScriptList);
        }
        redisService.put(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY, templateID.toString(), JSONUtil.toJsonStr(cacheData));
    }

    @Override
    public List<TbParameter> queryParam(QueryParamParam pa) {
        QueryWrapper<TbParameter> queryWrapper = new QueryWrapper<TbParameter>().eq("subjectType", pa.getSubjectType()).in("subjectID", pa.getSubjectID());
        if (ObjectUtil.isNotEmpty(pa.getSubjectTokenList())) {
            queryWrapper.in("token", pa.getSubjectTokenList());
        }
        return tbParameterMapper.selectList(queryWrapper);
    }

    @Override
    public List<MonitorTypeFieldWithFormula> queryMonitorTypeFieldWithFormula(QueryMonitorTypeFieldWithFormulaParam pa) {
        return tbMonitorTypeFieldMapper.queryMonitorTypeFieldWithFormula(pa.getMonitorType(), pa.getTemplateID() == null ? -1 : pa.getTemplateID());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomizedMonitorType(UpdateCustomizedMonitorTypeParam pa) {
        TbMonitorType tbMonitorTypeNew = pa.update();
        tbMonitorTypeMapper.updateByPrimaryKey(
                tbMonitorTypeNew
        );

        // 更新缓存
        setMonitorTypeCache(tbMonitorTypeNew,
                tbMonitorTypeFieldMapper.queryByMonitorTypes(List.of(tbMonitorTypeNew.getMonitorType()), true
                )
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomizedMonitorTypeField(UpdateCustomizedMonitorTypeFieldParam pa) {
        tbMonitorTypeFieldMapper.updateBatch(
                pa.getFieldList()
        );
        setMonitorTypeCache(
                tbMonitorTypeMapper.queryByType(pa.getMonitorType()),
                tbMonitorTypeFieldMapper.queryByMonitorTypes(List.of(pa.getMonitorType()), true)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addMonitorTypeField(AddMonitorTypeFieldParam pa) {
        List<TbMonitorTypeField> list = Param2DBEntityUtil.buildTbMonitorTypeFieldList(pa.getFieldList(), pa.getMonitorType());
        tbMonitorTypeFieldMapper.insertBatch(list);
        setMonitorTypeCache(
                tbMonitorTypeMapper.queryByType(pa.getMonitorType()),
                tbMonitorTypeFieldMapper.queryByMonitorTypes(List.of(pa.getMonitorType()), true)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplateBatch(List<Integer> templateIDList) {
        // 需要删除模板，数据源，公式或脚本
        tbTemplateDataSourceMapper.deleteByTemplateIDList(templateIDList);
        tbTemplateFormulaMapper.deleteByTemplateIDList(templateIDList);
        tbTemplateScriptMapper.deleteByTemplateIDList(templateIDList);
        tbMonitorTypeTemplateMapper.deleteBatchIds(templateIDList);
        // 删除缓存
        redisService.remove(RedisKeys.MONITOR_TYPE_TEMPLATE_KEY, templateIDList.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorTypeBatch(List<Integer> monitorTypeList) {
        List<TbMonitorTypeTemplate> typeTemplates = tbMonitorTypeTemplateMapper.selectList(new QueryWrapper<TbMonitorTypeTemplate>().in("monitorType", monitorTypeList));
        if (ObjectUtil.isNotEmpty(typeTemplates)) {
            deleteTemplateBatch(typeTemplates.stream().map(TbMonitorTypeTemplate::getID).collect(Collectors.toList()));
        }
        tbMonitorTypeFieldMapper.deleteByMonitorTypeList(monitorTypeList);
        tbMonitorTypeMapper.deleteByMonitorTypeList(monitorTypeList);
        deleteMonitorTypeCache(monitorTypeList);
    }

    private void deleteMonitorTypeCache(List<Integer> monitorTypeList) {
        redisService.remove(RedisKeys.MONITOR_TYPE_KEY, monitorTypeList.stream().map(String::valueOf).collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMonitorTypeFieldBatch(Integer monitorType, List<Integer> fieldIDList) {
        tbMonitorTypeFieldMapper.deleteBatchIds(fieldIDList);
        setMonitorTypeCache(
                tbMonitorTypeMapper.queryByType(monitorType),
                tbMonitorTypeFieldMapper.queryByMonitorTypes(List.of(monitorType), true)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPredefinedMonitorType(AddPredefinedMonitorTypeParam pa, Integer usrID) {

        Integer type;
        if (pa.getMonitorType() != null) {
            type = pa.getMonitorType();
        } else {
            QueryWrapper<TbMonitorType> wrapper = new QueryWrapper<>();
            wrapper.between("monitorType", 1, 20000);
            wrapper.orderByDesc("monitorType").last("limit 1");
            TbMonitorType temp = tbMonitorTypeMapper.selectOne(wrapper);
            if (temp == null) {
                type = 1;
            } else {
                if (temp.getMonitorType() + 1 > 20000) {
                    throw new CustomBaseException(ResultCode.SERVER_EXCEPTION.toInt(), "预定义的监测类型已经用尽");
                } else {
                    type = temp.getMonitorType() + 1;
                }
            }
        }
        TbMonitorType tbMonitorType = Param2DBEntityUtil.fromAddPredefinedMonitorTypeParam2tbMonitorType(pa, usrID, type);
        tbMonitorTypeMapper.insert(tbMonitorType);
        List<TbMonitorTypeField> list = Param2DBEntityUtil.buildTbMonitorTypeFieldList(pa.getFieldList(), type);
        tbMonitorTypeFieldMapper.insertBatch(list);

        setMonitorTypeCache(tbMonitorType, list);
    }

    @Override
    public Object querySimpleMonitorTypeList(QuerySimpleMonitorTypeListParam pa) {

        QueryWrapper<TbMonitorType> qu = new QueryWrapper<>();
        qu.eq(pa.getCreateType() != null, "createType", pa.getCreateType());
        qu.orderByDesc("id");
        if (pa.getProjectID() != null) {
            List<TbMonitorItem> tbMonitorItems = tbMonitorItemMapper.selectList(
                    new QueryWrapper<TbMonitorItem>().lambda().eq(TbMonitorItem::getProjectID, pa.getProjectID())
            );
            if (CollectionUtils.isEmpty(tbMonitorItems)) {
                return null;
            } else {
                qu.in("monitorType", tbMonitorItems.stream().map(TbMonitorItem::getMonitorType).collect(Collectors.toList()));
            }
        }
        List<TbMonitorType> list = tbMonitorTypeMapper.selectList(qu);
        if (pa.getGrouped() != null && pa.getGrouped()) {
            if (CollectionUtils.isEmpty(list)) {
                return Map.of();
            }
            list.forEach(item -> {
                if (StringUtils.isBlank(item.getMonitorTypeClass())) {
                    item.setMonitorTypeClass("未定义");
                }
            });
            return list.stream().collect(Collectors.groupingBy(TbMonitorType::getMonitorTypeClass));
        }
        return list;
    }

    @Override
    public QueryFormulaParamsResult queryFormulaParams(QueryFormulaParamsRequest request) {
        QueryFormulaParamsResult result = new QueryFormulaParamsResult();
        TbMonitorTypeTemplate monitorTypeTemplate = request.getMonitorTypeTemplate();
        List<TbTemplateDataSource> templateDataSourceList = tbTemplateDataSourceMapper
                .selectList(new LambdaQueryWrapper<TbTemplateDataSource>()
                        .eq(TbTemplateDataSource::getTemplateDataSourceID, monitorTypeTemplate.getTemplateDataSourceID()));
        if (!CollUtil.isEmpty(templateDataSourceList)) {
            templateDataSourceList.stream().collect(Collectors.groupingBy(TbTemplateDataSource::getDataSourceType))
                    .forEach((type, list) -> {
                        List<String> sourceTokenList = list.stream()
                                .map(TbTemplateDataSource::getTemplateDataSourceToken).collect(Collectors.toList());
                        switch (DataSourceType.codeOf(type)) {
                            case IOT_SENSOR:
                                Set<Object> iotModelTokens = list.stream()
                                        .map(s -> StrUtil.subBefore(s.getTemplateDataSourceToken(), StrUtil.UNDERLINE, false))
                                        .map(e -> (Object) e)
                                        .collect(Collectors.toSet());
                                RedisService redisService = ContextHolder.getBean(RedisConstant.IOT_REDIS_SERVICE);
                                List<Model> models = redisService.multiGet(RedisKeys.IOT_MODEL_KEY, iotModelTokens, Model.class);
                                Map<String, Object> modelMap = sourceTokenList.stream()
                                        .collect(Collectors.toMap(s -> s, s -> {
                                            String mt = StrUtil.subBefore(s, StrUtil.UNDERLINE, false);
                                            Model model = models.stream()
                                                    .filter(m -> m.getModelToken().equals(mt)).findFirst().orElse(null);
                                            if (model == null || CollUtil.isEmpty(model.getModelFieldList())) {
                                                return CollUtil.newArrayList();
                                            }
                                            return model.getModelFieldList().stream()
                                                    .map(Model.Field::getFieldToken)
                                                    .collect(Collectors.toMap(k ->
                                                            DefaultConstant.MONITOR_TEMPLATE_PARAM_NAME, CollUtil::newArrayList));
                                        }));
                                modelMap.put(DefaultConstant.MONITOR_TEMPLATE_PARAM_NAME, sourceTokenList);
                                result.setIot(modelMap);
                                break;
                            case MONITOR_SENSOR:
                                List<Integer> monitorTypes = list.stream()
                                        .map(s -> Integer.valueOf(StrUtil.subBefore(s.getTemplateDataSourceToken(),
                                                StrUtil.UNDERLINE, false)))
                                        .toList();
                                List<TbMonitorTypeField> typeFields = tbMonitorTypeFieldMapper
                                        .queryByMonitorTypes(monitorTypes, false);
                                Map<String, Object> typeChildMap = sourceTokenList.stream()
                                        .collect(Collectors.toMap(s -> s, s -> {
                                            Integer mt = Integer.valueOf(StrUtil.subBefore(s, StrUtil.UNDERLINE, false));
                                            return typeFields.stream()
                                                    .filter(f -> f.getMonitorType().equals(mt))
                                                    .map(TbMonitorTypeField::getFieldToken)
                                                    .collect(Collectors.toMap(k ->
                                                            DefaultConstant.MONITOR_TEMPLATE_PARAM_NAME, CollUtil::newArrayList));
                                        }));
                                typeChildMap.put(DefaultConstant.MONITOR_TEMPLATE_PARAM_NAME, sourceTokenList);
                                result.setMon(typeChildMap);
                                break;
                            default:
                                break;
                        }
                    });
        }
        List<String> exList = new LinkedList<>();
        List<String> selfList = new LinkedList<>();
        tbMonitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                        .eq(TbMonitorTypeField::getMonitorType, monitorTypeTemplate.getMonitorType())).stream()
                .collect(Collectors.groupingBy(TbMonitorTypeField::getFieldClass))
                .forEach((fc, list) -> {
                    switch (FieldClass.codeOf(fc)) {
                        case BASIC:
                        case EXTEND:
                            selfList.addAll(list.stream().map(TbMonitorTypeField::getFieldToken).toList());
                            break;
                        case EXTEND_CONFIG:
                            exList.addAll(list.stream().map(TbMonitorTypeField::getFieldToken).toList());
                            break;
                        default:
                            break;
                    }
                });
        result.setExList(exList);
        result.setSelfList(selfList);
        List<String> paramList = tbParameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                .eq(TbParameter::getSubjectType, ParameterSubjectType.TEMPLATE.getCode())
                .in(TbParameter::getSubjectID, request.getTemplateID())).stream().map(TbParameter::getName).collect(Collectors.toList());
        result.setParamList(paramList);
        return result;
    }
}
