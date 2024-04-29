package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tb_favorite
 */
@TableName(value ="tb_favorite")
@Accessors(chain = true)
@ToString
@Data
public class TbFavorite implements Serializable {
    /**
     * 
     */
    @TableField(value = "ID")
    @TableId(type = IdType.AUTO)
    private Integer ID;

    /**
     * 公司ID
     */
    private Integer companyID;

    /**
     * 收藏类型（0-企业收藏监测项目；）
     */
    private Integer subjectType;

    /**
     * 收藏对象ID
     */
    private Integer subjectID;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}