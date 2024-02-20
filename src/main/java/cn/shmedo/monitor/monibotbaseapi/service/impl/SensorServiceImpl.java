package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.CurrentSubject;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.monitor.enums.CalType;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.ParameterSubjectType;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.constants.RedisKeys;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.cache.FormulaCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.cache.MonitorTypeCacheData;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Model;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SendType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorKindEnum;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceAndSensorRequest;
import cn.shmedo.monitor.monibotbaseapi.model.param.video.VideoDeviceInfoV5;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.service.SensorService;
import cn.shmedo.monitor.monibotbaseapi.service.file.FileService;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaData;
import cn.shmedo.monitor.monibotbaseapi.util.formula.FormulaUtil;
import cn.shmedo.monitor.monibotbaseapi.util.formula.Origin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
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
    @Resource
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

    @Resource
    private TbVideoDeviceMapper videoDeviceMapper;

    @Resource
    private TbVideoDeviceSourceMapper videoDeviceSourceMapper;

    @Resource
    private ApplicationEventPublisher publisher;


    @Override
    public PageUtil.Page<SensorListResponse> sensorPage(SensorPageRequest request) {
        Page<SensorListResponse> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        IPage<SensorListResponse> pageData = this.baseMapper.selectSensorPage(page, request);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public List<DataSourceCatalogResponse> dataSourceCatalog(DataSourceCatalogRequest request) {
        List<DataSourceCatalogResponse> result = monitorTypeTemplateMapper.dataSourceCatalog(request).stream()
                .filter(e -> {
                    //过滤掉配置不全的模板
                    if (CalType.FORMULA.getCode() == e.getCalType()) {
                        return ObjUtil.equals(e.getFieldCount(), e.getFormulaCount());
                    } else if (CalType.SCRIPT.getCode() == e.getCalType()) {
                        return ObjUtil.equals(e.getFieldCount(), e.getScriptCount());
                    }
                    return true;
                }).toList();

        Map<String, List<DeviceWithSensor>> iotMap = getIotMap(result, request);
        Map<String, List<TbSensor>> monitorMap = getMonitorMap(() -> result.stream()
                .flatMap(e -> e.getDataSourceList().stream())
                .filter(e -> DataSourceType.MONITOR_SENSOR.getCode() == e.getDataSourceType())
                .map(DataSourceCatalogResponse.DataSource::getTemplateDataSourceToken).collect(Collectors.toSet()));

        return result.stream().peek(item -> item.getDataSourceList().forEach(e -> {
                    String token = StrUtil.subBefore(e.getTemplateDataSourceToken(), StrUtil.UNDERLINE, false);
                    if (DataSourceType.IOT_SENSOR.getCode() == e.getDataSourceType()) {
                        List<DeviceWithSensor> childList = iotMap.getOrDefault(token, List.of())
                                .stream().peek(device -> device.setSensorList(device.getSensorList().stream()
                                        .filter(sensor -> sensor.iotSensorType().equals(token)).toList()))
                                .filter(device -> !device.getSensorList().isEmpty())
                                .toList();
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
        LambdaQueryWrapper<TbMonitorTypeTemplate> wrapper = Wrappers.lambdaQuery(TbMonitorTypeTemplate.class)
                .eq(TbMonitorTypeTemplate::getDataSourceComposeType, request.getDataSourceComposeType());
        if (StrUtil.isNotBlank(request.getTemplateDataSourceID())) {
            wrapper.eq(TbMonitorTypeTemplate::getTemplateDataSourceID, request.getTemplateDataSourceID());
        }
        Set<Integer> monitorTypes = monitorTypeTemplateMapper.selectList(wrapper)
                .stream().map(TbMonitorTypeTemplate::getMonitorType).collect(Collectors.toSet());

        if (!monitorTypes.isEmpty()) {
            return monitorTypeMapper.selectList(Wrappers.lambdaQuery(TbMonitorType.class)
                            .in(TbMonitorType::getMonitorType, monitorTypes)
                            .in(TbMonitorType::getCompanyID, List.of(-1, request.getCompanyID())))
                    .stream().map(MonitorTypeCatalogResponse::valueOf).toList();
        }
        return List.of();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IdRecord addSensor(SaveSensorRequest request) {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        boolean isManual = request.getManual();
        // 中台传感器名称
        String mdmbaseSensorName = (isManual ? "manual_" : "") + genSensorName(request.getMonitorType(), request.getProjectID());

        //传感器
        TbSensor sensor = new TbSensor();
        sensor.setProjectID(request.getProjectID());
        sensor.setTemplateID(request.getTemplateID());
        sensor.setDataSourceID(UUID.randomUUID().toString());
        sensor.setDataSourceComposeType(request.getDataSourceComposeType().getCode());
        sensor.setMonitorType(request.getMonitorType());
        sensor.setName(mdmbaseSensorName);
        sensor.setAlias(StrUtil.isBlank(request.getAlias()) ? sensor.getName() : request.getAlias());
        sensor.setKind(isManual ? SensorKindEnum.MANUAL_KIND.getCode() : SensorKindEnum.AUTO_KIND.getCode());
        sensor.setConfigFieldValue(request.getConfigFieldValue());
        sensor.setDisplayOrder(0);
        sensor.setOnlineStatus(isManual ? 1 : null);
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
                // 如果是人工：
                // - {@code templateDataSourceToken}不允许用户选择，直接使用manual_2_a；
                // - {@code dataSourceToken}需要等于中台传感器名称（即{@code mdmbaseSensorName}），因为人工传感器没有对应的物联网平台传感器
                dataSource.setTemplateDataSourceToken(isManual ? "manual_2_a" : source.getTemplateDataSourceToken());
                String dataSourceToken;
                if (DataSourceType.IOT_SENSOR.equals(source.getDataSourceType())) {
                    dataSourceToken = isManual ? mdmbaseSensorName : source.getUniqueToken() + StrUtil.AT + source.getSensorName();
                } else {
                    dataSourceToken = source.getSensorName();
                }
                dataSource.setDataSourceToken(dataSourceToken);
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

        VideoDeviceInfoV5 videoDevice = null;
        if (sensor.getVideoDeviceSourceID() != null) {
            videoDevice = videoDeviceMapper.selectByVideoDeviceSourceID(sensor.getVideoDeviceSourceID());
            TbVideoDeviceSource videoDeviceSource = videoDeviceSourceMapper.selectByPrimaryKey(sensor.getVideoDeviceSourceID());
            if (videoDeviceSource != null) {
                videoDevice.setChannelCode(videoDeviceSource.getChannelNo());
            }
        }

        SensorInfoResponse response = SensorInfoResponse.valueOf(sensor, videoDevice);
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
            exField.setValue(exConfig.getStr(e.getFieldToken()));
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
        // 处理传感器相关数据
        publisher.publishEvent(new DeleteSensorEventDto(this, request.getSensorIDList()));
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
            Assert.isTrue(count == null || count == 0, "名称已存在");
            request.getSensor().setAlias(request.getAlias());
        }
        request.getSensor().setUpdateUserID(subject.getSubjectID());
        request.getSensor().setUpdateTime(null);
        //更新传感器、参数
        updateById(request.getSensor());
        if (CollUtil.isNotEmpty(request.getParamList())) {
            parameterMapper.replaceBatch(request.getParamList());
            monitorRedisService.putAll(RedisKeys.PARAMETER_PREFIX_KEY + ParameterSubjectType.SENSOR.getCode(),
                    parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                                    .eq(TbParameter::getSubjectType, ParameterSubjectType.SENSOR.getCode())
                                    .eq(TbParameter::getSubjectID, request.getSensor().getID()))
                            .stream().collect(Collectors.groupingBy(TbParameter::getSubjectID)));
        }
        return new IdRecord(request.getSensor().getID());
    }

    @Override
    public TryingParamResponse getTryingParam(QueryTryingParamRequest request) {
        TryingParamResponse result = new TryingParamResponse();
        result.setCalType(request.getTypeTemplateCache().getCalType().getCode());

        Map<Integer, FormulaCacheData> formulaMap = request.getTypeTemplateCache().getTemplateFormulaList()
                .stream().collect(Collectors.toMap(FormulaCacheData::getFieldID, e -> e));
        Map<FieldClass, Map<String, MonitorTypeCacheData.Field>> fieldClassGroup = request.getMonitorTypeCache()
                .getMonitortypeFieldList().stream()
                .collect(Collectors.groupingBy(MonitorTypeCacheData.Field::getFieldClass,
                        Collectors.toMap(MonitorTypeCacheData.Field::getFieldToken, e -> e)));
        //字段
        result.setFieldList(fieldClassGroup.entrySet().stream()
                .filter(e -> !FieldClass.EXTEND_CONFIG.equals(e.getKey()))
                .flatMap(e -> e.getValue().values().stream())
                .map(e -> Field.valueOf(e, formulaMap.get(e.getID())))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(e -> e.getDisplayOrder() != null ? e.getDisplayOrder() : Integer.MAX_VALUE))
                .toList());
        //脚本
        Optional.ofNullable(request.getTypeTemplateCache().getTemplateScriptList())
                .filter(e -> !e.isEmpty())
                .ifPresent(scripts -> result.setScript(CollUtil.getFirst(scripts).getScript()));
        //参数
        Map<Origin.Type, Set<FormulaData>> sourceMap = result.getFieldList().stream()
                .flatMap(e -> FormulaUtil.parse(e.getFormula()).stream())
                .collect(Collectors.toMap(FormulaData::getType, CollUtil::newHashSet, (a, b) -> {
                    a.addAll(b);
                    return a;
                }));
        List<Param> paramList = sourceMap.entrySet().stream().flatMap(entry -> {
            switch (entry.getKey()) {
                case IOT -> {
                    Set<Object> tokens = entry.getValue().stream().map(e -> StrUtil.subBefore(e.getSourceToken(),
                            StrUtil.UNDERLINE, false)).map(e -> (Object) e).collect(Collectors.toSet());
                    Map<String, Model> modelMap = iotRedisService.multiGet(RedisKeys.IOT_MODEL_KEY, tokens, Model.class)
                            .stream().collect(Collectors.toMap(Model::getModelToken, e -> e));
                    return entry.getValue().stream().map(e -> Param.valueOf(modelMap, e));
                }
                case PARAM -> {
                    Map<String, TbParameter> paramMap = monitorRedisService.getList(
                                    RedisKeys.PARAMETER_PREFIX_KEY + ParameterSubjectType.TEMPLATE.getCode(),
                                    request.getTemplateID().toString(), TbParameter.class)
                            .stream().collect(Collectors.toMap(TbParameter::getToken, e -> e));
                    return entry.getValue().stream().map(e -> {
                        Assert.isTrue(paramMap.containsKey(e.getFieldToken()),
                                "模板参数{}找不到", e.getFieldToken());
                        return Param.valueOf(paramMap.get(e.getFieldToken()),
                                e.getOrigin(), entry.getKey());
                    });
                }
                case EX -> {
                    Map<String, MonitorTypeCacheData.Field> exMap = request.getMonitorTypeCache()
                            .getMonitortypeFieldList().stream()
                            .filter(e -> FieldClass.EXTEND_CONFIG.equals(e.getFieldClass()))
                            .collect(Collectors.toMap(MonitorTypeCacheData.Field::getFieldToken, e -> e));
                    return entry.getValue().stream().map(e -> {
                        Assert.isTrue(exMap.containsKey(e.getFieldToken()),
                                "扩展字段{}找不到", e.getFieldToken());
                        return Param.valueOf(exMap.get(e.getFieldToken()), e.getOrigin(), entry.getKey());
                    });
                }
                case HISTORY -> {
                    return entry.getValue().stream().map(e -> Param.valueOf(request.getMonitorTypeCache(), e));
                }
                default -> {
                    //TODO SELF、MON 待实现
                    return Stream.empty();
                }
            }
        }).toList();
        result.setParamList(paramList);
        return result;
    }

    @Override
    public Object trying(TryingRequest request) {
        List<TryingResponse> result = List.of();
        Dict fieldValueMap = Dict.create();
        switch (request.getCalType()) {
            case FORMULA:
                return request.getFieldList().stream().map(f -> {
                    Double item = FormulaUtil.calculate(f.getFormula(), typeListMap ->
                            typeListMap.forEach((type, sources) ->
                                    sources.forEach(source ->
                                            source.setFieldValue(Origin.Type.SELF.equals(type) ?
                                                    fieldValueMap.getDouble(source.getFieldToken()) :
                                                    request.getParamMap().get(source.getOrigin())))
                            ));
                    //每计算一个字段，将其结果缓存，供后续公式使用
                    fieldValueMap.set(f.getFieldToken(), item);
                    return new TryingResponse(item, f.getFieldToken(), f.getID());
                }).filter(e -> request.getFieldID().equals(e.fieldID())).toList();
            case SCRIPT:
                //TODO 脚本计算未实现
                break;
            case HTTP:
                //TODO HTTP计算未实现
                break;
        }
        return result;
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

    @Override
    public Map<Integer, List<Integer>> queryAllSensorID() {
        List<Tuple<Integer, Integer>> temp = this.baseMapper.queryAllTypeAndID();
        return temp.stream().collect(Collectors.groupingBy(Tuple::getItem1, Collectors.mapping(Tuple::getItem2, Collectors.toList())));
    }

    @Override
    public List<SensorNameResponse> queryManualSensorListByMonitor(QueryManualSensorListByMonitorParam param) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbSensor>().eq(TbSensor::getMonitorType, param.getMonitorType())
                .eq(TbSensor::getProjectID, param.getProjectID()).eq(TbSensor::getKind, SensorKindEnum.MANUAL_KIND.getCode())
                .select(TbSensor::getID, TbSensor::getName, TbSensor::getAlias)).stream().map(u ->
                SensorNameResponse.builder().sensorID(u.getID()).sensorName(u.getName()).sensorAlias(u.getAlias()).build()).toList();
    }

    @Override
    public List<SensorSimple> querySimpleList(QuerySensorSimpleListRequest param) {
        LambdaQueryWrapper<TbSensor> wrapper = Wrappers.<TbSensor>lambdaQuery()
                .select(TbSensor::getID, TbSensor::getProjectID, TbSensor::getName, TbSensor::getAlias,
                        TbSensor::getMonitorType, TbSensor::getKind, TbSensor::getMonitorPointID);
        Optional.ofNullable(param.getIdList()).filter(e -> !e.isEmpty())
                .ifPresent(e -> wrapper.in(TbSensor::getID, e));
        Optional.ofNullable(param.getProjectIDList()).filter(e -> !e.isEmpty())
                .ifPresent(e -> wrapper.in(TbSensor::getProjectID, e));
        Optional.ofNullable(param.getMonitorTypeList()).filter(e -> !e.isEmpty())
                .ifPresent(e -> wrapper.in(TbSensor::getMonitorType, e));

        return this.list(wrapper).stream().map(SensorSimple::valueOf).toList();
    }

    @Override
    public TryingResponse calculateField(CalculateFieldRequest request) {
        switch (request.getTemplate().getDataSourceComposeType()) {
            case SINGLE_IOT, SINGLE_MONITOR -> {
                Map<String, TbParameter> templateParamMap = monitorRedisService.getList(RedisKeys.PARAMETER_PREFIX_KEY +
                                ParameterSubjectType.TEMPLATE.getCode(), request.getTemplate().getID().toString(), TbParameter.class)
                        .stream().collect(Collectors.toMap(TbParameter::getToken, e -> e));

                Map<String, TbParameter> sensorParamMap = monitorRedisService.getList(RedisKeys.PARAMETER_PREFIX_KEY +
                                ParameterSubjectType.SENSOR.getCode(), request.getSensorID().toString(), TbParameter.class)
                        .stream().collect(Collectors.toMap(TbParameter::getToken, e -> e));

                switch (request.getTemplate().getCalType()) {
                    case FORMULA: {
                        FormulaCacheData formula = request.getTemplate().getTemplateFormulaList().stream()
                                .filter(e -> e.getFieldID().equals(request.getTargetFieldID())).findFirst().orElse(null);
                        if (formula != null) {
                            Double data = FormulaUtil.calculate(formula.getFormula(), typeListMap -> {
                                typeListMap.forEach((type, sources) -> {
                                    switch (type) {
                                        case SELF -> {
                                            sources.forEach(source -> {
                                                source.setFieldValue(request.getParamFiledMap().get(source.getFieldToken()));
                                            });
                                        }
                                        case PARAM -> {
                                            sources.forEach(source -> {
                                                String value = sensorParamMap.containsKey(source.getFieldToken()) ?
                                                        sensorParamMap.get(source.getFieldToken()).getPaValue() :
                                                        templateParamMap.get(source.getFieldToken()).getPaValue();
                                                source.setFieldValue(Convert.toDouble(value));
                                            });
                                        }
                                        case MON -> {
                                            //TODO:
                                        }
                                        case EX -> {
                                            sources.forEach(source -> request.getConfigFieldValue().get(source.getFieldToken()));
                                        }
                                        default -> {
                                            throw new InvalidParameterException("缺少必要参数");
                                        }
                                    }
                                });
                            });
                            return new TryingResponse(data, request.getTargetFieldToken(), request.getTargetFieldID());
                        }
                    }
                    case SCRIPT:
                    case HTTP:
                    case NONE:
                }
            }
            default -> {
                throw new InvalidParameterException("不支持的数据源组合类型");
            }
        }
        return null;
    }

    /**
     * 根据物联网传感器类型获取设备和传感器信息
     *
     * @param catalog 传感器数据源列表
     * @param request 请求参数
     * @return key:iotSensorType value: {@link DeviceWithSensor}
     */
    private Map<String, List<DeviceWithSensor>> getIotMap(List<DataSourceCatalogResponse> catalog,
                                                          DataSourceCatalogRequest request) {
        Set<String> iotSensorTypeSet = catalog.stream()
                .flatMap(e -> e.getDataSourceList().stream())
                .filter(e -> DataSourceType.IOT_SENSOR.getCode() == e.getDataSourceType())
                .map(e -> StrUtil.subBefore(e.getTemplateDataSourceToken(), StrUtil.UNDERLINE, false))
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<String, List<DeviceWithSensor>> iotMap = new HashMap<>();
        if (!iotSensorTypeSet.isEmpty()) {
            QueryDeviceAndSensorRequest param = QueryDeviceAndSensorRequest.builder()
                    .companyID(request.getCompanyID() == null ? CurrentSubjectHolder.getCurrentSubject().getCompanyID() : request.getCompanyID())
                    .iotSensorTypeList(iotSensorTypeSet)
                    .sendType(SendType.MDMBASE.toInt())
                    .sendAddressList(List.of(request.getProjectID().toString()))
                    .build();
            Optional.ofNullable(request.getKeyword()).filter(e -> !e.isBlank())
                    .ifPresent(e -> param.setDeviceTokenList(List.of(e)));
            ResultWrapper<List<DeviceWithSensor>> wrapper = iotService.queryDeviceAndSensorList(param);
            if (wrapper.apiSuccess()) {
                List<DeviceWithSensor> data = wrapper.getData();
                data.forEach(item -> {
                    item.getSensorList().stream().collect(Collectors.groupingBy(DeviceWithSensor.Sensor::iotSensorType))
                            .forEach((iotSensorType, list) -> {
                                DeviceWithSensor temp = BeanUtil.copyProperties(item, DeviceWithSensor.class);
                                temp.setSensorList(list);
                                if (iotMap.containsKey(iotSensorType)) {
                                    List<DeviceWithSensor> val = iotMap.get(iotSensorType);
                                    val.add(temp);
                                    iotMap.put(iotSensorType, val);
                                } else {
                                    iotMap.put(iotSensorType, CollUtil.newArrayList(temp));
                                }
                            });
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
                    .in(TbSensor::getName, monitorSensorNames)
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

    
    