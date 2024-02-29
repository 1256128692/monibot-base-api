package cn.shmedo.monitor.monibotbaseapi.model.dto.checkpoint;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
public class CheckPointInfo extends CheckPointSimple {

    private Integer createUserID;

    private String createUserName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;

    private Integer updateUserID;

    private String updateUserName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    public static CheckPointInfo valueOf(TbCheckPoint entity) {

        return CheckPointInfo.builder()
                .id(entity.getID())
                .serialNumber(entity.getSerialNumber())
                .projectID(entity.getProjectID())
                .name(entity.getName())
                .enable(entity.getEnable())
                .groupID(entity.getGroupID())
                .address(entity.getAddress())
                .lastCheckTime(entity.getLastCheckTime())
                .exValue(entity.getExValue())
                .createUserID(entity.getCreateUserID())
                .createTime(entity.getCreateTime())
                .updateUserID(entity.getUpdateUserID())
                .updateTime(entity.getUpdateTime()).build();
    }
}