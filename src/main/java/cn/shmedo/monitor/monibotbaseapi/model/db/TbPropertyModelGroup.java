package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName tb_property_model_group
 */
@Data
public class TbPropertyModelGroup implements Serializable {
    /**
     * 主键ID
     */
    private Integer ID;

    /**
     * 公司ID
     */
    private Integer companyID;

    /**
     * 组名称
     */
    private String groupname;

    /**
     * 描述
     */
    private String desc;

    /**
     * 扩展字段
     */
    private String exvalues;

    private static final long serialVersionUID = 1L;
}