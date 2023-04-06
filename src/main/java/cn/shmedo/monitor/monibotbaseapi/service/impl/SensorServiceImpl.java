package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.CurrentSubjectHolder;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeTemplateMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTemplateDataSourceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DatasourceType;
import cn.shmedo.monitor.monibotbaseapi.model.param.sensor.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.iot.QueryDeviceAndSensorRequest;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.DataSourceCatalogResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.MonitorTypeCatalogResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorPageResponse;
import cn.shmedo.monitor.monibotbaseapi.service.SensorService;
import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
    public MonitorTypeCatalogResponse monitorTypeCate(MonitorTypeCatalogRequest request) {
        return null;
    }

    @Override
    public Object addSensor(SaveSensorRequest request) {
        return null;
    }

    @Override
    public SensorInfoResponse sensorInfo(SensorInfoRequest request) {
        return null;
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
}

    
    