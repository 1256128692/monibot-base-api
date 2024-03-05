package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 巡检点表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_check_point")
public class TbCheckPoint {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 关联工程项目ID
     */
    @TableField(value = "ProjectID")
    private Integer projectID;

    /**
     * 关联平台ID
     */
    @TableField(value = "ServiceID")
    private Integer serviceID;

    /**
     * 巡检点编码 形如XJ202400001
     */
    @TableField(value = "SerialNumber")
    private String serialNumber;

    /**
     * 巡检点名称
     */
    @TableField(value = "`Name`")
    private String name;

    /**
     * 是否启用
     */
    @TableField(value = "`Enable`")
    private Boolean enable;

    /**
     * 关联巡检组ID
     */
    @TableField(value = "GroupID")
    private Integer groupID;

    /**
     * 巡检点地址
     */
    @TableField(value = "Address")
    private String address;

    /**
     * 巡检点地址经纬度
     */
    @TableField(value = "`Location`")
    private String location;

    /**
     * 最后巡检时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "LastCheckTime")
    private LocalDateTime lastCheckTime;

    /**
     * 扩展字段
     */
    @TableField(value = "ExValue")
    @Size(max = 255, message = "扩展字段最大长度要小于 255")
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