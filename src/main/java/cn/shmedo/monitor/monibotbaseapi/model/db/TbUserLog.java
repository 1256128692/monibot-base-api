package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.util.Date;

public class TbUserLog {
    private Integer ID;

    private Integer companyID;

    private Integer userID;

    private String userName;

    private Date operationDate;

    private String operationIP;

    private String moduleName;

    private String operationName;

    private String operationProperty;

    private String operationPath;

    private String operationParams;

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

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public String getOperationIP() {
        return operationIP;
    }

    public void setOperationIP(String operationIP) {
        this.operationIP = operationIP;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationProperty() {
        return operationProperty;
    }

    public void setOperationProperty(String operationProperty) {
        this.operationProperty = operationProperty;
    }

    public String getOperationPath() {
        return operationPath;
    }

    public void setOperationPath(String operationPath) {
        this.operationPath = operationPath;
    }

    public String getOperationParams() {
        return operationParams;
    }

    public void setOperationParams(String operationParams) {
        this.operationParams = operationParams;
    }
}