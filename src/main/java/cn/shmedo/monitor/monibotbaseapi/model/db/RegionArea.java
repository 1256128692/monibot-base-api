package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 地址区域表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_region_area")
public class RegionArea implements Serializable {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**
     * 父级
     */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
     * 名称
     */
    @TableField(value = "`name`")
    private String name;

    /**
     * 简称
     */
    @TableField(value = "short_name")
    private String shortName;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    private String longitude;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    private String latitude;

    /**
     * 级别 1 省 2 市 3 区 4 乡镇/街道
     */
    @TableField(value = "`level`")
    private Short level;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 状态 0 无效 1 有效
     */
    @TableField(value = "`status`")
    private Boolean status;

    @Serial
    private static final long serialVersionUID = 1L;
}