package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserNoPageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.*;
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
                            final Map<Integer, Map<Integer, UserNoPageInfo.TbUserEx>> deptIDUsersMap) {
        setUserName(companyID, deviceWarnList, userMap, deptIDUsersMap);
        setUserName(companyID, dataWarnList, userMap, deptIDUsersMap);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void setUserName(final Integer companyID, List<? extends DeviceWarnConfigInfo> dataList,
                                    final Map<Integer, UserNoPageInfo.TbUserEx> userExMap,
                                    final Map<Integer, Map<Integer, UserNoPageInfo.TbUserEx>> deptIDUsersMap) {
        dataList.stream().peek(u -> {
            Map<Integer, UserIDName> userMap = u.getUserMap();
            Optional.ofNullable(u.getAllUserList()).filter(CollUtil::isNotEmpty).ifPresent(w ->
                    w.stream().map(userExMap::get).filter(Objects::nonNull).peek(s -> {
                        Integer userID = s.getUserID();
                        (companyID.equals(s.getCompanyID()) ? userMap : u.getExternalMap()).putIfAbsent(userID, new UserIDName(userID, s.getName()));
                    }).toList());
            Optional.ofNullable(u.getDeptList()).filter(CollUtil::isNotEmpty).ifPresent(w ->
                    userMap.putAll(w.stream().map(deptIDUsersMap::get).filter(Objects::nonNull).map(s ->
                                    s.values().stream().map(m -> new UserIDName(m.getUserID(), m.getName())).toList())
                            .filter(CollUtil::isNotEmpty).flatMap(Collection::stream)
                            .collect(Collectors.toMap(UserIDName::getUserID, Function.identity()))));
        }).toList();
    }
}
