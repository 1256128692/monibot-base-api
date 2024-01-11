package cn.shmedo.monitor.monibotbaseapi.model.response.third;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 16:38
 */
public class DeptSimpleInfo {
    private Integer id;

    private String name;

    private Integer companyID;

    private Integer parentID;

    public DeptSimpleInfo() {
    }

    public DeptSimpleInfo(Integer id, String name, Integer companyID, Integer parentID) {
        this.id = id;
        this.name = name;
        this.companyID = companyID;
        this.parentID = parentID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }
}
