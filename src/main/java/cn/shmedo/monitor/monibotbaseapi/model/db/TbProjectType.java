package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

/**
 * 项目类型
 */
@Data
@Builder
@TableName(value = "tb_project_type")
public class TbProjectType {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Byte ID;

    /**
     * 类型名称
     */
    @TableField(value = "TypeName")
    private String typeName;

    /**
     * 主类型名称
     */
    @TableField(value = "MainType")
    private String mainType;

}