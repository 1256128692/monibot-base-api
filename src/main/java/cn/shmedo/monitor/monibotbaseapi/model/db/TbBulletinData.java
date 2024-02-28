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
 * @date 2024-02-27 17:36
 */
@Data
@TableName("tb_bulletin_data")
@NoArgsConstructor
@AllArgsConstructor
public class TbBulletinData {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 公司ID
     */
    @TableField("CompanyID")
    private Integer companyID;
    /**
     * 0.其他 1.行业政策 2.重要新闻 3.工作公告
     */
    @TableField("`Type`")
    private Integer type;
    /**
     * 名称
     */
    @TableField("`Name`")
    private String name;
    /**
     * 公告内容
     */
    @TableField("Content")
    private String content;
    /**
     * 其他信息
     */
    @TableField("ExValue")
    private String exValue;
    /**
     * 发布状态 0.未发布 1.已发布
     */
    @TableField("`Status`")
    private Integer status;
    /**
     * 是否置顶
     */
    @TableField("TopMost")
    private Boolean topMost;
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
    /**
     * 修改时间
     */
    @TableField("UpdateTime")
    private Date updateTime;
    /**
     * 修改部门及修改人信息
     */
    @TableField("UpdateUser")
    private String updateUser;
}
