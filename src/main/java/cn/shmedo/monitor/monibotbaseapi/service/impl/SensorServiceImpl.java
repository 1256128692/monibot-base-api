package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.ParameterSubjectType;
import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.DataSourceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Field;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.IdRecord;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Param;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceAndSensorRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.service.SensorService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.FormulaUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Chengfs on 2023/4/4
 */
@Service
public class SensorServiceImpl extends ServiceImpl<TbSensorMapper, TbSensor> implements SensorService {

    @Resource
    private IotService iotService;
    @Resource
    private TbMonitorTypeTemplateMapper monitorTypeTemplateMapper;
    @Resource
    private TbMonitorTypeMapper monitorTypeMapper;
    @Resource
    private TbParameterMapper parameterMapper;
    @Resource
    private TbSensorDataSourceMapper sensorDataSourceMapper;
    private TbMonitorTypeFieldMapper monitorTypeFieldMapper;
    @Resource
    private TbTemplateScriptMapper templateScriptMapper;
    @Resource
    private TbTemplateFormulaMapper templateFormulaMapper;
    @Resource
    private RedisService iotRedisService;
    @Resource
    private RedisService monitorRedisService;
    @Resource
    private FileService fileService;


    @Override
    public PageUtil.Page<SensorListResponse> sensorPage(SensorPageRequest request) {
        Page<SensorListResponse> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        IPage<SensorListResponse> pageData = this.baseMapper.selectSensorPage(page, request);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public List<DataSourceCatalogResponse> dataSourceCatalog(DataSourceCatalogRequest request) {
        List<DataSourceCatalogResponse> result = monitorTypeTemplateMapper.dataSourceCatalog(request);

        Map<String, List<DeviceWithSensor>> iotMap = getIotMap(() -> result.stream()
                .flatMap(e -> e.getDataSourceList().stream())
                .filter(e -> DataSourceType.IOT_SENSOR.getCode() == e.getDataSourceType())
                .map(e -> StrUtil.subBefore(e.getTemplateDataSourceToken(), StrUtil.UNDERLINE, false))
                .filter(Objects::nonNull).collect(Collectors.toSet()), request.getKeyword());

        Map<String, List<TbSensor>> monitorMap = getMonitorMap(() -> result.stream()
                .flatMap(e -> e.getDataSourceList().stream())
                .filter(e -> DataSourceType.MONITOR_SENSOR.getCode() == e.getDataSourceType())
                .map(DataSourceCatalogResponse.DataSource::getTemplateDataSourceToken).collect(Collectors.toSet()));

        return result.stream().peek(item -> item.getDataSourceList().forEach(e -> {
                    String token = StrUtil.subBefore(e.getTemplateDataSourceToken(),
                            StrUtil.UNDERLINE, false);
                    if (DataSourceType.IOT_SENSOR.getCode() == e.getDataSourceType()) {

                        List<DeviceWithSensor> childList = new ArrayList<>();
                        iotMap.getOrDefault(token, List.of()).forEach(device -> {
                            DeviceWithSensor data = new DeviceWithSensor();
                            data.setID(device.getID());
                            data.setDeviceName(device.getDeviceName());
                            data.setDeviceToken(device.getDeviceToken());
                            data.setUniqueToken(device.getUniqueToken());
                            data.setProductID(device.getProductID());
                            data.setSensorList(device.getSensorList().stream()
                                    .filter(sensor -> sensor.iotSensorType().equals(token)).toList());
                            childList.add(data);
                        });
                        e.setChildList(childList);
                    } else if (DataSourceType.MONITOR_SENSOR.getCode() == e.getDataSourceType()) {
                        e.setChildList(monitorMap.getOrDefault(token, List.of()));
                    }
                }))
                .filter(item -> item.getDataSourceList().stream()
                        .noneMatch(e -> e.getChildList().isEmpty())).collect(Collectors.toList());

    }

    @Override
    public List<MonitorTypeCatalogResponse> monitorTypeCatalog(MonitorTypeCatalogRequest request) {
        LambdaQueryWrapper<TbMonitorTypeTemplate> wrapper = new LambdaQueryWrapper<TbMonitorTypeTemplate>()
                .eq(TbMonitorTypeTemplate::getDataSourceComposeType, request.getDataSourceComposeType());
        if (StrUtil.isNotBlank(request.getTemplateDataSourceID())) {
            wrapper.eq(TbMonitorTypeTemplate::getTemplateDataSourceID, request.getTemplateDataSourceID());
        }
        Set<Integer> monitorTypes = monitorTypeTemplateMapper.selectList(wrapper)
                .stream().map(TbMonitorTypeTemplate::getMonitorType).collect(Collectors.toSet());

        if (!monitorTypes.isEmpty()) {
            return monitorTypeMapper.selectList(new LambdaQueryWrapper<TbMonitorType>()
                            .in(TbMonitorType::getMonitorType, monitorTypes)).stream()
                    .map(MonitorTypeCatalogResponse::valueOf).toList();
        }
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRecord addSensor(SaveSensorRequest request) {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        //传感器
        TbSensor sensor = new TbSensor();
        sensor.setProjectID(request.getProjectID());
        sensor.setTemplateID(request.getTemplateID());
        sensor.setDataSourceID(UUID.randomUUID().toString());
        sensor.setDataSourceComposeType(request.getDataSourceComposeType().getCode());
        sensor.setMonitorType(request.getMonitorType());
        sensor.setName(genSensorName(request.getMonitorType(), request.getProjectID()));
        sensor.setAlias(StrUtil.isBlank(request.getAlias()) ? sensor.getName() : request.getAlias());
        sensor.setKind(ByteUtil.intToByte(3));
        sensor.setConfigFieldValue(request.getConfigFieldValue());
        sensor.setDisplayOrder(0);
        sensor.setCreateUserID(subject.getSubjectID());
        sensor.setUpdateUserID(subject.getSubjectID());
        if (StrUtil.isNotBlank(request.getImagePath())) {
            sensor.setImagePath(fileService.base64Upload(request.getImagePath()));
        }
        baseMapper.insert(sensor);

        //传感器数据源
        if (CollUtil.isNotEmpty(request.getDataSourceList())) {
            List<TbSensorDataSource> sensorDataSources = request.getDataSourceList().stream().map(source -> {
                TbSensorDataSource dataSource = new TbSensorDataSource();
                dataSource.setDataSourceID(sensor.getDataSourceID());
                dataSource.setDataSourceType(source.getDataSourceType().getCode());
                dataSource.setTemplateDataSourceToken(source.getTemplateDataSourceToken());
                dataSource.setDataSourceToken(DataSourceType.IOT_SENSOR.equals(source.getDataSourceType()) ?
                        source.getUniqueToken() + StrUtil.AT + source.getSensorName() : source.getSensorName());
                dataSource.setDataSourceComposeType(request.getDataSourceComposeType().getCode());
                dataSource.setExValues(source.getExValues());
                return dataSource;
            }).toList();
            sensorDataSourceMapper.insertBatchSomeColumn(sensorDataSources);
        }

        //传感器参数
        if (!request.getParameterList().isEmpty()) {
            request.getParameterList().forEach(item -> item.setSubjectID(sensor.getID()));
            parameterMapper.insertBatch(request.getParameterList());
            monitorRedisService.putAll(RedisKeys.PARAMETER_PREFIX_KEY + ParameterSubjectType.SENSOR.getCode(),
                    request.getParameterList().stream().collect(Collectors.groupingBy(TbParameter::getSubjectID)));
        }
        return new IdRecord(sensor.getID());
    }

    @Override
    public SensorInfoResponse sensorInfo(SensorInfoRequest request) {
        TbSensor sensor = baseMapper.selectById(request.getSensorID());
        Assert.notNull(sensor, "传感器不存在");

        SensorInfoResponse response = SensorInfoResponse.valueOf(sensor);
        //图片
        response.setImagePath(fileService.getFileUrl(sensor.getImagePath()));
        //扩展配置
        List<TbMonitorTypeField> typeFields = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                .eq(TbMonitorTypeField::getMonitorType, response.getMonitorType())
                .eq(TbMonitorTypeField::getFieldClass, FieldClass.EXTEND_CONFIG.getCode()));
        Dict exConfig = JSONUtil.isTypeJSON(response.getConfigFieldValue()) ?
                JSONUtil.toBean(response.getConfigFieldValue(), Dict.class) : Dict.create();
        response.setExFields(typeFields.stream().map(e -> {
                    SensorInfoResponse.ExField exField = SensorInfoResponse.ExField.valueOf(e);
                    exField.setValue(exConfig.getStr(e.getFieldName()));
                    return exField;
                }).toList());
        //参数
        Map<Integer, Map<String, TbParameter>> paramMap = parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                .or(wrapper -> wrapper
                        .eq(TbParameter::getSubjectType, ParameterSubjectType.SENSOR.getCode())
                        .eq(TbParameter::getSubjectID, response.getID()))
                .or(wrapper -> {
                    wrapper.eq(TbParameter::getSubjectType, ParameterSubjectType.TEMPLATE.getCode())
                            .eq(TbParameter::getSubjectID, response.getTemplateID());
                })).stream().collect(Collectors.groupingBy(TbParameter::getSubjectType,
                Collectors.toMap(TbParameter::getToken, e -> e)));
        if (paramMap.containsKey(ParameterSubjectType.TEMPLATE.getCode())) {
            Map<String, TbParameter> sensorParamMap = paramMap.getOrDefault(ParameterSubjectType.SENSOR.getCode(), Map.of());
            response.setParamFields(paramMap.get(ParameterSubjectType.TEMPLATE.getCode()).values().stream()
                    .peek(p -> p.setPaValue(sensorParamMap.getOrDefault(p.getToken(), p).getPaValue())).toList());
        }
        //数据源
        response.setDataSourceList(sensorDataSourceMapper
                .selectList(new LambdaQueryWrapper<TbSensorDataSource>()
                        .eq(TbSensorDataSource::getDataSourceID, response.getDataSourceID())));
        response.setMonitorTypeName(MonitorTypeCache.monitorTypeMap.get(response.getMonitorType()).getTypeName());
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSensor(DeleteSensorRequest request) {
        //删除传感器数据源
        Set<String> dataSourceIds = request.getSensorList().stream()
                .map(TbSensor::getDataSourceID).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(dataSourceIds)) {
            sensorDataSourceMapper.delete(new LambdaQueryWrapper<TbSensorDataSource>()
                    .in(TbSensorDataSource::getDataSourceID, dataSourceIds));
        }
        //删除传感器参数
        parameterMapper.delete(new LambdaQueryWrapper<TbParameter>()
                .eq(TbParameter::getSubjectType, ParameterSubjectType.SENSOR.getCode())
                .in(TbParameter::getSubjectID, request.getSensorIDList()));
        monitorRedisService.remove(RedisKeys.PARAMETER_PREFIX_KEY + ParameterSubjectType.SENSOR.getCode(),
                request.getSensorIDList().stream().map(String::valueOf).toArray(String[]::new));
        //删除传感器
        removeBatchByIds(request.getSensorIDList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRecord updateSensor(UpdateSensorRequest request) {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        Optional.ofNullable(request.getEnable()).ifPresent(enable -> request.getSensor().setEnable(enable));
        Optional.ofNullable(request.getDisplayOrder())
                .ifPresent(displayOrder -> request.getSensor().setDisplayOrder(displayOrder));
        if (StrUtil.isNotBlank(request.getExValues())) {
            request.getSensor().setExValues(request.getExValues());
        }
        if (StrUtil.isNotBlank(request.getImagePath())) {
            request.getSensor().setImagePath(fileService.base64Upload(request.getImagePath()));
        }
        if (StrUtil.isNotBlank(request.getAlias()) && !request.getSensor().getAlias().equals(request.getAlias())) {
            //校验别名是否重复
            Long count = baseMapper.selectCount(new LambdaQueryWrapper<TbSensor>()
                    .eq(TbSensor::getAlias, request.getAlias())
                    .ne(TbSensor::getProjectID, request.getProjectID()));
            Assert.isTrue( count == null || count == 0, "名称已存在");
            request.getSensor().setAlias(request.getAlias());
        }
        request.getSensor().setUpdateUserID(subject.getSubjectID());
        request.getSensor().setUpdateTime(null);
        //更新传感器、参数
        updateById(request.getSensor());
        if (CollUtil.isNotEmpty(request.getParamList())) {
            parameterMapper.replaceBatch(request.getParamList());
        }
        return new IdRecord(request.getSensor().getID());
    }

    @Override
    public TryingParamResponse getTryingParam(QueryTryingParamRequest request) {
        TryingParamResponse result = new TryingParamResponse();
        result.setCalType(request.getMonitorTypeTemplate().getCalType());
        switch (CalType.codeOf(result.getCalType())) {
            case FORMULA:
                Map<Integer, TbTemplateFormula> formulaMap = templateFormulaMapper
                        .selectList(new LambdaQueryWrapper<TbTemplateFormula>()
                                .eq(TbTemplateFormula::getTemplateID, request.getTemplateID())
                                .eq(TbTemplateFormula::getMonitorType, request.getMonitorType()))
                        .stream().collect(Collectors.toMap(TbTemplateFormula::getFieldID, e -> e));
                result.setFieldList(request.getTypeFields().stream()
                        .filter(e -> formulaMap.containsKey(e.getID())).map(typeField -> {
                            Field field = Field.valueOf(typeField);
                            field.setDisplayFormula(formulaMap.get(typeField.getID()).getDisplayFormula());
                            field.setFormula(formulaMap.get(typeField.getID()).getFormula());
                            return field;
                        }).toList());
                break;
            case SCRIPT:
                TbTemplateScript tbTemplateScript = templateScriptMapper.selectOne(new LambdaQueryWrapper<TbTemplateScript>()
                        .eq(TbTemplateScript::getTemplateID, request.getTemplateID())
                        .eq(TbTemplateScript::getMonitorType, request.getMonitorType())
                        .select(TbTemplateScript::getScript));
                result.setScript(tbTemplateScript.getScript());
                break;
            case HTTP: //TODO HTTP计算未实现
                break;
            default:
                break;
        }

        Map<FormulaUtil.DataType, Set<FormulaUtil.Source>> sourceMap = result.getFieldList().stream()
                .map(Field::getFormula)
                .flatMap(e -> FormulaUtil.parse(e).entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> {
                    a.addAll(b);
                    return a;
                }));

        Set<Object> modelTokens = sourceMap.getOrDefault(FormulaUtil.DataType.IOT, Collections.emptySet()).stream()
                .map(e -> StrUtil.subBefore(e.getSourceToken(), StrUtil.UNDERLINE, false))
                .map(e -> (Object) e)
                .collect(Collectors.toSet());
        Map<String, Map<String, Model.Field>> modelMap = iotRedisService.multiGet(RedisKeys.IOT_MODEL_KEY, modelTokens, Model.class)
                .stream().collect(Collectors.toMap(Model::getModelToken,
                        e -> e.getModelFieldList().stream().collect(Collectors.toMap(Model.Field::getFieldToken, f -> f))));
        List<Param> paramList = sourceMap.entrySet().stream().flatMap(entry -> {
            switch (entry.getKey()) {
                case IOT -> {
                    return entry.getValue().stream().map(e -> {
                        String modelToken = StrUtil.subBefore(e.getSourceToken(), StrUtil.UNDERLINE, false);
                        Assert.isTrue(modelMap.containsKey(modelToken), "模型{}不存在", modelToken);
                        Assert.isTrue(modelMap.get(modelToken).containsKey(e.getFieldToken()),
                                "模型{}不存在字段 {}", modelToken, e.getFieldToken());
                        return Param.valueOf(modelMap.get(modelToken).get(e.getFieldToken()),
                                e.getOrigin(), entry.getKey());
                    });
                }
                case PARAM -> {
                    List<String> paramTokens = entry.getValue().stream().map(FormulaUtil.Source::getFieldToken).toList();
                    Map<String, TbParameter> paramMap = parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                                    .eq(TbParameter::getSubjectType, ParameterSubjectType.TEMPLATE.getCode())
                                    .eq(TbParameter::getSubjectID, request.getTemplateID())
                                    .in(TbParameter::getToken, paramTokens))
                            .stream().collect(Collectors.toMap(TbParameter::getToken, e -> e));
                    return entry.getValue().stream().map(e -> {
                        Assert.isTrue(paramMap.containsKey(e.getFieldToken()),
                                "模板参数{}找不到", e.getFieldToken());
                        return Param.valueOf(paramMap.get(e.getFieldToken()),
                                e.getOrigin(), entry.getKey());
                    });
                }
                case EX -> {
                    List<String> exTokens = entry.getValue().stream().map(FormulaUtil.Source::getFieldToken).toList();
                    Map<String, TbMonitorTypeField> exMap = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                            .eq(TbMonitorTypeField::getMonitorType, request.getMonitorType())
                            .eq(TbMonitorTypeField::getFieldClass, FieldClass.EXTEND_CONFIG.getCode())
                            .in(TbMonitorTypeField::getFieldToken, exTokens)
                            .select(TbMonitorTypeField::getID, TbMonitorTypeField::getFieldName)
                    ).stream().collect(Collectors.toMap(TbMonitorTypeField::getFieldToken, e -> e));
                    return entry.getValue().stream().map(e -> {
                        Assert.isTrue(exMap.containsKey(e.getFieldToken()),
                                "扩展字段{}找不到", e.getFieldToken());
                        return Param.valueOf(exMap.get(e.getFieldToken()), e.getOrigin(), entry.getKey());
                    });
                }
                default -> {
                    //TODO SELF、MON、HISTORY 待实现
                    return Stream.empty();
                }
            }
        }).toList();
        result.setParamList(paramList);
        return result;
    }

    @Override
    public Object trying(TryingRequest request) {
        switch (request.getCalType()) {
            case FORMULA:
                Dict fieldValueMap = Dict.create();
                return request.getFieldList().stream().map(f -> {
                    Object result = FormulaUtil.calculate(f.getFormula(), dataTypeSetMap ->
                            dataTypeSetMap.forEach((type, sources) ->
                                    sources.forEach(source ->
                                            source.setFieldValue(FormulaUtil.DataType.SELF.equals(type) ?
                                                    fieldValueMap.get(source.getFieldToken()) :
                                                    request.getParamMap().get(source.getOrigin())))));
                    //每计算一个字段，将其结果缓存，供后续公式使用
                    fieldValueMap.set(f.getFieldToken(), result);
                    return new TryingResponse(result, f.getFieldToken());
                }).toList();
            case SCRIPT:
                //TODO 脚本计算未实现
                break;
            case HTTP:
                //TODO HTTP计算未实现
                break;
        }
        return List.of();
    }

    @Override
    public BaseConfigResponse baseConfig(BaseConfigRequest request) {
        List<TbParameter> parameterList = parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                .eq(TbParameter::getSubjectType, ParameterSubjectType.TEMPLATE.getCode())
                .eq(TbParameter::getSubjectID, request.getTemplateID()));
        QueryWrapper<Void> wrapper = new QueryWrapper<>();
//            wrapper.in("tmtf.FieldClass", MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass());
        wrapper.eq("tmt.MonitorType", request.getMonitorType());
        List<BaseConfigResponse> list = monitorTypeMapper.queryMonitorTypeWithField(wrapper).stream().map(e -> {
            BaseConfigResponse item = new BaseConfigResponse();
            item.setExFields(e.getFieldList().stream().filter(f -> FieldClass.EXTEND_CONFIG
                    .getCode() == f.getFieldClass()).collect(Collectors.toList()));
            item.setParamFields(parameterList);
            return item;
        }).toList();
        return CollUtil.getFirst(list);
    }

    @Override
    public List<SensorListResponse> sensorList(SensorListRequest request) {
        return this.baseMapper.selectSensorList(request);
    }

    @Override
    public List<DataSourceWithSensor> querySensorDataSource(SourceWithSensorRequest request) {
        return sensorDataSourceMapper.selectDataSourceWithSensor(request);
    }

    @Override
    public void updateSensorStatusAndMonitorBeginTime(UpdateSensorStatusRequest request) {
        TbSensor sensor = new TbSensor();
        sensor.setID(request.getSensorID());
        Optional.ofNullable(request.getSensorStatus()).ifPresent(e -> sensor.setStatus(e.byteValue()));
        Optional.ofNullable(request.getMonitorBeginTime()).ifPresent(sensor::setMonitorBeginTime);
        this.updateById(sensor);
    }

    /**
     * 根据物联网传感器类型获取设备和传感器信息
     *
     * @param consumer    传感器类型
     * @param searchToken 搜索设备token
     * @return key:iotSensorType value: {@link DeviceWithSensor}
     */
    private Map<String, List<DeviceWithSensor>> getIotMap(Supplier<Set<String>> consumer, String searchToken) {
        Set<String> iotSensorTypes = consumer.get();
        Map<String, List<DeviceWithSensor>> iotMap = new HashMap<>();
        if (!iotSensorTypes.isEmpty()) {
            QueryDeviceAndSensorRequest request = new QueryDeviceAndSensorRequest();
            request.setCompanyID(CurrentSubjectHolder.getCurrentSubject().getCompanyID());
            request.setIotSensorTypeList(iotSensorTypes);
            if (searchToken != null) {
                request.setDeviceTokenList(List.of(searchToken));
            }
            ResultWrapper<List<DeviceWithSensor>> wrapper = iotService.queryDeviceAndSensorList(request);
            if (wrapper.apiSuccess()) {
                List<DeviceWithSensor> data = wrapper.getData();
                iotSensorTypes.forEach(iotSensorType -> {
                    if (data.stream().anyMatch(e -> e.getSensorList().stream()
                            .anyMatch(sensor -> sensor.iotSensorType().equals(iotSensorType)))) {
                        iotMap.put(iotSensorType, data.stream().filter(e -> e.getSensorList().stream()
                                .anyMatch(sensor -> sensor.iotSensorType().equals(iotSensorType))).collect(Collectors.toList()));
                    }
                });
            }
        }
        return iotMap;
    }

    /**
     * 根据传感器名称获取监测传感器信息
     *
     * @param consumer 传感器名称
     * @return 监测传感器信息
     */
    private Map<String, List<TbSensor>> getMonitorMap(Supplier<Set<String>> consumer) {
        Set<String> monitorSensorNames = consumer.get();
        if (!monitorSensorNames.isEmpty()) {
            LambdaQueryWrapper<TbSensor> wrapper = new LambdaQueryWrapper<TbSensor>()
                    .eq(TbSensor::getName, monitorSensorNames)
                    .select(TbSensor::getName, TbSensor::getID, TbSensor::getAlias);
            return this.baseMapper.selectList(wrapper).stream().collect(Collectors.groupingBy(TbSensor::getName));
        }
        return Collections.emptyMap();
    }

    /**
     * 加锁生成传感器名称
     *
     * @param monitorType 监测类型
     * @param projectID   项目ID
     * @return 传感器名称
     */
    private synchronized String genSensorName(Integer monitorType, Integer projectID) {
        Integer number = this.baseMapper.getNameSerialNumber(projectID, monitorType);
        return monitorType + StrUtil.UNDERLINE + number;
    }
}

    
    