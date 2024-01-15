package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.util.StrUtil;
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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Map<String, Map<Integer, WarnNotifyConfig>> groupByPlatformAndProject(@NotNull Integer notifyType,
                                                                                 @NotNull List<Integer> projectIDList) {
        return baseMapper.queryByProjectID(notifyType, projectIDList)
                .entrySet()
                .stream().flatMap(e -> e.getValue().stream().map(i -> Tuples.of(e.getKey(), i)))
                .collect(Collectors.groupingBy(e -> e.getT2().getCompanyID() + StrUtil.COLON + e.getT2().getPlatform(),
                        Collectors.toMap(Tuple2::getT1, v -> {
                            TbWarnNotifyConfig config = v.getT2();
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
                        })));
    }
}
