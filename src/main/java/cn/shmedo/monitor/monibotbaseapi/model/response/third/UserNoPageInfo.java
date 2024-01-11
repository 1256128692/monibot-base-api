package cn.shmedo.monitor.monibotbaseapi.model.response.third;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 16:56
 */
public class UserNoPageInfo {
    private TbUserEx user;
    private List<TbDepartment> departments;
    private List<TbGroup> groups;
    private Timestamp lastLoginTime;
    private String lastLoginIP;
    private String lastLoginType;

    public UserNoPageInfo() {
    }

    public UserNoPageInfo(TbUserEx user, List<TbDepartment> departments, List<TbGroup> groups, Timestamp lastLoginTime,
                          String lastLoginIP, String lastLoginType) {
        this.user = user;
        this.departments = departments;
        this.groups = groups;
        this.lastLoginTime = lastLoginTime;
        this.lastLoginIP = lastLoginIP;
        this.lastLoginType = lastLoginType;
    }

    public TbUserEx getUser() {
        return user;
    }

    public void setUser(TbUserEx user) {
        this.user = user;
    }

    public List<TbDepartment> getDepartments() {
        return departments;
    }

    public void setDepartments(List<TbDepartment> departments) {
        this.departments = departments;
    }

    public List<TbGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<TbGroup> groups) {
        this.groups = groups;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIP() {
        return lastLoginIP;
    }

    public void setLastLoginIP(String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }

    public String getLastLoginType() {
        return lastLoginType;
    }

    public void setLastLoginType(String lastLoginType) {
        this.lastLoginType = lastLoginType;
    }

    public static class TbUserEx {
        private Integer userID;
        private Integer companyID;
        private String companyName;
        private String account;
        private String name;
        private String cellPhone;
        private String position;
        private String email;
        private String phone;
        private String address;
        private Integer allowAccessType;
        private String headPhotoPath;
        private Boolean userEnable;
        private Integer createUserID;
        private Date createTime;
        private Integer updateUserID;
        private Date updateTime;
        private Date expireTime;
        private Boolean ssoUser;
        private String ssoToken;
        private Boolean userVxEnable;
        private Date groupExpireTime;

        public TbUserEx() {
        }

        public TbUserEx(Integer userID, Integer companyID, String companyName, String account, String name, String cellPhone,
                        String position, String email, String phone, String address, Integer allowAccessType,
                        String headPhotoPath, Boolean userEnable, Integer createUserID, Date createTime, Integer updateUserID,
                        Date updateTime, Date expireTime, Boolean ssoUser, String ssoToken, Boolean userVxEnable, Date groupExpireTime) {
            this.userID = userID;
            this.companyID = companyID;
            this.companyName = companyName;
            this.account = account;
            this.name = name;
            this.cellPhone = cellPhone;
            this.position = position;
            this.email = email;
            this.phone = phone;
            this.address = address;
            this.allowAccessType = allowAccessType;
            this.headPhotoPath = headPhotoPath;
            this.userEnable = userEnable;
            this.createUserID = createUserID;
            this.createTime = createTime;
            this.updateUserID = updateUserID;
            this.updateTime = updateTime;
            this.expireTime = expireTime;
            this.ssoUser = ssoUser;
            this.ssoToken = ssoToken;
            this.userVxEnable = userVxEnable;
            this.groupExpireTime = groupExpireTime;
        }

        public Integer getUserID() {
            return userID;
        }

        public void setUserID(Integer userID) {
            this.userID = userID;
        }

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

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCellPhone() {
            return cellPhone;
        }

        public void setCellPhone(String cellPhone) {
            this.cellPhone = cellPhone;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getAllowAccessType() {
            return allowAccessType;
        }

        public void setAllowAccessType(Integer allowAccessType) {
            this.allowAccessType = allowAccessType;
        }

        public String getHeadPhotoPath() {
            return headPhotoPath;
        }

        public void setHeadPhotoPath(String headPhotoPath) {
            this.headPhotoPath = headPhotoPath;
        }

        public Boolean getUserEnable() {
            return userEnable;
        }

        public void setUserEnable(Boolean userEnable) {
            this.userEnable = userEnable;
        }

        public Integer getCreateUserID() {
            return createUserID;
        }

        public void setCreateUserID(Integer createUserID) {
            this.createUserID = createUserID;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Integer getUpdateUserID() {
            return updateUserID;
        }

        public void setUpdateUserID(Integer updateUserID) {
            this.updateUserID = updateUserID;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Date getExpireTime() {
            return expireTime;
        }

        public void setExpireTime(Date expireTime) {
            this.expireTime = expireTime;
        }

        public Boolean getSsoUser() {
            return ssoUser;
        }

        public void setSsoUser(Boolean ssoUser) {
            this.ssoUser = ssoUser;
        }

        public String getSsoToken() {
            return ssoToken;
        }

        public void setSsoToken(String ssoToken) {
            this.ssoToken = ssoToken;
        }

        public Boolean getUserVxEnable() {
            return userVxEnable;
        }

        public void setUserVxEnable(Boolean userVxEnable) {
            this.userVxEnable = userVxEnable;
        }

        public Date getGroupExpireTime() {
            return groupExpireTime;
        }

        public void setGroupExpireTime(Date groupExpireTime) {
            this.groupExpireTime = groupExpireTime;
        }
    }

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

        public TbDepartment() {
        }

        public TbDepartment(Integer id, String name, Integer companyID, Integer parentID, String desc, Integer level,
                            Boolean readOnly, Integer displayOrder, Integer createUserID, Date createTime, Integer updateUserID,
                            Date updateTime, Boolean hasChild, Boolean delete, Integer departmentPeopleCount) {
            this.id = id;
            this.name = name;
            this.companyID = companyID;
            this.parentID = parentID;
            this.desc = desc;
            this.level = level;
            this.readOnly = readOnly;
            this.displayOrder = displayOrder;
            this.createUserID = createUserID;
            this.createTime = createTime;
            this.updateUserID = updateUserID;
            this.updateTime = updateTime;
            this.hasChild = hasChild;
            this.delete = delete;
            this.departmentPeopleCount = departmentPeopleCount;
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

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }

        public Boolean getReadOnly() {
            return readOnly;
        }

        public void setReadOnly(Boolean readOnly) {
            this.readOnly = readOnly;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        public Integer getCreateUserID() {
            return createUserID;
        }

        public void setCreateUserID(Integer createUserID) {
            this.createUserID = createUserID;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Integer getUpdateUserID() {
            return updateUserID;
        }

        public void setUpdateUserID(Integer updateUserID) {
            this.updateUserID = updateUserID;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }

        public Boolean getHasChild() {
            return hasChild;
        }

        public void setHasChild(Boolean hasChild) {
            this.hasChild = hasChild;
        }

        public Boolean getDelete() {
            return delete;
        }

        public void setDelete(Boolean delete) {
            this.delete = delete;
        }

        public Integer getDepartmentPeopleCount() {
            return departmentPeopleCount;
        }

        public void setDepartmentPeopleCount(Integer departmentPeopleCount) {
            this.departmentPeopleCount = departmentPeopleCount;
        }
    }

    public static class TbGroup {
        private Integer id;
        private Integer companyID;
        private String name;
        private String desc;
        private Integer displayOrder;
        private Integer createType;
        private Integer createUserID;
        private Date createTime;
        private Integer updateUserID;
        private Date updateTime;

        public TbGroup() {
        }

        public TbGroup(Integer id, Integer companyID, String name, String desc, Integer displayOrder, Integer createType,
                       Integer createUserID, Date createTime, Integer updateUserID, Date updateTime) {
            this.id = id;
            this.companyID = companyID;
            this.name = name;
            this.desc = desc;
            this.displayOrder = displayOrder;
            this.createType = createType;
            this.createUserID = createUserID;
            this.createTime = createTime;
            this.updateUserID = updateUserID;
            this.updateTime = updateTime;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCompanyID() {
            return companyID;
        }

        public void setCompanyID(Integer companyID) {
            this.companyID = companyID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        public Integer getCreateType() {
            return createType;
        }

        public void setCreateType(Integer createType) {
            this.createType = createType;
        }

        public Integer getCreateUserID() {
            return createUserID;
        }

        public void setCreateUserID(Integer createUserID) {
            this.createUserID = createUserID;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Integer getUpdateUserID() {
            return updateUserID;
        }

        public void setUpdateUserID(Integer updateUserID) {
            this.updateUserID = updateUserID;
        }

        public Date getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Date updateTime) {
            this.updateTime = updateTime;
        }
    }
}
