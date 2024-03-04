package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTaskPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskType;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
public class AddCheckTaskRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @NotNull
    @Positive
    private Integer projectID;

    @NotNull
    private CheckTaskType checkType;

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
    private TbProjectInfo project;

    @JsonIgnore
    private Integer subjectID;

    @Override
    public ResultWrapper<?> validate() {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        this.taskDate = Optional.ofNullable(taskDate).orElse(LocalDate.now());
        this.checkerID = Optional.ofNullable(checkerID).orElse(subject.getSubjectID());

        TbProjectInfoMapper projectMapper = SpringUtil.getBean(TbProjectInfoMapper.class);
        this.project = projectMapper.selectOne(Wrappers.<TbProjectInfo>lambdaQuery()
                .eq(TbProjectInfo::getID, projectID)
                .select(TbProjectInfo::getProjectName, TbProjectInfo::getID));
        Assert.notNull(project, () -> new InvalidParameterException("项目不存在"));

        TbCheckPointMapper pointMapper = SpringUtil.getBean(TbCheckPointMapper.class);
        this.points = pointMapper.selectBatchIds(pointIDList);
        Assert.isTrue(points.size() == pointIDList.size(),
                () -> new InvalidParameterException("包含不存在的巡检点"));
        points.forEach(p -> {
            Assert.isTrue(p.getProjectID().equals(projectID),
                    () -> new InvalidParameterException("包含不属于当前项目的巡检点"));
            Assert.isTrue(p.getEnable(), () -> new InvalidParameterException("包含未启用的巡检点"));
        });
        this.subjectID = CurrentSubjectHolder.getCurrentSubject().getSubjectID();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public String toString() {
        return "AddCheckTaskRequest{" +
                "companyID=" + companyID +
                ", projectID=" + projectID +
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
        entity.setProjectID(projectID);
        entity.setCheckType(checkType);
        entity.setName(Optional.ofNullable(name).filter(e -> !e.isEmpty())
                .orElse(project.getProjectName() + checkType.getDesc() + "任务"));
        entity.setTaskDate(taskDate);
        entity.setCheckerID(checkerID);
        entity.setExValue(exValue);
        entity.setCreateUserID(subjectID);
        entity.setUpdateUserID(subjectID);
        return entity;
    }

    public List<TbCheckTaskPoint> toEntities(Integer taskID) {
        return points.stream().map(p -> {
            TbCheckTaskPoint point = new TbCheckTaskPoint();
            point.setTaskID(taskID);
            point.setPointID(p.getID());
            point.setPointInfo(JSONUtil.toJsonStr(p));
            point.setCreateUserID(subjectID);
            point.setUpdateUserID(subjectID);
            return point;
        }).toList();
    }
}