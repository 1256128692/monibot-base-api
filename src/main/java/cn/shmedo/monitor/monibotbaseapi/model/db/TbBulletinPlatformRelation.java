package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 11:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_bulletin_platform_relation")
public class TbBulletinPlatformRelation {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 所属平台
     */
    @TableField("Platform")
    private Integer platform;
    /**
     * 公告ID
     */
    @TableField("BulletinID")
    private Integer bulletinID;
}
