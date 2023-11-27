package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class QueryControlRecordPageRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    private String keyword;

    private ControlType controlType;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime begin;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;

    @NotNull
    @Positive
    private Integer currentPage;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.BASE_PROJECT);
    }
}