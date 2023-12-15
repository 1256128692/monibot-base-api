package cn.shmedo.monitor.monibotbaseapi.model.param.watermeasure;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.CalculateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureWay;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * @author Chengfs on 2023/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMeasurePointRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @NotNull
    @Positive
    private Integer projectID;

    @NotBlank
    private String monitorPointName;

    @NotNull
    private MonitorType monitorType;

    @NotBlank
    private String gpsLocation;

    @NotNull
    @Positive
    private Integer monitorItemID;

    @NotNull
    private WaterMeasureType waterMeasureType;

    @NotNull
    private WaterMeasureWay waterMeasureWay;

    @NotNull
    private CalculateType calculateType;

    @NotEmpty
    private Set<MonitorType> monitorElements;

    @NotNull
    @Positive
    private Integer sensorID;

    @JsonIgnore
    private TbSensor sensor;

    @Override
    public ResultWrapper<?> validate() {
        Assert.isTrue(waterMeasureType.validWaterMeasureWay(waterMeasureWay), "Illegal WaterMeasureWay");
        Assert.isTrue(waterMeasureType.validCalculateType(calculateType), "Illegal CalculateType");
        Assert.isTrue(waterMeasureType.validElements(monitorElements.toArray(MonitorType[]::new)), "Illegal MonitorType");
        Assert.isTrue(MonitorType.WATER_LEVEL.equals(monitorType) || MonitorType.FLOW_VELOCITY.equals(monitorType) ||
                MonitorType.FLOW_CAPACITY.equals(monitorType), "Illegal MonitorType");

        TbMonitorItemMapper monitorItemMapper = SpringUtil.getBean(TbMonitorItemMapper.class);
        Assert.isTrue(monitorItemMapper.exists(Wrappers.<TbMonitorItem>lambdaQuery()
                .eq(TbMonitorItem::getProjectID, projectID)
                .eq(TbMonitorItem::getID, monitorItemID)), "监测项不存在");

        TbSensorMapper sensorMapper = SpringUtil.getBean(TbSensorMapper.class);
        this.sensor = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery()
                .eq(TbSensor::getProjectID, projectID)
                .eq(TbSensor::getID, sensorID).select(TbSensor::getID, TbSensor::getMonitorPointID));
        Assert.notNull(sensor, "传感器不存在");
        Assert.isTrue(sensor.getMonitorPointID() == null, "传感器已绑定其他监测点");

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}