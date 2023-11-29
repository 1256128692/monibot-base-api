package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-23 17:33
 */
@Data
public class SensorIDWithFormulaBaseInfo {
    private Integer sensorID;
    private Integer monitorType;
    private Integer calType;
    private List<FormulaBaseInfo> formulaBaseInfoList;
    private List<TbParameter> parameterList;
}
