package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 资产仓库表
 */
@Data
@Builder
@TableName(value = "tb_asset_house")
public class TbAssetHouse {
    @TableId(value = "ID", type = IdType.AUTO)
    @JsonProperty(value = "ID")
    private Integer ID;

    @TableField(value = "CompanyID")
    private Integer companyID;

    @TableField(value = "`Name`")
    private String name;

    /**
     * 编号
     */
    @TableField(value = "Code")
    private String code;

    @TableField(value = "Address")
    private String address;

    /**
     * 备注
     */
    @TableField(value = "`Comment`")
    private String comment;

    /**
     * 联系人
     */
    @TableField(value = "ContactPerson")
    private String contactPerson;

    /**
     * 联系号码
     */
    @TableField(value = "ContactNumber")
    private String contactNumber;

    @TableField(value = "ExValue")
    private String exValue;

    @TableField(value = "CreateTime")
    private Date createTime;

    @TableField(value = "CreateUserID")
    private Integer createUserID;

    @TableField(value = "UpdateTime")
    private Date updateTime;

    @TableField(value = "UpdateUserID")
    private Integer updateUserID;
}