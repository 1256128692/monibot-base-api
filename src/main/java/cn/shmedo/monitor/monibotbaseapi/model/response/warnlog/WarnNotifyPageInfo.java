package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import cn.shmedo.monitor.monibotbaseapi.model.response.third.NotifyPageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-26 13:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WarnNotifyPageInfo extends NotifyPageInfo {
    private Integer warnLogID;
    private Integer warnType;
}
