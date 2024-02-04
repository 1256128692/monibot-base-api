package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 16:09
 */
@Data
public class AddWarnThresholdConfigBatchDataListParam {
    @NotNull(message = "监测属性ID不能为空")
    @Positive(message = "监测属性ID必须是正值")
    private Integer fieldID;
    private String warnName;
    @NotNull(message = "比较方式不能为空")
    @Range(min = 1, max = 6, message = "比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于")
    private Integer compareMode;
    private Boolean enable;
    private String value;
}
