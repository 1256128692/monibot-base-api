package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nonnull;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
public interface ITbWarnNotifyConfigService extends IService<TbWarnNotifyConfig> {

    WarnNotifyConfig queryByProjectIDAndPlatform(@Nonnull Integer projectID,
                                                 @Nonnull Integer platform, @Nonnull Integer notifyType);
}
