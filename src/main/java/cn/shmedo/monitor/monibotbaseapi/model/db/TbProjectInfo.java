package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.util.Date;

public class TbProjectInfo {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 公司ID
    */
    private Integer companyID;

    /**
    * 项目名称
    */
    private String projectName;

    /**
    * 项目简称
    */
    private String shortName;

    /**
    * 项目类型
    */
    private Byte projectType;

    /**
    * 项目有效期
    */
    private Date expiryDate;

    /**
    * 直管单位
    */
    private String directManageUnit;

    /**
    * 平台类型
    */
    private Byte platformType;

    /**
    * 是否可用，需要配合有效期使用0:可用，1不可用
    */
    private Boolean enable;

    /**
    * 项目位置信息
    */
    private String location;

    /**
    * 项目地址
    */
    private String projectAddress;

    /**
    * 项目经度
    */
    private Double latitude;

    /**
    * 项目纬度
    */
    private Double longitude;

    /**
    * 项目图片地址
    */
    private String imagePath;

    /**
    * 项目简介
    */
    private String projectDesc;

    /**
    * 模板ID
    */
    private Integer modelID;

    /**
    * 创建时间
    */
    //@TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
    * 创建用户ID
    */
    //@TableField(fill = FieldFill.INSERT)
    private Integer createUserID;

    /**
    * 修改时间
    */
    //@TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
    * 修改用户ID
    */
    //@TableField(fill = FieldFill.INSERT_UPDATE)
    private Integer updateUserID;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Byte getProjectType() {
        return projectType;
    }

    public void setProjectType(Byte projectType) {
        this.projectType = projectType;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDirectManageUnit() {
        return directManageUnit;
    }

    public void setDirectManageUnit(String directManageUnit) {
        this.directManageUnit = directManageUnit;
    }

    public Byte getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Byte platformType) {
        this.platformType = platformType;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public Integer getModelID() {
        return modelID;
    }

    public void setModelID(Integer modelID) {
        this.modelID = modelID;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserID() {
        return createUserID;
    }

    public void setCreateUserID(Integer createUserID) {
        this.createUserID = createUserID;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUserID() {
        return updateUserID;
    }

    public void setUpdateUserID(Integer updateUserID) {
        this.updateUserID = updateUserID;
    }
}