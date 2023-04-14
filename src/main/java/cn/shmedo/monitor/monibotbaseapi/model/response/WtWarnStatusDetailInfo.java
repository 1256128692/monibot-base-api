package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WtWarnStatusDetailInfo extends WtWarnStatusInfo {
    private Integer metadataID;
    private String metadataName;
    private String compareRule;
    private String triggerRule;
}
