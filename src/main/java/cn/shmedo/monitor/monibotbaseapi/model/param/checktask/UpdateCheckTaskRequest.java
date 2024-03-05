package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTaskPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskType;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
public class UpdateCheckTaskRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer id;

    @NotNull
    private CheckTaskType checkType;

    @Size(max = 255)
    private String name;

    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate taskDate;

    @Positive
    private Integer checkerID;

    @NotEmpty
    private Set<@Positive Integer> pointIDList;

    private String exValue;

    @JsonIgnore
    private List<TbCheckPoint> points;

    @JsonIgnore
    private TbCheckTask task;

    @JsonIgnore
    private Integer subjectID;

    @Override
    public ResultWrapper<?> validate() {
        this.pointIDList = Optional.ofNullable(pointIDList).orElse(Set.of()).stream()
                .filter(e -> e != null && e > 0).collect(Collectors.toSet());
        Assert.isFalse(pointIDList.isEmpty(), () -> new InvalidParameterException("巡检点必须有效且不能为空"));


        TbCheckTaskMapper taskMapper = SpringUtil.getBean(TbCheckTaskMapper.class);
        this.task = taskMapper.selectById(id);
        Assert.notNull(task, () -> new InvalidParameterException("巡检任务不存在"));
        Assert.isTrue(CheckTaskStatus.UN_START.equals(task.getStatus()),
                () -> new InvalidParameterException("巡检任务不处于未开始状态"));

        TbCheckPointMapper pointMapper = SpringUtil.getBean(TbCheckPointMapper.class);
        this.points = pointMapper.selectBatchIds(pointIDList);
        Assert.isTrue(points.size() == pointIDList.size(),
                () -> new InvalidParameterException("巡检点必须有效且不能为空"));
        points.forEach(p -> {
            Assert.isTrue(p.getProjectID().equals(task.getProjectID()),
                    () -> new InvalidParameterException("巡检点必须位于同一项目中"));
            Assert.isTrue(p.getEnable(), () -> new InvalidParameterException("巡检点必须为启用状态"));
        });
        this.subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(task.getProjectID().toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public String toString() {
        return "UpdateCheckTaskRequest{" +
                "id=" + id +
                ", checkType=" + checkType +
                ", name='" + name + '\'' +
                ", taskDate=" + taskDate +
                ", checkerID=" + checkerID +
                ", pointIDList=" + pointIDList +
                ", exValue='" + exValue + '\'' +
                '}';
    }

    public TbCheckTask toEntity() {
        TbCheckTask entity = new TbCheckTask();
        entity.setID(id);
        entity.setCheckType(checkType);
        entity.setName(Optional.ofNullable(name)
                .filter(e -> !e.isBlank())
                .orElseGet(() -> {
                    TbProjectInfoMapper projectMapper = SpringUtil.getBean(TbProjectInfoMapper.class);
                    TbProjectInfo project = projectMapper.selectOne(Wrappers.<TbProjectInfo>lambdaQuery()
                            .eq(TbProjectInfo::getID, task.getProjectID())
                            .select(TbProjectInfo::getProjectName));
                    return project.getProjectName() + checkType.getDesc() + "任务";
                }));
        Optional.ofNullable(taskDate).ifPresent(entity::setTaskDate);
        Optional.ofNullable(checkerID).ifPresent(entity::setCheckerID);
        Optional.ofNullable(exValue).ifPresent(entity::setExValue);
        entity.setUpdateUserID(subjectID);
        return entity;
    }

    public List<TbCheckTaskPoint> toEntities(Integer taskID) {
        return points.stream().map(p -> {
            TbCheckTaskPoint point = new TbCheckTaskPoint();
            point.setTaskID(taskID);
            point.setPointID(p.getID());
            point.setPointInfo(JSONUtil.toJsonStr(p));
            point.setUpdateUserID(subjectID);
            point.setCreateUserID(subjectID);
            return point;
        }).toList();
    }
}