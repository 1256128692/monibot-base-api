package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnThresholdConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnThresholdConfigListParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.MonitorWithThresholdConfigCountInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnThresholdMonitorPointInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnlog.DataWarnDetailInfo;
import jakarta.annotation.Nonnull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:20
 */
public interface TbWarnThresholdConfigMapper extends BasicMapper<TbWarnThresholdConfig> {
    WarnThresholdConfig getInfoById(@Nonnull Integer id);

    List<MonitorWithThresholdConfigCountInfo> selectMonitorWithThresholdConfigCountByProjectID(@Param("projectID") Integer projectID,
                                                                                               @Param("platform") Integer platform);

    List<WarnThresholdMonitorPointInfo> selectWarnThresholdConfigList(@Param("param") QueryWarnThresholdConfigListParam param);

    /**
     * 查询阈值配置关联的信息
     */
    DataWarnDetailInfo selectDataWarnRelativeByID(Integer id);
}
