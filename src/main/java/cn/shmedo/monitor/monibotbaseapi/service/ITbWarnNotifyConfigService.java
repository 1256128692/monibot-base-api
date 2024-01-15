package cn.shmedo.monitor.monibotbaseapi.service;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
public interface ITbWarnNotifyConfigService extends IService<TbWarnNotifyConfig> {

    /**
     * 按 companyID:platform 和 projectID 分组
     *
     * @param notifyType    通知类型 1.设备报警通知 2.数据报警通知
     * @param projectIDList 项目ID集
     * @return map
     */
    Map<String, Map<Integer, WarnNotifyConfig>> groupByPlatformAndProject(@Nonnull Integer notifyType,
                                                                           @Nonnull List<Integer> projectIDList);
}
