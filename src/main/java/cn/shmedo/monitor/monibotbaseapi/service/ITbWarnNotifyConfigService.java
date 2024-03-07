package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnBaseConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.CompanyPlatformParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnNotifyConfigDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.UpdateWarnNotifyConfigParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
public interface ITbWarnNotifyConfigService extends IService<TbWarnNotifyConfig> {
    WarnNotifyConfigDetail queryWarnNotifyConfigDetail(QueryWarnNotifyConfigDetailParam param);

    void addWarnNotifyConfig(TbWarnNotifyConfig tbWarnNotifyConfig, List<Integer> projectIDList, Integer userID);

    void updateWarnNotifyConfig(UpdateWarnNotifyConfigParam param, Integer userID);

    WarnNotifyConfigInfo queryWarnNotifyConfigList(CompanyPlatformParam param, TbWarnBaseConfig tbWarnBaseConfig);

    void deleteWarnNotifyConfigBatch(List<Integer> notifyConfigIDList);

    Map<Integer, Map<Integer, WarnNotifyConfig>> queryByProjectIDAndPlatform(@Nonnull Integer projectID,
                                                 @Nonnull Integer platform, @Nonnull Integer notifyType);

}
