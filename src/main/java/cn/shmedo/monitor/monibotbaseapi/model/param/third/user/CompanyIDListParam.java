package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

import java.util.Collection;

/**
 * @program: iot-manager
 * @author: gaoxu
 * @create: 2021-07-14 09:46
 **/
public class CompanyIDListParam {
    /**
     * 大小为100
     */
    private Collection<Integer> companyIDList;
    /**
     * 支持模糊查询
     */
    private String companyName;
    /**
     * true时公司名称必须不为空， companyIDList为当前用户的公司ID
     */
    private Boolean isAll;

    public CompanyIDListParam(Collection<Integer> companyIDList) {
        this.companyIDList = companyIDList;
    }

    public CompanyIDListParam() {
    }

    public Collection<Integer> getCompanyIDList() {
        return companyIDList;
    }

    public void setCompanyIDList(Collection<Integer> companyIDList) {
        this.companyIDList = companyIDList;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getAll() {
        return isAll;
    }

    public void setAll(Boolean all) {
        isAll = all;
    }
}
