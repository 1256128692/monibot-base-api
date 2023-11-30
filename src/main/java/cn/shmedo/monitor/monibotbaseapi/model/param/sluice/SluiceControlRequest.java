package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionKind;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Set;

import static cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionType.*;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class SluiceControlRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer projectID;

    private Integer gateID;

    @NotNull
    private ControlActionKind actionKind;

    private ControlActionType actionType;

    @Valid
    private Target target;

    @Data
    @Validated
    public static class Target {

        @NotNull(groups = {ConstantWaterLevel.class}, message = "waterLevel must not be null")
        @Positive(groups = {ConstantWaterLevel.class}, message = "waterLevel must be positive")
        private Double waterLevel;

        @NotNull(groups = {ConstantFlow.class}, message = "flowRate must not be null")
        @Positive(groups = {ConstantFlow.class}, message = "flowRate must be positive")
        private Double flowRate;

        @NotNull(groups = {ConstantSluiceLevel.class}, message = "gateDegree must not be null")
        @Positive(groups = {ConstantSluiceLevel.class}, message = "gateDegree must be positive")
        private Double gateDegree;

        @NotNull(groups = {TimeControl.class}, message = "openTime must not be null")
        @Past(groups = {TimeControl.class}, message = "openTime must be past")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime openTime;

        @NotNull(groups = {TimeControl.class}, message = "duration must not be null")
        @Positive(groups = {TimeControl.class}, message = "duration must be positive")
        private Integer duration;

        @NotNull(groups = {TimePeriodControl.class}, message = "beginTime must not be null")
        @Past(groups = {TimePeriodControl.class}, message = "beginTime must be past")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime beginTime;

        @NotNull(groups = {TimePeriodControl.class}, message = "endTime must not be null")
        @Past(groups = {TimePeriodControl.class}, message = "endTime must be past")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime endTime;

        @NotNull(groups = {TotalControl.class}, message = "totalFlow must not be null")
        @Positive(groups = {TotalControl.class}, message = "totalFlow must be positive")
        private Integer totalFlow;
    }

    @Override
    public ResultWrapper<?> validate() {
        if (gateID == null) {
            Assert.isTrue(ControlActionKind.OPEN.equals(actionKind) ||
                    ControlActionKind.CLOSE.equals(actionKind));
        } else {
            if (ControlActionKind.AUTO.equals(actionKind)) {
                Assert.isTrue(actionType != null && target != null);

                //校验参数
                Validator validator = ContextHolder.getBean(Validator.class);
                Set<ConstraintViolation<Target>> validate = validator.validate(target, actionType.getValidGroup());
                if (!validate.isEmpty()) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, validate.iterator().next().getMessage());
                }

                if (TIME_PERIOD_CONTROL.equals(actionType) ) {
                    Assert.isTrue(target.getBeginTime().isBefore(target.getEndTime()), "beginTime must be before endTime");
                }
            } else {
                Assert.isTrue(actionType == null);
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}