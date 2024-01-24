package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnLevelAliasInfo;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 13:44
 */
@Data
@Builder
public class DataWarnHistoryListInfo {
    private Date warnTime;
    private Integer compareMode;
    private String threshold;
    private WarnLevelAliasInfo aliasConfig;
}
