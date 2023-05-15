package cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig;

import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.IConfigGroup;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.IConfigParam;
import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 14:27
 */
@Data
@Builder(toBuilder = true)
public class ListProjectConfigResponse implements IConfigParam, IConfigGroup {
    private Integer projectID;
    private Integer monitorGroupID;
    private String group;
    private String key;
    private String value;
}
