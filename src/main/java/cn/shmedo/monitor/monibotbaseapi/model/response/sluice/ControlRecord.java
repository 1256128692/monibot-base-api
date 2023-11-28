package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class ControlRecord {

    private Long id;
    private Integer projectID;
    private String projectName;
    private Integer gateID;
    private String gateName;
    private String canal;
    private String sluiceType;
    private String mmUnit;
    private ControlType controlType;
    private Integer actionType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime operationTime;
    private Integer operationUserID;
    private String operationUser;

}