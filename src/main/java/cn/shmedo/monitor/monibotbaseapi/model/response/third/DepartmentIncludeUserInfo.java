package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-29 16:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentIncludeUserInfo {
    private TbCompany company;
    private List<DeptResponse> depts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TbCompany {
        private Integer id;
        private String shortName;
        private String fullName;
        private Integer parentID;
        private String desc;
        private Integer level;
        private Integer createUserID;
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
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
        private List<TbCompany> subCompany;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DeptResponse {
        private TbDepartment deptInfo;
        private List<DeptResponse> subDepts;
        private List<UsersInDept> usersInDept;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UsersInDept {
        private Integer userID;
        private String name;
        private String cellPhone;
        private Integer departmentID;
        private String headPhotoPath;
        private String headPhotoHttpPath;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TbDepartment {
        private Integer id;
        private String name;
        private Integer companyID;
        private Integer parentID;
        private String desc;
        private Integer level;
        private Boolean readOnly;
        private Integer displayOrder;
        private Integer createUserID;
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createTime;
        private Integer updateUserID;
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private Date updateTime;
        private Boolean hasChild;
        private Boolean delete;
        private Integer departmentPeopleCount;
    }
}
