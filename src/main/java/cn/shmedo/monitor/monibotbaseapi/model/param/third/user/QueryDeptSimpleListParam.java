package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 16:36
 */
public class QueryDeptSimpleListParam {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    private List<Integer> deptIDList;

    public QueryDeptSimpleListParam() {
    }

    public QueryDeptSimpleListParam(Integer companyID, List<Integer> deptIDList) {
        this.companyID = companyID;
        this.deptIDList = deptIDList;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public List<Integer> getDeptIDList() {
        return deptIDList;
    }

    public void setDeptIDList(List<Integer> deptIDList) {
        this.deptIDList = deptIDList;
    }
}
