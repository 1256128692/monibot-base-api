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
    private Integer type;
    private Integer projectID;
}
