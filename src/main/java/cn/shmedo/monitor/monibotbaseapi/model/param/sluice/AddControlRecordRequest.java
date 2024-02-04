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
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class AddControlRecordRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @JsonIgnore
    private Integer projectID;

    @NotEmpty
    private Set<@NotNull Integer> sensorIDList;

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
        List<TbSensor> sensors = sensorMapper.selectList(Wrappers.<TbSensor>lambdaQuery()
                .in(TbSensor::getID, this.sensorIDList)
                .eq(TbSensor::getMonitorType, SluiceLog.MONITOR_TYPE)
                .select(TbSensor::getProjectID, TbSensor::getID));
        Assert.isTrue(sensors.size() == sensorIDList.size(), "闸门不存在");

        List<Integer> list = sensors.stream().map(TbSensor::getProjectID).distinct().toList();
        Assert.isTrue(list.size() == 1, "闸门不属于同一项目");
        this.projectID = list.get(0);

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}