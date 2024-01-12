package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:47
 */
public interface ITbWarnBaseConfigService extends IService<TbWarnBaseConfig> {
    TbWarnBaseConfig queryByCompanyIDAndPlatform(final Integer companyID, final Integer platform);

    default TbWarnBaseConfig getDefault(final Integer companyID, final Integer platform) {
        return new TbWarnBaseConfig(null, companyID, platform, 1, 1, 1, null, null, null, null);
    }

    Map<Integer, Map<Integer, TbWarnBaseConfig>> groupByCompanyIDAndPlatform(List<Tuple2<Integer, Integer>> param);
}
