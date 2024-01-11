package cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 12:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateWarnNotifyConfigParam extends QueryWarnNotifyConfigDetailParam {
    private Boolean allProject;
    private List<Integer> projectIDList;
    private List<Integer> warnLevel;
    private List<Integer> notifyMethod;
    private List<Integer> deptList;
    private List<Integer> userList;
    private List<Integer> roleList;
    private String exValue;
}
