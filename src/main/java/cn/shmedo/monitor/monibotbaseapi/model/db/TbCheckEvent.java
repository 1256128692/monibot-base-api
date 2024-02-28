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
 * 巡检事件表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_check_event")
public class TbCheckEvent {
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
     * 工单ID
     */
    @TableField(value = "OrderID")
    private Integer orderID;

    /**
     * 关联巡检任务ID
     */
    @TableField(value = "TaskID")
    private Integer taskID;

    /**
     * 事件类型ID
     */
    @TableField(value = "TypeID")
    private Integer typeID;

    /**
     * 事件编码 形如 E2024020200001
     */
    @TableField(value = "SerialNumber")
    private String serialNumber;

    /**
     * 事件位置
     */
    @TableField(value = "Address")
    private String address;

    /**
     * 事件经纬度
     */
    @TableField(value = "`Location`")
    private String location;

    /**
     * 事件描述
     */
    @TableField(value = "`Describe`")
    private String describe;

    /**
     * 附件文件osskey
     */
    @TableField(value = "Annexes")
    private String annexes;

    /**
     * 提报人
     */
    @TableField(value = "ReportUserID")
    private Integer reportUserID;

    /**
     * 提报时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @TableField(value = "ReportTime")
    private LocalDateTime reportTime;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @TableField(value = "HandleTime")
    private LocalDateTime handleTime;

    /**
     * 事件状态 0-未处理 1-已处理
     */
    @TableField(value = "`Status`")
    private Integer status;

    /**
     * 结束批注
     */
    @TableField(value = "`Comment`")
    private String comment;

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