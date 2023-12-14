package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-12-13 18:04
 */
@Data
public class AddManualBatchV2Data {
    @NotNull(message = "人工传感器ID不能为空")
    @Positive(message = "人工传感器ID不能小于0")
    private Integer sensorID;
    @NotNull(message = "时间不能为空")
    private Date time;
    @NotEmpty(message = "属性列表不能为空")
    private List<Map<String, String>> fieldList;
}
