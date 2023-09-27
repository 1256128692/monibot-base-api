package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName(value = "tb_project_relation")
public class TbProjectRelation {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 一级工程ID
     */
    @TableField(value = "UpLevelID")
    private Integer upLevelID;

    /**
     * 二级工程ID
     */
    @TableField(value = "DownLevelID")
    private Integer downLevelID;

    /**
     * 1,2 对应1-2关系，和2-son关系
     */
    @TableField(value = "`type`")
    private Byte type;
}