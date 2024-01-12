package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import org.apache.ibatis.annotations.Param;
import reactor.util.function.Tuple2;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:04
 */
public interface TbWarnBaseConfigMapper extends BasicMapper<TbWarnBaseConfig> {

    List<TbWarnBaseConfig> queryByCompanyIDAndPlatform(@Param("list") List<Tuple2<Integer, Integer>> param);
}
