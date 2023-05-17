package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 11:22
 */
@Data
@Builder(toBuilder = true)
public class SimpleMonitorInfo {
    private Integer groupID;
    private String groupName;
    private Boolean enable;
}
