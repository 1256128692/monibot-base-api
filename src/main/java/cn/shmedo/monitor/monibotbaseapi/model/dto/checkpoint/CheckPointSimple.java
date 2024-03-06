package cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CheckPointSimple {

    private Integer id;

    private String serialNumber;

    private Integer projectID;

    private String projectName;

    private String name;

    private Boolean enable;

    private Integer groupID;

    private String groupName;

    private String address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastCheckTime;

    private String exValue;

    private Boolean existRunningTask;
}