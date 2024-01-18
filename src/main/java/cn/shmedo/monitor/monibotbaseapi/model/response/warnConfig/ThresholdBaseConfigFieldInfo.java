package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-17 17:37
 */
@Data
public class ThresholdBaseConfigFieldInfo {
    private Integer fieldID;
    private String fieldName;
    private String fieldToken;
    private List<WarnLevelAliasInfo> aliasConfigList;
}
