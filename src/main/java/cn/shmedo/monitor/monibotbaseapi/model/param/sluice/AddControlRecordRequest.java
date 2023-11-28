package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sluice.SluiceLog;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class AddControlRecordRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @JsonIgnore
    private Integer projectID;

    @NotNull
    @Positive
    private Integer sid;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime time;

    @NotNull
    @Positive
    public Integer userID;

    @Range(min = 0, max = 2)
    private Integer runningSta;

    @Range(min = 0, max = 7)
    private Integer software;

    @Range(min = 0, max = 2)
    private Integer hardware;

    private String msg;

    private String logLevel;

    @Override
    public ResultWrapper<?> validate() {

        TbSensorMapper sensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        TbSensor sensor = sensorMapper.selectOne(Wrappers.<TbSensor>lambdaQuery()
                .eq(TbSensor::getID, this.sid)
                .eq(TbSensor::getMonitorType, SluiceLog.MONITOR_TYPE)
                .select(TbSensor::getProjectID, TbSensor::getID));
        Assert.notNull(sensor, "闸门不存在");

        projectID = sensor.getProjectID();

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}