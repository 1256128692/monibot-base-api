package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn.WarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserContactParam;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
@Service
@AllArgsConstructor
public class TbWarnNotifyConfigServiceImpl extends ServiceImpl<TbWarnNotifyConfigMapper, TbWarnNotifyConfig> implements ITbWarnNotifyConfigService {

    private final UserService userService;
    private final FileConfig fileConfig;

    @Override
    public WarnNotifyConfig queryByProjectIDAndPlatform(@NotNull Integer projectID, @NotNull Integer platform, @NotNull Integer notifyType) {
        TbWarnNotifyConfig config = baseMapper.queryByProjectIDAndPlatform(projectID, platform, notifyType);
        if (config != null) {
            ResultWrapper<Map<Integer, String>> wrapper = userService.queryUserContact(QueryUserContactParam.builder()
                    .depts(JSONUtil.toList(config.getDepts(), Integer.class))
                    .roles(JSONUtil.toList(config.getRoles(), Integer.class))
                    .users(JSONUtil.toList(config.getUsers(), Integer.class))
                    .build(), fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret());

            WarnNotifyConfig item = new WarnNotifyConfig();
            item.setLevels(JSONUtil.toList(config.getWarnLevel(), Integer.class));
            item.setContacts(Optional.ofNullable(wrapper.getData()).filter(e -> !e.isEmpty()).orElse(Map.of()));
            item.setMethods(JSONUtil.toList(config.getNotifyMethod(), Integer.class));
            return item;
        }
        return null;
    }
}
