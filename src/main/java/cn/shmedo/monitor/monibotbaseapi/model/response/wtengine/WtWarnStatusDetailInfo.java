package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WtWarnStatusDetailInfo extends WtWarnStatusInfo {
    private String fieldToken;
    private String fieldName;
    private String triggerRule;
}
