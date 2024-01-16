package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.CompanyPlatformParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnNotifyConfigDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.UpdateWarnNotifyConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nonnull;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
public interface ITbWarnNotifyConfigService extends IService<TbWarnNotifyConfig> {
    WarnNotifyConfigDetail queryWarnNotifyConfigDetail(QueryWarnNotifyConfigDetailParam param);

    void updateWarnNotifyConfig(UpdateWarnNotifyConfigParam param);

    WarnNotifyConfigInfo queryWarnNotifyConfigList(CompanyPlatformParam param, TbWarnBaseConfig tbWarnBaseConfig);

    void deleteWarnNotifyConfigBatch(List<Integer> notifyConfigIDList);

    WarnNotifyConfig queryByProjectIDAndPlatform(@Nonnull Integer projectID,
                                                 @Nonnull Integer platform, @Nonnull Integer notifyType);
}
