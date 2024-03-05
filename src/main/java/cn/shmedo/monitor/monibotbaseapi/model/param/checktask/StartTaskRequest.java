package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTaskPoint;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Chengfs on 2024/3/4
 */
@Data
public class StartTaskRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer taskID;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @JsonIgnore
    private Integer projectID;

    @JsonIgnore
    private CurrentSubject subject;

    @Override
    public ResultWrapper<?> validate() {
        LocalDate today = LocalDate.now();
        if (this.startTime == null) {
            this.startTime = LocalDateTime.now();
        } else {
            Assert.isTrue(this.startTime.toLocalDate().equals(today),
                    () -> new InvalidParameterException("开始时间不能超出任务日期"));
        }


        this.subject = CurrentSubjectHolder.getCurrentSubject();

        TbCheckTaskMapper mapper = SpringUtil.getBean(TbCheckTaskMapper.class);
        TbCheckTask task = mapper.selectOne(Wrappers.<TbCheckTask>lambdaQuery()
                .eq(TbCheckTask::getID, taskID)
                .select(TbCheckTask::getProjectID, TbCheckTask::getCheckerID, TbCheckTask::getTaskDate));

        Assert.isTrue(task.getCheckerID().equals(subject.getSubjectID()),
                () -> new InvalidParameterException("巡检任务必须属于当前用户"));

        Assert.isTrue(task.getTaskDate().equals(today),
                () -> new InvalidParameterException("巡检任务必须属于当天"));

        TbCheckTaskPointMapper taskPointMapper = SpringUtil.getBean(TbCheckTaskPointMapper.class);
        Assert.isTrue(taskPointMapper.selectCount(Wrappers.<TbCheckTaskPoint>lambdaQuery()
                .eq(TbCheckTaskPoint::getTaskID, taskID)) > 0,
                () -> new InvalidParameterException("巡检任务需要至少关联1个巡检点"));

        this.projectID = task.getProjectID();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public String toString() {
        return "StartTaskRequest{" +
                "taskID=" + taskID +
                ", startTime=" + startTime +
                '}';
    }
}