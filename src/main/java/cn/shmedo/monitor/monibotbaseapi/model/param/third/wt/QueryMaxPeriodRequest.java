package cn.shmedo.monitor.monibotbaseapi.model.param.third.wt;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 16:26
 */
public class QueryMaxPeriodRequest {
    private Integer companyID;
    private Integer year;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
