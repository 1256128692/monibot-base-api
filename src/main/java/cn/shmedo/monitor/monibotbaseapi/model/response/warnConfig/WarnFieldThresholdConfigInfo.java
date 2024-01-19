package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

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
    private Integer configID;
    private String warnName;
    private Integer compareMode;
    private Boolean enable;
    private String value;
}
