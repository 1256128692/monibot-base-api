package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.DepartmentIncludeUserInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserNoPageInfo;
import cn.shmedo.monitor.monibotbaseapi.util.depts.DeptUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-15 14:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarnNotifyConfigInfo extends WarnBaseConfigInfo {
    private List<DeviceWarnConfigInfo> deviceWarnList;
    private List<DataWarnConfigInfo> dataWarnList;

    public void setUserName(final Integer companyID, final Map<Integer, UserNoPageInfo.TbUserEx> userMap,
                            final List<DepartmentIncludeUserInfo.DeptResponse> deptResponseList) {
        setUserName(companyID, deviceWarnList, userMap, deptResponseList);
        setUserName(companyID, dataWarnList, userMap, deptResponseList);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void setUserName(final Integer companyID, List<? extends DeviceWarnConfigInfo> dataList,
                                    final Map<Integer, UserNoPageInfo.TbUserEx> userExMap,
                                    final List<DepartmentIncludeUserInfo.DeptResponse> deptResponseList) {
        Map<Integer, Set<UserIDName>> deptIDUsersMap = new HashMap<>();
        dataList.stream().peek(u -> {
            Map<Integer, UserIDName> userMap = u.getUserMap();
            Optional.ofNullable(u.getAllUserList()).filter(CollUtil::isNotEmpty).ifPresent(w ->
                    w.stream().map(userExMap::get).filter(Objects::nonNull).peek(s -> {
                        Integer userID = s.getUserID();
                        (companyID.equals(s.getCompanyID()) ? userMap : u.getExternalMap()).putIfAbsent(userID, new UserIDName(userID, s.getName()));
                    }).toList());
            Optional.ofNullable(u.getDeptList()).filter(CollUtil::isNotEmpty).ifPresent(w -> w.stream().peek(s -> {
                if (deptIDUsersMap.containsKey(s)) {
                    userMap.putAll(deptIDUsersMap.get(s).stream().collect(Collectors.toMap(UserIDName::getUserID, Function.identity())));
                } else {
                    try {
                        DeptUtils.processDepartWithAllUser(deptResponseList, dept -> s.equals(dept.getId()), (dept, users) -> {
                            if (Objects.nonNull(dept)) {
                                Map<Integer, UserIDName> collect = users.stream().map(n -> new UserIDName(n.getUserID(), n.getName()))
                                        .collect(Collectors.toMap(UserIDName::getUserID, Function.identity(), (o1, o2) -> o1));
                                userMap.putAll(collect);
                                deptIDUsersMap.put(s, new HashSet<>(collect.values()));
                            }
                        });
                    } catch (TimeoutException e) {
                        throw new RuntimeException("执行超时...");
                    }
                }
            }).toList());
        }).toList();
    }
}
