package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 18:18
 */
@Data
public class WarnNotifyConfigDetail {
    private List<Integer> projectIDList;
    private List<Integer> userIDList;
    private List<Integer> externalIDList;
    @JsonIgnore
    private String projectIDStr;
    @JsonIgnore
    private String deptIDStr;
    @JsonIgnore
    private String userIDStr;

    @JsonProperty(value = "allProject")
    private Boolean allProject() {
        return Optional.ofNullable(projectIDStr).map(String::trim).map("-1"::equals).orElse(false);
    }

    @JsonProperty(value = "deptIDList")
    private List<Integer> deptIDList() {
        return Optional.ofNullable(deptIDStr).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseArray)
                .map(u -> JSONUtil.toList(u, Integer.class)).orElse(List.of());
    }
}
