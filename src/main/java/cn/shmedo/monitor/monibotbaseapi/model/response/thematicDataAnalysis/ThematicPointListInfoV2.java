package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 15:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThematicPointListInfoV2 extends ThematicPointListInfo {
    private String sensorExValues;
    private Double nozzleElevation;
    private Double emptyPipeDistance;
    private Double levelElevation;
    private List<ThematicProjectConfigInfo> monitorPointConfigList;
    private List<ThematicEigenValueData> eigenValueList;
    private List<ThematicSensorParamField> paramFieldList;
}
