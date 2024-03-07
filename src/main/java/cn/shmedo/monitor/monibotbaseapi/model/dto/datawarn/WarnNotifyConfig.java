package cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn;

import java.util.Set;

/**
 * @author Chengfs on 2024/1/15
 */
public record WarnNotifyConfig(Integer level, Integer method,
                              Set<Integer> depts, Set<Integer> roles, Set<Integer> users) {
}