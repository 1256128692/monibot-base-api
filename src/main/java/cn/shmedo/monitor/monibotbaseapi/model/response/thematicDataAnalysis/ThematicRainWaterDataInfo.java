package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 17:09
 */
@Data
@Builder
public class ThematicRainWaterDataInfo {
    private Date time;
    private Double rainfall;
    private Double distance;
    private Double volumeFlowInput;
    private Double volumeFlowOutput;

    /**
     * @param key 标识枚举 1.降雨量(mm) 2.distance库水位(m) 3.volumeFlowInput入库流量(m³/s) 4.volumeFlowOutput出库流量(m³/s)
     */
    @SneakyThrows
    public Map<String, Object> toMaxDataMap(final Integer key, final SimpleDateFormat formatter) {
        Double value;
        switch (key) {
            case 1 -> value = rainfall;
            case 2 -> value = distance;
            case 3 -> value = volumeFlowInput;
            case 4 -> value = volumeFlowOutput;
            default -> throw new IllegalArgumentException("不合法的水雨情最大值key枚举,key:" + key);
        }
        return Map.of("key", key, "time", formatter.parse(formatter.format(formatter.format(time))), "value", value);
    }
}
