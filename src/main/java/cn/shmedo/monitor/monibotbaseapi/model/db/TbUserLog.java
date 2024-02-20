package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TbUserLog {

    @TableId(type = IdType.AUTO, value = "ID")
    private Integer ID;

    @TableField(value = "CompanyID")
    private Integer companyID;

    @TableField(value = "UserID")
    private Integer userID;

    @TableField(value = "UserName")
    private String userName;

    @TableField(value = "OperationDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime operationDate;

    @TableField(value = "OperationIP")
    private String operationIP;

    @TableField(value = "ModuleName")
    private String moduleName;

    @TableField(value = "OperationName")
    private String operationName;

    @TableField(value = "OperationProperty")
    private String operationProperty;

    @TableField(value = "OperationPath")
    private String operationPath;

    @TableField(value = "OperationParams")
    private String operationParams;

}