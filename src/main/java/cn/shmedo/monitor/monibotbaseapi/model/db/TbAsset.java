package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资产表
 */
@Data
@Builder
@TableName(value = "tb_asset")
@AllArgsConstructor
@NoArgsConstructor
public class TbAsset {
    @TableId(value = "ID", type = IdType.AUTO)
    @JsonProperty(value = "ID")
    private Integer ID;

    @TableField(value = "CompanyID")
    private Integer companyID;

    /**
     * 型号或名称
     */
    @TableField(value = "`Name`")
    private String name;

    /**
     * 厂商或品牌
     */
    @TableField(value = "Vendor")
    private String vendor;

    /**
     * 单位
     */
    @TableField(value = "Unit")
    private Byte unit;

    /**
     * 类型
     */
    @TableField(value = "`Type`")
    private Byte type;

    /**
     * 警戒值
     */
    @TableField(value = "WarnValue")
    private Integer warnValue;

    @TableField(value = "Comparison")
    private String comparison;

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