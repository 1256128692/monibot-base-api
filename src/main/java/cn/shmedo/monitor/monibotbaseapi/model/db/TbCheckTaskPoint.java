package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 巡检任务关联巡检点表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_check_task_point")
public class TbCheckTaskPoint {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 关联任务ID
     */
    @TableField(value = "TaskID")
    private Integer taskID;

    /**
     * 关联巡检点ID
     */
    @TableField(value = "PointID")
    private Integer pointID;

    /**
     * 附件文件osskey集合
     */
    @TableField(value = "Annexes")
    private String annexes;

    /**
     * 巡检备注
     */
    @TableField(value = "Remark")
    private String remark;

    /**
     * 巡检点信息
     */
    @TableField(value = "PointInfo")
    private String pointInfo;

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