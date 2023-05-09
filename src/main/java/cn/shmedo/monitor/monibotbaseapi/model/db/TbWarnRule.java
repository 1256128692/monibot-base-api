package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serial;
import java.util.Date;

public class TbWarnRule {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 规则类型, 1:报警规则 2:视频规则 3:智能终端规则
     */
    private Byte ruleType;

    /**
     * 工程项目ID
     */
    private Integer projectID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 监测项目ID
     */
    private Integer monitorItemID;

    /**
     * 监测点ID
     */
    private Integer monitorPointID;

    /**
     * 传感器ID
     */
    private Integer sensorID;

    /**
     * 产品ID
     */
    private Integer productID;

    /**
     * 设备SN列表
     */
    private String deviceCSV;

    /**
     * 视频设备型号
     */
    private String videoType;

    /**
     * 视频SN列表
     */
    private String videoCSV;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 启用状态
     */
    private Boolean enable;

    /**
     * 拓展信息
     */
    private String exValue;

    /**
     * 规则描述
     */
    @TableField(value = "`desc`")
    private String desc;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建用户
     */
    private Integer createUserID;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改用户
     */
    private Integer updateUserID;

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Byte getRuleType() {
        return ruleType;
    }

    public void setRuleType(Byte ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(Integer monitorType) {
        this.monitorType = monitorType;
    }

    public Integer getMonitorItemID() {
        return monitorItemID;
    }

    public void setMonitorItemID(Integer monitorItemID) {
        this.monitorItemID = monitorItemID;
    }

    public Integer getMonitorPointID() {
        return monitorPointID;
    }

    public void setMonitorPointID(Integer monitorPointID) {
        this.monitorPointID = monitorPointID;
    }

    public Integer getSensorID() {
        return sensorID;
    }

    public void setSensorID(Integer sensorID) {
        this.sensorID = sensorID;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
    }

    public String getDeviceCSV() {
        return deviceCSV;
    }

    public void setDeviceCSV(String deviceCSV) {
        this.deviceCSV = deviceCSV;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoCSV() {
        return videoCSV;
    }

    public void setVideoCSV(String videoCSV) {
        this.videoCSV = videoCSV;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getExValue() {
        return exValue;
    }

    public void setExValue(String exValue) {
        this.exValue = exValue;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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