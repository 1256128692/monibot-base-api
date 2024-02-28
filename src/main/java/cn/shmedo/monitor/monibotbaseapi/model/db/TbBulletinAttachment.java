package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-27 17:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_bulletin_attachment")
public class TbBulletinAttachment {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 文件类型 1.公告文件附件; 2.公告内嵌文件
     */
    @TableField("`Type`")
    private Integer type;
    /**
     * 公告ID
     */
    @TableField("BulletinID")
    private Integer bulletinID;
    /**
     * 公告文件附件:文件oss-key; 公告内嵌文件:文件绝对路径
     */
    @TableField("FilePath")
    private String filePath;
    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date createTime;
    /**
     * 创建部门及创建人信息
     */
    @TableField("CreateUser")
    private String createUser;
}
