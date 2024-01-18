package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnLevelAlias;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.ThresholdBaseConfigFieldInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:08
 */
public interface TbWarnLevelAliasMapper extends BasicMapper<TbWarnLevelAlias> {
    List<ThresholdBaseConfigFieldInfo> selectThresholdBaseConfigFieldInfoList(@Param("platform") Integer platform,
                                                                              @Param("monitorItemID") Integer monitorItemID);
}
