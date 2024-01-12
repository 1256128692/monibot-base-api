package cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/1/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarnThresholdConfigInfo extends TbWarnThresholdConfig {

    private String projectName;
    private String monitorTypeName;
    private String monitorItemName;
    private String sensorName;
    private String fieldName;
    private String fieldUnitEng;
}