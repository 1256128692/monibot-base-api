package cn.shmedo.monitor.monibotbaseapi.model.db;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TbCompany {
    private Integer id;
    private String shortName;
    private String fullName;
    private Integer parentID;
    private String desc;
    private Integer level;
    private Integer createUserID;
    private Date createTime;
    private Integer updateUserID;
    private String updateTime;
    private Boolean hasChild;
    private Boolean delete;
    private String address;
    private String phone;
    private String legalPerson;
    private String scale;
    private String industry;
    private String nature;
    private Integer companyGroupID;
    private String companyGroupName;
    private Integer companyGroupOrder;
    private String webSite;

    private Integer displayOrder;

    private Integer companyPeopleCount;

    public static final int MAX_COMPANY_LEVEL = 2;

    public static final int MEDO_COMPANY_LEVEL = 0;

}