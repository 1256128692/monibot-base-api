package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.SluiceLog;
import cn.shmedo.monitor.monibotbaseapi.util.influx.SimpleQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.influxdb.InfluxDB;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class SingleControlRecordRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @NotNull
    @Positive
    private Long id;

    @JsonIgnore
    private Integer projectID;

    @JsonIgnore
    private SluiceLog sluiceLog;

    private TbSensor sensor;

    @Override
    public ResultWrapper<?> validate() {
        InfluxDB influxDb = ContextHolder.getBean(InfluxDB.class);
        sluiceLog = SimpleQuery.of(SluiceLog.TABLE).eq(DbConstant.TIME_FIELD,
                        LocalDateTime.ofEpochSecond(id, 0, ZoneOffset.of("+8")))
                .limit(1).orderByDesc(DbConstant.TIME_FIELD).row(influxDb, SluiceLog.class);
        Assert.notNull(sluiceLog, "记录不存在");

        TbSensorMapper sensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        sensor = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery()
                .eq(TbSensor::getID, sluiceLog.getSid())
                .eq(TbSensor::getMonitorType, SluiceLog.MONITOR_TYPE)
                .select(TbSensor::getProjectID, TbSensor::getID, TbSensor::getAlias));
        Assert.notNull(sensor, "闸门不存在");

        projectID = sensor.getProjectID();

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}