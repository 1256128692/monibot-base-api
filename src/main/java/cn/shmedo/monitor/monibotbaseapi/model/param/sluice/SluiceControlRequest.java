package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionKind;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

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

    private Target target;

    @Data
    public static class Target {

        @NotNull(groups = {ConstantWaterLevel.class})
        @Positive(groups = {ConstantWaterLevel.class})
        private Double waterLevel;

        @NotNull(groups = {ConstantFlow.class})
        @Positive(groups = {ConstantFlow.class})
        private Double flowRate;

        @NotNull(groups = {ConstantSluiceLevel.class})
        @Positive(groups = {ConstantSluiceLevel.class})
        private Double gateDegree;

        @NotNull(groups = {TimeControl.class})
        @Past(groups = {TimeControl.class})
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime openTime;

        @NotNull(groups = {TimeControl.class})
        @Positive(groups = {TimeControl.class})
        private Integer duration;

        @NotNull(groups = {TimePeriodControl.class})
        @Past(groups = {TimePeriodControl.class})
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime beginTime;

        @NotNull(groups = {TimePeriodControl.class})
        @Past(groups = {TimePeriodControl.class})
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime endTime;

        @NotNull(groups = {TotalControl.class})
        @Positive(groups = {TotalControl.class})
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
                Validator validator = ContextHolder.getBean(Validator.class);
                validator.validate(target, actionType.getValidGroup());
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