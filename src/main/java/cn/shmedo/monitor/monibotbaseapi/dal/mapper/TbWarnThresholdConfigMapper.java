package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnThresholdConfigInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:20
 */
public interface TbWarnThresholdConfigMapper extends BasicMapper<TbWarnThresholdConfig> {

    List<WarnThresholdConfigInfo> listInfoById(@Param("list") List<Integer> list);
}
