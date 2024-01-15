package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:12
 */
public interface TbWarnNotifyConfigMapper extends BasicMapper<TbWarnNotifyConfig> {

    @MapKey("projectID")
    Map<Integer, List<TbWarnNotifyConfig>> queryByProjectID(@Param("notifyType") Integer notifyType,
                                                   @Param("list") List<Integer> projectIDList);
}
