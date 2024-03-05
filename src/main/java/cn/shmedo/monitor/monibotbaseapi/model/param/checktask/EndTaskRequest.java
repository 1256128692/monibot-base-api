package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTaskPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/3/4
 */
@Data
public class EndTaskRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer taskID;

    @NotEmpty
    private Set<Note> notes;

    @NotNull
    @Range(min = 0, max = 1)
    private Integer evaluate;

    private String trajectory;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    private String exValue;

    @JsonIgnore
    private CurrentSubject subject;

    @JsonIgnore
    private Integer projectID;

    @JsonIgnore
    private List<TbCheckTaskPoint> points;

    @JsonIgnore
    private Map<Integer, Note> noteMap;

    @Data
    public static class Note {

        @NotNull
        @Positive
        private Integer pointID;

        @NotEmpty
        private Set<@NotEmpty String> annexes;

        private String remark;

        @Override
        public int hashCode() {
            return pointID;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Note note) {
                return Objects.equals(pointID, note.pointID);
            }
            return false;
        }
    }

    @Override
    public ResultWrapper<?> validate() {
        LocalDate today = LocalDate.now();
        Assert.isTrue(this.endTime.toLocalDate().equals(today),
                () -> new InvalidParameterException("结束时间不能超出任务日期"));

        this.notes = Optional.ofNullable(notes).orElse(Set.of()).stream()
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Assert.isFalse(notes.isEmpty(), () -> new InvalidParameterException("notes 必须包含有效元素且不能为空"));
        notes.forEach(note -> {
            note.setAnnexes(Optional.ofNullable(note.getAnnexes()).orElse(Set.of()).stream()
                    .filter(e -> e!= null && !e.isBlank()).collect(Collectors.toSet()));
            Assert.isFalse(note.getAnnexes().isEmpty(),
                    () -> new InvalidParameterException("annexes 必须包含有效元素且不能为空"));
        });


        this.subject = CurrentSubjectHolder.getCurrentSubject();
        TbCheckTaskMapper mapper = SpringUtil.getBean(TbCheckTaskMapper.class);
        TbCheckTask task = mapper.selectOne(Wrappers.<TbCheckTask>lambdaQuery()
                .eq(TbCheckTask::getID, taskID)
                .select(TbCheckTask::getProjectID, TbCheckTask::getCheckerID,
                        TbCheckTask::getTaskDate, TbCheckTask::getBeginTime, TbCheckTask::getStatus));

        // 检查任务归属、日期以及状态
        Assert.isTrue(task.getCheckerID().equals(subject.getSubjectID()),
                () -> new InvalidParameterException("任务不属于当前用户"));
        Assert.isTrue(task.getTaskDate().equals(LocalDate.now()),
                () -> new InvalidParameterException("任务不属于当天"));
        Assert.isTrue(CheckTaskStatus.PROCESSING.equals(task.getStatus()),
                () -> new InvalidParameterException("任务" + task.getStatus().getDesc()));
        Assert.isTrue(task.getBeginTime().isBefore(this.endTime),
                () -> new InvalidParameterException("任务结束时间不能早于开始时间"));

        //校验巡检记录
        this.noteMap = notes.stream().collect(Collectors.toMap(e -> e.pointID, e -> e));
        TbCheckTaskPointMapper pointMapper = SpringUtil.getBean(TbCheckTaskPointMapper.class);
        this.points = pointMapper.selectList(Wrappers.<TbCheckTaskPoint>lambdaQuery()
                .eq(TbCheckTaskPoint::getTaskID, taskID)
                .select(TbCheckTaskPoint::getID, TbCheckTaskPoint::getPointID));
        points.forEach(item -> Assert.isTrue(noteMap.containsKey(item.getPointID()),
                "巡检记录必须包含所有巡检点"));

        this.projectID = task.getProjectID();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public String toString() {
        return "EndTaskRequest{" +
                "taskID=" + taskID +
                ", notes=" + notes +
                ", evaluate=" + evaluate +
                ", trajectory='" + trajectory + '\'' +
                ", endTime=" + endTime +
                ", exValue='" + exValue + '\'' +
                '}';
    }

    public List<TbCheckTaskPoint> getTaskPoints() {
        return points.stream().map(item -> {
            Note note = this.noteMap.get(item.getPointID());

            TbCheckTaskPoint entity = new TbCheckTaskPoint();
            entity.setID(item.getID());
            entity.setAnnexes(JSONUtil.toJsonStr(note.annexes));
            entity.setRemark(note.remark);
            entity.setUpdateUserID(subject.getSubjectID());
            return entity;
        }).toList();

    }
}
