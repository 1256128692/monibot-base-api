package cn.shmedo.monitor.monibotbaseapi.model.param.watermeasure;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorKindEnum;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.CalculateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureWay;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.Set;

/**
 * @author Chengfs on 2023/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMeasurePointRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer sensorID;

    @Positive
    private Integer targetSensorID;

    private String gpsLocation;

    private WaterMeasureType waterMeasureType;

    private WaterMeasureWay waterMeasureWay;

    private CalculateType calculateType;

    private Set<@NotNull MonitorType> monitorElements;

    @JsonIgnore
    private TbSensor sensor;

    @Override
    public ResultWrapper<?> validate() {
        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        this.sensor = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery()
                .eq(TbSensor::getID, sensorID).select(TbSensor::getProjectID, TbSensor::getMonitorType,
                        TbSensor::getMonitorPointID, TbSensor::getKind));
        Assert.notNull(sensor, "传感器不存在");
        Assert.notNull(sensor.getMonitorPointID(), "传感器未绑定监测点");

        targetSensorID = sensorID.equals(targetSensorID) ? null : targetSensorID;
        Optional.ofNullable(targetSensorID).ifPresent(id -> {
            Assert.isTrue(SensorKindEnum.MANUAL_KIND.getCode().equals(sensor.getKind()), "不允许修改自动采集量水点计算方式");
            TbSensor target = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery()
                    .eq(TbSensor::getProjectID, sensor.getProjectID()).eq(TbSensor::getID, id)
                    .select(TbSensor::getID, TbSensor::getKind, TbSensor::getMonitorPointID, TbSensor::getMonitorType));
            Assert.notNull(target, "传感器不存在");
            Assert.isTrue(target.getMonitorPointID() == null, "传感器已绑定其他监测点");
            Assert.isTrue(target.getMonitorType().equals(sensor.getMonitorType()), "传感器监测类型不匹配");
//            Assert.isTrue(SensorKindEnum.MANUAL_KIND.getCode().equals(target.getKind()), "不允许修改非人工传感器");
        });


        if (SensorKindEnum.MANUAL_KIND.getCode().equals(sensor.getKind())) {
            Assert.notNull(waterMeasureType, "waterMeasureType can not be null");
            Assert.notNull(waterMeasureWay, "waterMeasureWay can not be null");
            Assert.notNull(calculateType, "calculateType can not be null");
            Assert.notEmpty(monitorElements, "monitorElements can not be null");
        }
        Optional.ofNullable(waterMeasureType).ifPresent(type -> {
            Optional.ofNullable(waterMeasureWay).ifPresent(way ->
                    Assert.isTrue(type.validWaterMeasureWay(way), "非法参数: waterMeasureWay"));
            Optional.ofNullable(calculateType).ifPresent(cType ->
                    Assert.isTrue(type.validCalculateType(cType), "非法参数: calculateType"));
            Optional.ofNullable(monitorElements).ifPresent(elements ->
                    Assert.isTrue(type.validElements(elements.toArray(MonitorType[]::new)), "非法参数: monitorElements"));
        });
        monitorElements = Optional.ofNullable(monitorElements).orElse(Set.of());
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(sensor.getProjectID().toString(), ResourceType.BASE_PROJECT);
    }
}