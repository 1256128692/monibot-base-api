package cn.shmedo.monitor.monibotbaseapi.model.db;

import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskEvaluate;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskType;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 巡检任务表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_check_task")
public class TbCheckTask {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 关联项目ID
     */
    @TableField(value = "ProjectID")
    private Integer projectID;

    /**
     * 任务编号 形如：XJ202310020001
     */
    @TableField(value = "SerialNumber")
    private String serialNumber;

    /**
     * 巡检类型 0-其他 1-日常巡检 2-设备巡查 3-隐患点检查 4-安全检查
     */
    @TableField(value = "CheckType")
    private CheckTaskType checkType;

    /**
     * 任务名称
     */
    @TableField(value = "`Name`")
    private String name;

    /**
     * 任务状态 0-未开始 1-进行中 2-已过期 3-已结束
     */
    @TableField(value = "`Status`")
    private CheckTaskStatus status;

    /**
     * 任务日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @TableField(value = "TaskDate")
    private LocalDate taskDate;

    /**
     * 任务开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "BeginTime")
    private LocalDateTime beginTime;

    /**
     * 任务结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "EndTime")
    private LocalDateTime endTime;

    /**
     * 巡检人
     */
    @TableField(value = "CheckerID")
    private Integer checkerID;

    /**
     * 巡检轨迹
     */
    @TableField(value = "Trajectory")
    private String trajectory;

    /**
     * 巡检评价 0-正常 1-异常
     */
    @TableField(value = "Evaluate")
    private CheckTaskEvaluate evaluate;

    /**
     * 扩展字段
     */
    @TableField(value = "ExValue")
    private String exValue;

    /**
     * 创建人
     */
    @TableField(value = "CreateUserID")
    private Integer createUserID;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "CreateTime", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @TableField(value = "UpdateUserID")
    private Integer updateUserID;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "UpdateTime", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime updateTime;
}