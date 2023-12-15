package cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure;

import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.CalculateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.irrigated.WaterMeasureWay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 监测点扩展字段 <br/>
 * <br/>
 * 使用示例: <br/>
 * <pre>
 *     String json = """
 *                     {"calculateType": 1,"monitorElements": [2, 3, 14],"waterMeasureType": 1,"waterMeasureWay": 11}
 *                     """;
 *     MonitorPointExValue bean = JsonUtil.toObject(json, MonitorPointExValue.class);
 *     WaterMeasureType type = JsonUtil.get(json,"waterMeasureType", WaterMeasureType.class);
 * </pre>
 *
 * @author Chengfs on 2023/12/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitorPointExValue {

    private WaterMeasureType waterMeasureType;

    private WaterMeasureWay waterMeasureWay;

    private CalculateType calculateType;

    private List<MonitorType> monitorElements;

}