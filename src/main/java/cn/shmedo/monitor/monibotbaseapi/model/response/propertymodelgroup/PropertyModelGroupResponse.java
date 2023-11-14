package cn.shmedo.monitor.monibotbaseapi.model.response.propertymodelgroup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @Author wuxl
 * @Date 2023/9/21 15:34
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.propertymodelgroup
 * @ClassName: PropertyModelGroupResponse
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class PropertyModelGroupResponse {
    @JsonProperty("ID")
    private Integer ID;

    private Integer companyID;

    private Integer platform;

    private Integer groupType;

    private Integer groupTypeSubType;

    private String name;

    private String desc;

    private String exValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private Integer createUserID;

    private String createUserName;
}
