package cn.shmedo.monitor.monibotbaseapi.model.dto.checktsak;

import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckTaskSimple {

    private Integer id;
    private Integer projectID;
    private String projectName;
    private String serialNumber;
    private CheckTaskType checkType;
    private String name;
    private CheckTaskStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate taskDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDateTime endTime;

    private Integer checkerID;
    private String checkerName;
    private String exValue;
}