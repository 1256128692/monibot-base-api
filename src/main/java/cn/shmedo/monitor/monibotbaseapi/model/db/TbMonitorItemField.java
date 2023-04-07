package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 监测项目监测属性关联表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbMonitorItemField implements Serializable {
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 监测项目ID
     */
    private Integer monitorItemID;

    /**
     * 监测属性ID
     */
    private Integer monitorTypeFieldID;

    private static final long serialVersionUID = 1L;
}