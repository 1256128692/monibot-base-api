package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class QueryUserIDNameParameter {
    @NotEmpty(message = "查询的用户IDList不能为空")
    private List<Integer> userIDList;

    public QueryUserIDNameParameter() {
    }

    public QueryUserIDNameParameter(List<Integer> userIDList) {
        this.userIDList = userIDList;
    }

    public List<Integer> getUserIDList() {
        return userIDList;
    }

    public void setUserIDList(List<Integer> userIDList) {
        this.userIDList = userIDList;
    }

    @Override
    public String toString() {
        return userIDList.toString();
    }
}
