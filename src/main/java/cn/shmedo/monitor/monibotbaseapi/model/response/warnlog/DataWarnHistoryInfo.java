package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnBaseConfigInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 13:43
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataWarnHistoryInfo extends WarnBaseConfigInfo {
    private List<DataWarnHistoryListInfo> dataList;
}
