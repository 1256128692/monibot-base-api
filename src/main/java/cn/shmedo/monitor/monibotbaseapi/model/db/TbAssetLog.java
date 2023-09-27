package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 资产日志
 */
@Data
@Builder
@TableName(value = "tb_asset_log")
public class TbAssetLog {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    @TableField(value = "HouseID")
    private Integer houseID;

    @TableField(value = "AssetID")
    private Integer assetID;

    /**
     * 出入库值，有正负
     */
    @TableField(value = "`Value`")
    private Integer value;

    /**
     * 备注
     */
    @TableField(value = "`Comment`")
    private String comment;

    @TableField(value = "`Time`")
    private Date time;

    @TableField(value = "UserID")
    private Integer userID;

    @TableField(value = "UserName")
    private String userName;
}