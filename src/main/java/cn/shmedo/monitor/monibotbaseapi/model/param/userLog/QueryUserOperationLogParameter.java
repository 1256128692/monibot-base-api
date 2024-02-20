package cn.shmedo.monitor.monibotbaseapi.model.param.userLog;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Chengfs on 2024/2/20
 */
@Data
public class QueryUserOperationLogParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    @Positive
    private Integer companyID;

    private String operationType;

    @Positive
    private Integer userID;

    private String modelName;

    private String operationName;

    @Past
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime begin;

    @Past
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;

    @NotNull
    @Min(1)
    private Integer currentPage;

    @NotNull
    @Max(100)
    private Integer pageSize;

    @Override
    public ResultWrapper<?> validate() {
        if (begin.isAfter(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不允许大于结束时间");
        }

        if (begin.getYear() != end.getYear() || begin.getMonth() != end.getMonth()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "时间范围不允许超过一个月");

        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

}