package cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.IConfigParam;
import cn.shmedo.monitor.monibotbaseapi.util.projectConfig.ProjectConfigKeyUtils;
import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 14:27
 */
@Data
@Builder(toBuilder = true)
public class ListProjectConfigResponse implements IConfigParam {
    private Integer projectID;
    private Integer ID;
    private Integer configID;
    private String group;
    private String key;
    private String value;

    public static ListProjectConfigResponse build(TbProjectConfig config) {
        Tuple<String, Integer> tuple = ProjectConfigKeyUtils.getKey(config.getKey());
        return ListProjectConfigResponse.builder().configID(config.getID()).ID(tuple.getItem2()).key(tuple.getItem1())
                .projectID(config.getProjectID()).group(config.getGroup()).value(config.getValue()).build();
    }
}
