package cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure;

import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.SensorKindEnum;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.CalculateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureWay;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author Chengfs on 2023/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WaterMeasurePointSimple {

    private Integer companyID;

    private Integer projectID;

    private String projectName;

    private ProjectType projectType;

    private String projectTypeName;

    private Integer monitorPointID;

    private String monitorPointName;

    private MonitorType monitorType;

    private String monitorTypeName;

    private Integer sensorID;

    private SensorKindEnum sensorKind;

    private WaterMeasureType waterMeasureType;

    private WaterMeasureWay waterMeasureWay;

    private CalculateType calculateType;

    private Collection<MonitorType> monitorElements;

    @JsonIgnore
    private String exValues;
}