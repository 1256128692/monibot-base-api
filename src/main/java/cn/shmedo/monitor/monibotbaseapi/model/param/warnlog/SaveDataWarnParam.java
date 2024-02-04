package cn.shmedo.monitor.monibotbaseapi.model.param.warnlog;

import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnCase;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.Date;

/**
 * @author Chengfs on 2024/1/12
 */
@Data
public class SaveDataWarnParam {

    /**
     * 报警阈值配置 id
     */
    @Positive
    private Integer thresholdID;

    /**
     * 报警等级 1-4
     */
    @Range(min = 0, max = 4)
    private Integer warnLevel;

    /**
     * 报警字段值
     */
    @NotNull
    private Double warnValue;

    @NotNull
    private Integer sensorID;

    /**
     * 报警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date warnTime;

    @JsonIgnore
    private String warnContent;

    @JsonIgnore
    private DataWarnCase warnCase;

    @JsonIgnore
    private String warnLevelName;

}