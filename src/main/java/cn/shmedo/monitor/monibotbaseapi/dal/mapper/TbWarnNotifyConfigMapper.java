package cn.shmedo.monitor.monibotbaseapi.dal.mapper;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.DataWarnConfigInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;
import jakarta.annotation.Nonnull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:12
 */
public interface TbWarnNotifyConfigMapper extends BasicMapper<TbWarnNotifyConfig> {
    WarnNotifyConfigDetail selectWarnNotifyConfigDetailByID(@Param("companyID") Integer companyID,
                                                            @Param("platform") Integer platform,
                                                            @Param("id") Integer id);

    List<DataWarnConfigInfo> selectWarnNotifyConfigList(@Param("companyID") Integer companyID,
                                                        @Param("platform") Integer platform,
                                                        @Param("projectID") Integer projectID,
                                                        @Param("notifyType") Integer notifyType);

    List<TbWarnNotifyConfig> queryByProjectIDAndPlatform(@Nonnull Integer projectID, @Nonnull Integer platform, @Nonnull Integer notifyType);

    List<TbWarnNotifyConfig> queryByCompanyIDAndPlatformID(@Param("companyID") Integer companyID,
                                                     @Param("platform") Integer platform,
                                                     @Param("notifyType") Integer notifyType,
                                                     @Param("projectID") Integer projectID);
}
