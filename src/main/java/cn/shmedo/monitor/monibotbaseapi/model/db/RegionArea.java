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
import java.math.BigDecimal;

/**
 * 地址区域表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "tb_region_area")
public class RegionArea implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 层级
     */
    @TableField(value = "`level`")
    private Byte level;

    /**
     * 父级行政代码
     */
    @TableField(value = "parent_code")
    private Long parentCode;

    /**
     * 行政代码
     */
    @TableField(value = "area_code")
    private Long areaCode;

    /**
     * 邮政编码
     */
    @TableField(value = "zip_code")
    private Integer zipCode;

    /**
     * 区号
     */
    @TableField(value = "city_code")
    private String cityCode;

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
     * 组合名
     */
    @TableField(value = "merger_name")
    private String mergerName;

    /**
     * 拼音
     */
    @TableField(value = "pinyin")
    private String pinyin;

    /**
     * 经度
     */
    @TableField(value = "lng")
    private BigDecimal lng;

    /**
     * 纬度
     */
    @TableField(value = "lat")
    private BigDecimal lat;

    @Serial
    private static final long serialVersionUID = 1L;
}