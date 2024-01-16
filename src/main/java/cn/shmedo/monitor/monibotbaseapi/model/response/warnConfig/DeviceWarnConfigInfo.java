package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryProjectBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserIDName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-15 14:57
 */
@Data
@Slf4j
public class DeviceWarnConfigInfo {
    private Integer id;
    private Boolean allProject;
    private List<QueryProjectBaseInfoResponse> projectList;
    @JsonIgnore
    private final Map<Integer, UserIDName> userMap = new HashMap<>();
    @JsonIgnore
    private final Map<Integer, UserIDName> externalMap = new HashMap<>();
    @JsonIgnore
    private String notifyMethod;
    @JsonIgnore
    private String users;
    @JsonIgnore
    private List<Integer> allUserList;
    @JsonIgnore
    private String depts;
    @JsonIgnore
    private List<Integer> deptList;
    @JsonIgnore
    private List<Integer> projectIDList;

    @JsonProperty(value = "notifyMethod")
    private List<Integer> notifyMethod() {
        try {
            return Optional.ofNullable(notifyMethod).map(JSONUtil::parseArray).map(u -> JSONUtil.toList(u, Integer.class)).orElse(List.of());
        } catch (JSONException e) {
            log.error("解析json失败,notifyMethod: {}", notifyMethod);
            return List.of();
        }
    }


    @JsonProperty("userList")
    private Collection<UserIDName> userList() {
        return Optional.of(userMap).filter(CollUtil::isNotEmpty).map(Map::values).orElse(null);
    }

    @JsonProperty("externalList")
    private Collection<UserIDName> externalList() {
        return Optional.of(externalMap).filter(CollUtil::isNotEmpty).map(Map::values).orElse(null);
    }

    public void afterProperties(final Map<Integer, TbProjectInfo> allProjectInfoMap, final List<QueryProjectBaseInfoResponse> allProjectList) {
        this.allProject = Optional.ofNullable(projectIDList).filter(CollUtil::isNotEmpty).map(u -> u.size() == 1 &&
                u.stream().findAny().orElseThrow() == -1).orElseThrow();
        this.projectList = this.allProject ? allProjectList : projectIDList.stream().distinct().filter(allProjectInfoMap::containsKey)
                .map(u -> {
                    TbProjectInfo tbProjectInfo = allProjectInfoMap.get(u);
                    QueryProjectBaseInfoResponse response = new QueryProjectBaseInfoResponse();
                    response.setProjectID(u);
                    response.setProjectName(tbProjectInfo.getProjectName());
                    response.setProjectShortName(tbProjectInfo.getShortName());
                    return response;
                }).toList();
        try {
            Optional.ofNullable(users).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseArray)
                    .map(u -> JSONUtil.toList(u, Integer.class)).ifPresent(this::setAllUserList);
            Optional.ofNullable(depts).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseArray)
                    .map(u -> JSONUtil.toList(u, Integer.class)).ifPresent(this::setDeptList);
        } catch (JSONException e) {
            log.error("parse json error,userListStr:{},\tdeptListStr:{}", users, depts);
            throw new IllegalArgumentException("");
        }
    }
}
