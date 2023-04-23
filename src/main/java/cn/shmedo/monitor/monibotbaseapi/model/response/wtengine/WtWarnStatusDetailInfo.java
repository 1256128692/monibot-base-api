package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import cn.shmedo.monitor.monibotbaseapi.model.standard.IFieldName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
public class WtWarnStatusDetailInfo extends WtWarnStatusInfo implements IFieldName {
    @JsonIgnore
    private Integer monitorType;
    private String fieldToken;
    private String fieldName;
    private String triggerRule;

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
