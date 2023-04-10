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
import cn.shmedo.monitor.monibotbaseapi.cache.MonitorTypeCache;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DatasourceType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorTypeFieldClass;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ParamSubjectType;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceAndSensorRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.service.SensorService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2023/4/4
 */
@Service
@AllArgsConstructor
public class SensorServiceImpl extends ServiceImpl<TbSensorMapper, TbSensor> implements SensorService {

    private final IotService iotService;
    private final TbMonitorTypeTemplateMapper monitorTypeTemplateMapper;
    private final TbTemplateDataSourceMapper templateDataSourceMapper;
    private final TbMonitorTypeMapper monitorTypeMapper;
    private final TbParameterMapper parameterMapper;
    private final TbSensorDataSourceMapper sensorDataSourceMapper;
    private final TbMonitorTypeFieldMapper monitorTypeFieldMapper;

    @Override
    public PageUtil.Page<SensorPageResponse> sensorPage(SensorPageRequest request) {
        Page<SensorPageResponse> page = new Page<>(request.getCurrentPage(), request.getPageSize());
        IPage<SensorPageResponse> pageData = this.baseMapper.selectSensorPage(page, request);
        return new PageUtil.Page<>(pageData.getPages(), pageData.getRecords(), pageData.getTotal());
    }

    @Override
    public List<DataSourceCatalogResponse> dataSourceCatalog(DataSourceCatalogRequest request) {
        List<DataSourceCatalogResponse> result = monitorTypeTemplateMapper.dataSourceCatalog(request);

        Map<String, List<DeviceWithSensor>> iotMap = getIotMap(() -> result.stream()
                .flatMap(e -> e.getDataSourceList().stream())
                .filter(e -> DatasourceType.IOT.getCode() == e.getDataSourceType())
                .map(e -> StrUtil.subBefore(e.getTemplateDataSourceToken(), StrUtil.UNDERLINE, false))
                .filter(Objects::nonNull).collect(Collectors.toSet()), request.getKeyword());

        Map<String, List<TbSensor>> monitorMap = getMonitorMap(() -> result.stream()
                .flatMap(e -> e.getDataSourceList().stream())
                .filter(e -> DatasourceType.MONITOR.getCode() == e.getDataSourceType())
                .map(DataSourceCatalogResponse.DataSource::getTemplateDataSourceToken).collect(Collectors.toSet()));

        return result.stream().peek(item -> item.getDataSourceList().forEach(e -> {
                    String token = StrUtil.subBefore(e.getTemplateDataSourceToken(),
                            StrUtil.UNDERLINE, false);
                    if (DatasourceType.IOT.getCode() == e.getDataSourceType()) {

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
                    } else if (DatasourceType.MONITOR.getCode() == e.getDataSourceType()) {
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
        if (request.getTemplateDataSourceID() != null) {
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
    public Object addSensor(SaveSensorRequest request) {
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
        baseMapper.insert(sensor);

        //传感器数据源
        List<TbSensorDataSource> sensorDataSources = request.getDataSourceList().stream().map(source -> {
            TbSensorDataSource dataSource = new TbSensorDataSource();
            dataSource.setDataSourceID(sensor.getDataSourceID());
            dataSource.setDataSourceType(source.getDataSourceType().getCode());
            dataSource.setTemplateDataSourceToken(source.getTemplateDataSourceToken());
            dataSource.setDataSourceToken(DatasourceType.IOT.equals(source.getDataSourceType()) ?
                    source.getUniqueToken() + StrUtil.AT + source.getSensorName() : source.getSensorName());
            dataSource.setDataSourceComposeType(request.getDataSourceComposeType().getCode());
            dataSource.setExValues(source.getExValues());
            return dataSource;
        }).toList();
        sensorDataSourceMapper.insertBatchSomeColumn(sensorDataSources);

        //传感器参数
        if (!request.getParameterList().isEmpty()) {
            request.getParameterList().forEach(item -> item.setSubjectID(sensor.getID()));
            parameterMapper.insertBatch(request.getParameterList());
        }

        return Dict.of("id", sensor.getID());
    }

    @Override
    public SensorInfoResponse sensorInfo(SensorInfoRequest request) {
        TbSensor sensor = baseMapper.selectById(request.getSensorID());
        Assert.notNull(sensor, "传感器不存在");

        SensorInfoResponse response = SensorInfoResponse.valueOf(sensor);
        //扩展配置
        Map<String, TbMonitorTypeField> typeFields = monitorTypeFieldMapper.selectList(new LambdaQueryWrapper<TbMonitorTypeField>()
                        .eq(TbMonitorTypeField::getMonitorType, response.getMonitorType())
                        .eq(TbMonitorTypeField::getFieldClass, MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass()))
                .stream().collect(Collectors.toMap(TbMonitorTypeField::getFieldName, e -> e));
        Dict exConfig = JSONUtil.toBean(response.getConfigFieldValue(), Dict.class);
        response.setExFields(exConfig.entrySet().stream().filter(e -> typeFields.containsKey(e.getKey()))
                .map(e -> SensorInfoResponse.ExField.valueOf(typeFields.get(e.getKey()),
                        e.getValue().toString())).toList());
        //参数
        response.setParamFields(parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                .eq(TbParameter::getSubjectType, ParamSubjectType.Sensor.getType())
                .eq(TbParameter::getSubjectID, response.getID())));
        //数据源
        response.setDataSourceList(sensorDataSourceMapper
                .selectList(new LambdaQueryWrapper<TbSensorDataSource>()
                        .eq(TbSensorDataSource::getDataSourceID, response.getDataSourceID())));
        response.setMonitorTypeName(MonitorTypeCache.monitorTypeMap.get(response.getMonitorType()).getTypeName());
        return response;
    }

    @Override
    public void deleteSensor(DeleteSensorRequest request) {

    }

    @Override
    public Object updateSensor(UpdateSensorRequest request) {
        return null;
    }

    @Override
    public Object getTryingParam(QueryTryingParamRequest request) {
        return null;
    }

    @Override
    public Object trying(TryingRequest request) {
        return null;
    }

    @Override
    public BaseConfigResponse baseConfig(BaseConfigRequest request) {
        List<TbParameter> parameterList = parameterMapper.selectList(new LambdaQueryWrapper<TbParameter>()
                .eq(TbParameter::getSubjectType, ParamSubjectType.Template.getType())
                .eq(TbParameter::getSubjectID, request.getTemplateID()));
        QueryWrapper<Void> wrapper = new QueryWrapper<>();
//            wrapper.in("tmtf.FieldClass", MonitorTypeFieldClass.ExtendedConfigurations.getFieldClass());
        wrapper.eq("tmt.MonitorType", request.getMonitorType());
        List<BaseConfigResponse> list = monitorTypeMapper.queryMonitorTypeWithField(wrapper).stream().map(e -> {
            BaseConfigResponse item = new BaseConfigResponse();
            item.setExFields(e.getFieldList().stream().filter(f -> f.getFieldClass() == 3).collect(Collectors.toList()));
            item.setParamFields(parameterList);
            return item;
        }).toList();
        return CollUtil.getFirst(list);
    }

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

    private Map<String, List<TbSensor>> getMonitorMap(Supplier<Set<String>> consumer) {
        Set<String> monitorSensorTypes = consumer.get();
        Map<String, List<DeviceWithSensor>> monitorMap = new HashMap<>();
        if (!monitorSensorTypes.isEmpty()) {
            LambdaQueryWrapper<TbSensor> wrapper = new LambdaQueryWrapper<TbSensor>()
                    .eq(TbSensor::getName, monitorSensorTypes)
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
        Integer number = this.baseMapper.getNameSerialNumber(monitorType, projectID);
        return monitorType + StrUtil.UNDERLINE + number;
    }
}

    
    