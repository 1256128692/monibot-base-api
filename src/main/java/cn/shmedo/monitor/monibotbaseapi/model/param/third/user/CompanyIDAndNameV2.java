package cn.shmedo.monitor.monibotbaseapi.model.param.third.user;

/**
 * @program: iot-manager
 * @author: gaoxu
 * @create: 2021-07-14 09:30
 **/
public class CompanyIDAndNameV2 {
    private Integer companyID;
    private String companyName;

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public CompanyIDAndNameV2() {
    }

    public CompanyIDAndNameV2(Integer companyID, String companyName) {
        this.companyID = companyID;
        this.companyName = companyName;
    }
}
