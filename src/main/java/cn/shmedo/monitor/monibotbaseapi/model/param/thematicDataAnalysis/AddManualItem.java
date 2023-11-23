package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-23 10:17
 */
@Data
@Builder
public class AddManualItem {
    private Integer monitorType;
    /**
     * {@code monitorType}下全部子类型的{@code fieldToken}
     */
    private List<String> fieldTokenList;
    /**
     * 添加到influxDB的数据,是{@code monitorType}、{@code fieldTokenList}对应的数据
     * <p>
     * 如果{@code fieldTokenList}中含有`s1`、`s2`、`s3`三个字段，
     * 那么{@code dataList}中每一项的key都应当具有`s1`、`s2`、`s3`这三个字段和额外的`sensorID`、`time`两个字段，且对应的value都不为null
     * </p>
     */
    private List<Map<String, Object>> dataList;
}
