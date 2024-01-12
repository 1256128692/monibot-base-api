package cn.shmedo.monitor.monibotbaseapi.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbWarnNotifyConfigMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnNotifyConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.user.QueryUserNoPageParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.warnConfig.QueryWarnNotifyConfigDetailParam;
import cn.shmedo.monitor.monibotbaseapi.model.response.third.UserNoPageInfo;
import cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig.WarnNotifyConfigDetail;
import cn.shmedo.monitor.monibotbaseapi.service.ITbWarnNotifyConfigService;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 17:39
 */
@Service
@RequiredArgsConstructor
public class TbWarnNotifyConfigServiceImpl extends ServiceImpl<TbWarnNotifyConfigMapper, TbWarnNotifyConfig> implements ITbWarnNotifyConfigService {
    private final UserService userService;
    private final FileConfig fileConfig;
    private final TbProjectInfoMapper tbProjectInfoMapper;

    @Override
    public WarnNotifyConfigDetail queryWarnNotifyConfigDetail(QueryWarnNotifyConfigDetailParam param) {
        Integer companyID = param.getCompanyID();
        WarnNotifyConfigDetail detail = param.getDetail();
        Optional.ofNullable(detail.getUserIDStr()).filter(JSONUtil::isTypeJSONArray).map(JSONUtil::parseArray)
                .map(u -> JSONUtil.toList(u, Integer.class)).filter(CollUtil::isNotEmpty).ifPresent(u -> {
                    Set<Integer> externalUserIDSet = Optional.of(new QueryUserNoPageParam(companyID, null, null, null, null, null, true, u, null))
                            .map(w -> userService.queryUserNoPage(w, fileConfig.getAuthAppKey(), fileConfig.getAuthAppSecret()))
                            .filter(ResultWrapper::apiSuccess).map(ResultWrapper::getData).map(w ->
                                    w.stream().map(UserNoPageInfo::getUser).filter(s -> !companyID.equals(s.getCompanyID()))
                                            .map(UserNoPageInfo.TbUserEx::getUserID).collect(Collectors.toSet())).orElse(Set.of());
                    detail.setExternalIDList(u.stream().filter(externalUserIDSet::contains).toList());
                    detail.setUserIDList(u.stream().filter(w -> !externalUserIDSet.contains(w)).toList());
                });
        Optional.ofNullable(detail.getProjectIDStr()).filter(JSONUtil::isTypeJSONArray).map(JSONUtil::parseArray)
                .map(u -> JSONUtil.toList(u, Integer.class)).filter(CollUtil::isNotEmpty).ifPresent(u -> detail
                        .setProjectIDList(u.size() > 1 || u.stream().findAny().orElse(0) != -1 ? u :
                                tbProjectInfoMapper.selectListByCompanyIDAndProjectIDList(companyID, null)
                                        .stream().map(TbProjectInfo::getID).toList()));
        return detail;
    }
}
