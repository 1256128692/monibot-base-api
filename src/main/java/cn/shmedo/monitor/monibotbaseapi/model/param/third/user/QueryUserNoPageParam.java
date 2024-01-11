package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 16:54
 */
public class QueryUserNoPageParam {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于0")
    private Integer companyID;
    private Integer deptID;
    private String account;
    private String userName;
    private String cellPhone;
    private Integer groupID;
    private Boolean includeExternal;
    private List<Integer> userIDList;
    private Boolean followChildDept;

    public QueryUserNoPageParam() {
    }

    public QueryUserNoPageParam(Integer companyID, Integer deptID, String account, String userName, String cellPhone,
                                Integer groupID, Boolean includeExternal, List<Integer> userIDList, Boolean followChildDept) {
        this.companyID = companyID;
        this.deptID = deptID;
        this.account = account;
        this.userName = userName;
        this.cellPhone = cellPhone;
        this.groupID = groupID;
        this.includeExternal = includeExternal;
        this.userIDList = userIDList;
        this.followChildDept = followChildDept;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getDeptID() {
        return deptID;
    }

    public void setDeptID(Integer deptID) {
        this.deptID = deptID;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Boolean getIncludeExternal() {
        return includeExternal;
    }

    public void setIncludeExternal(Boolean includeExternal) {
        this.includeExternal = includeExternal;
    }

    public List<Integer> getUserIDList() {
        return userIDList;
    }

    public void setUserIDList(List<Integer> userIDList) {
        this.userIDList = userIDList;
    }

    public Boolean getFollowChildDept() {
        return followChildDept;
    }

    public void setFollowChildDept(Boolean followChildDept) {
        this.followChildDept = followChildDept;
    }
}
