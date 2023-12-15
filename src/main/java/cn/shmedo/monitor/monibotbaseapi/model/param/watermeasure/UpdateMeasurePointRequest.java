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
    private Integer companyID;

    @NotNull
    @Positive
    private Integer sensorID;

    private String gpsLocation;

    private WaterMeasureType waterMeasureType;

    private WaterMeasureWay waterMeasureWay;

    private CalculateType calculateType;

    private Set<@NotNull MonitorType> monitorElements;

    @JsonIgnore
    private TbSensor sensor;

    @Override
    public ResultWrapper<?> validate() {
        this.sensor = SpringUtil.getBean(TbSensorMapper.class).selectOne(Wrappers.<TbSensor>lambdaQuery()
                .eq(TbSensor::getID, sensorID).select(TbSensor::getProjectID, TbSensor::getMonitorPointID, TbSensor::getKind));
        Assert.notNull(sensor, "传感器不存在");
        Assert.notNull(sensor.getMonitorPointID(), "传感器未绑定监测点");

        if (SensorKindEnum.AUTO_KIND.getCode().equals(sensor.getKind())) {
            Assert.notNull(waterMeasureType, "waterMeasureType can not be null");
            Assert.notNull(waterMeasureWay, "waterMeasureWay can not be null");
            Assert.notNull(calculateType, "calculateType can not be null");
            Assert.notEmpty(monitorElements, "monitorElements can not be null");
        }
        Optional.ofNullable(waterMeasureType).ifPresent(type -> {
            Optional.ofNullable(waterMeasureWay).ifPresent(way ->
                    Assert.isTrue(type.validWaterMeasureWay(way), "Illegal waterMeasureWay"));
            Optional.ofNullable(calculateType).ifPresent(cType ->
                    Assert.isTrue(type.validCalculateType(cType), "Illegal calculateType"));
            Optional.ofNullable(monitorElements).ifPresent(elements ->
                    Assert.isTrue(type.validElements(elements.toArray(MonitorType[]::new)), "Illegal monitorElements"));
        });
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(sensor.getProjectID().toString(), ResourceType.BASE_PROJECT);
    }
}