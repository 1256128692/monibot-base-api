package cn.shmedo.monitor.monibotbaseapi.model.response.notify;

import lombok.Data;

/**
 * @Author wuxl
 * @Date 2024/3/5 13:48
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.notify
 * @ClassName: NotifyByProjectID
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class NotifyListByProjectID {
    private Integer notifyID;
    private Integer type;
    private Integer projectID;
    /**
     * 监测项目ID（当且仅当relationType=1时，非空）
     */
    private Integer monitorItemID;
    /**
     * 监测点ID（当且仅当relationType=1时，非空）
     */
    private Integer monitorPointID;
    /**
     * 是否是历史报警（当且仅当relationType=2时，非空）
     */
    private Boolean historyFlag;
}
