package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataWarnLevelType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:47
 */
public interface ITbWarnBaseConfigService extends IService<TbWarnBaseConfig> {
    TbWarnBaseConfig queryByCompanyIDAndPlatform(final Integer companyID, final Integer platform);

    default TbWarnBaseConfig getDefault(final Integer companyID, final Integer platform) {
        return new TbWarnBaseConfig(null, companyID, platform, 1, 1, 1, null, null, null, null);
    }

    default Set<Integer> getWarnLevelSet(final Integer companyID, final Integer platform) {
        return DataWarnLevelType.fromCode(queryByCompanyIDAndPlatform(companyID, platform).getWarnLevelType()).getWarnLevelSet();
    }
}
