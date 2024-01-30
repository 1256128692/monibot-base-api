package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 11:11
 */
@Data
public class WarnFieldThresholdConfigInfo {
    private Integer fieldID;
    private String fieldName;
    private String fieldToken;
    @JsonIgnore
    private Integer displayOrder;
    private Integer configID;
    private String warnName;
    private Integer compareMode;
    private Boolean enable;
    private String value;
}
