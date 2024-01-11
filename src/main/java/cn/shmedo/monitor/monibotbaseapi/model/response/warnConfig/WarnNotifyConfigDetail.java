package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 18:18
 */
@Data
@Builder
public class WarnNotifyConfigDetail {
    private Boolean allProject;
    private List<Integer> projectIDList;
    private List<Integer> deptIDList;
    private List<Integer> userIDList;
    private List<Integer> externalIDList;
}
