package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName(value = "tb_project_service_relation")
public class TbProjectServiceRelation {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 项目ID
     */
    @TableField(value = "ProjectID")
    private Integer projectID;

    /**
     * 服务ID
     */
    @TableField(value = "ServiceID")
    private Integer serviceID;
}