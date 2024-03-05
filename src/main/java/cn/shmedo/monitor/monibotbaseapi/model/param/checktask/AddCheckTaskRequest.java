package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectServiceRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
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
    @Positive
    private Integer serviceID;

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
        Assert.notNull(project, () -> new InvalidParameterException("工程项目必须有效且不能为空"));

        TbCheckPointMapper pointMapper = SpringUtil.getBean(TbCheckPointMapper.class);
        this.points = pointMapper.selectBatchIds(pointIDList);
        Assert.isTrue(points.size() == pointIDList.size(),
                () -> new InvalidParameterException("巡检点必须有效且不能为空"));
        points.forEach(p -> {
            Assert.isTrue(p.getProjectID().equals(projectID),
                    () -> new InvalidParameterException("巡检点必须属于当前工程项目"));
            Assert.isTrue(p.getEnable(), () -> new InvalidParameterException("巡检点必须有效且不能为空"));
            Assert.isTrue(p.getServiceID().equals(serviceID),
                    () -> new InvalidParameterException("巡检点必须属于当前服务"));
        });

        TbProjectServiceRelationMapper projectRelationMapper = SpringUtil.getBean(TbProjectServiceRelationMapper.class);
        Assert.isTrue(projectRelationMapper.exists(Wrappers.<TbProjectServiceRelation>lambdaQuery()
                        .eq(TbProjectServiceRelation::getProjectID, projectID)
                        .eq(TbProjectServiceRelation::getServiceID, serviceID)),
                () -> new InvalidParameterException("所选项目必须属于所选平台"));

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
                ", serviceID=" + serviceID +
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
        entity.setServiceID(serviceID);
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