package cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDataWarnLog;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * @author Chengfs on 2024/1/12
 */
@Data
public class DataWarnDto {

    /**
     * 报警阈值配置 id
     */
    private Integer thresholdID;

    /**
     * 报警等级 1-4
     */
    private Integer warnLevel;

    /**
     * 报警字段值
     */
    private Double warnValue;

    /**
     * 报警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date warnTime;

    @JsonIgnore
    private Integer companyID;

    @JsonIgnore
    private Integer platform;

    @JsonIgnore
    private Integer projectID;

    @JsonIgnore
    private Integer monitorType;

    @JsonIgnore
    private Integer monitorItemID;

    @JsonIgnore
    private Integer sensorID;

    @JsonIgnore
    private Integer fieldID;

    @JsonIgnore
    private String projectName;

    @JsonIgnore
    private String monitorTypeName;

    @JsonIgnore
    private String monitorItemName;

    @JsonIgnore
    private String sensorName;

    @JsonIgnore
    private String fieldName;

    @JsonIgnore
    private String fieldUnitEng;

    @JsonIgnore
    private String warnName;

    @JsonIgnore
    private String warnContent;

    public TbDataWarnLog toTbDataWarnLog(){
        TbDataWarnLog entity = new TbDataWarnLog();
        entity.setCompanyID(this.getCompanyID());
        entity.setPlatform(this.getPlatform());
        entity.setWarnThresholdID(this.getThresholdID());
        entity.setWarnLevel(this.getWarnLevel());
        entity.setWarnTime(this.getWarnTime());
        entity.setDataStatus(0);
        entity.setWarnContent(this.getWarnContent());
        return entity;
    }

    public void perfect(WarnThresholdConfigInfo entity){
        this.setCompanyID(entity.getCompanyID());
        this.setPlatform(entity.getPlatform());
        this.setProjectID(entity.getProjectID());
        this.setMonitorType(entity.getMonitorType());
        this.setMonitorItemID(entity.getMonitorItemID());
        this.setSensorID(entity.getSensorID());
        this.setFieldID(entity.getFieldID());
        this.setProjectName(entity.getProjectName());
        this.setMonitorTypeName(entity.getMonitorTypeName());
        this.setMonitorItemName(entity.getMonitorItemName());
        this.setSensorName(entity.getSensorName());
        this.setFieldName(entity.getFieldName());
        this.setFieldUnitEng(entity.getFieldUnitEng());
        this.setWarnName(entity.getWarnName());
    }
}