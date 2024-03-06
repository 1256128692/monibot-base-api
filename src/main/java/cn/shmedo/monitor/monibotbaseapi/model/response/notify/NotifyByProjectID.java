package cn.shmedo.monitor.monibotbaseapi.model.response.notify;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Set;

/**
 * @Author wuxl
 * @Date 2024/3/5 13:48
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.notify
 * @ClassName: NotifyByProjectID
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class NotifyByProjectID {
    private Set<Integer> dataList;
    private Set<Integer> deviceList;
    private Set<Integer> eventList;
    private Set<Integer> workOrderList;
    private List<NotifyListByProjectID> notifyListByProjectIDList;
}
