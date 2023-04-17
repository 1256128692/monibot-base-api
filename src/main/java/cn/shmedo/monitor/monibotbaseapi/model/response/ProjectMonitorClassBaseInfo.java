package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectMonitorClassBaseInfo {

    /**
     * 主键
     */
    private Integer ID;

    /**
     * 监测类别,0:环境监测 1:安全监测 2:工情监测 3:防洪调度指挥监测 4:视频监测
     */
    private Integer monitorClass;

    private String monitorClassCnName;

    /**
     * 项目ID
     */
    private Integer projectID;

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 查询密度
     */
    private String density;
}
