package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.IConfigParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * Implement {@code IConfigParam} to use {@see ProjectConfigKeyUtils#getKey(String, Integer)}<br>
 *
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 14:51
 * @see IConfigParam
 * @see cn.shmedo.monitor.monibotbaseapi.util.projectConfig.ProjectConfigKeyUtils#getKey(String, Integer)
 */
@Data
public class ThematicProjectConfigInfo implements IConfigParam {
    @JsonIgnore
    private Integer projectID;
    private Integer configID;
    private String group;
    private String key;
    private String value;
}
