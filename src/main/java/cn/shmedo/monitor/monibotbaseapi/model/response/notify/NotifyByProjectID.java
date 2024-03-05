package cn.shmedo.monitor.monibotbaseapi.model.response.notify;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

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
    private List<Integer> dataList;
    private List<Integer> deviceList;
    private List<Integer> eventList;
    private List<Integer> workOrderList;
}
