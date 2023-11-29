package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-14 17:18
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ThematicLongitudinalEigenValueData extends ThematicEigenValueData {
    @JsonIgnore
    private Double eigenValue;
    private Double abnormalValue;
}
