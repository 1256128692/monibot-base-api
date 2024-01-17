package cn.shmedo.monitor.monibotbaseapi.service.notify;

import cn.hutool.core.util.StrUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.exception.CustomBaseException;
import cn.shmedo.monitor.monibotbaseapi.config.FileConfig;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.SmsNotify;
import cn.shmedo.monitor.monibotbaseapi.service.third.auth.UserService;
import cn.shmedo.monitor.monibotbaseapi.service.third.notify.MdNotifyService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;

/**
 * 通知服务整合
 *
 * @author Chengfs on 2024/1/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements NotifyService {

    private final FileConfig config;
    private final MdNotifyService mdNotifyService;
    private final UserService userService;

    @Override
    public boolean smsNotify(String signName, String templateCode, Supplier<Collection<SmsNotify.Param>> paramSupplier,
                             @Nonnull String... phoneNumbers) {
        SmsNotify param = SmsNotify.builder().signName(signName).templateCode(templateCode)
                .userPhones(Arrays.stream(phoneNumbers).distinct().toList())
                .params(paramSupplier.get().stream()
                        .map(e -> new SmsNotify.Param(e.keyName(), formatSmsParam(e.value()))).toList()).build();
        ResultWrapper<Void> result = mdNotifyService.sendSms(param);
        return Optional.of(result.apiSuccess()).filter(e -> e)
                .orElseThrow(() -> new CustomBaseException(result.getCode(), result.getMsg()));
    }

    @Override
    public boolean smsNotify(String signName, String templateCode, Map<String, Object> params, @Nonnull String... phoneNumbers) {
        return smsNotify(signName, templateCode, () -> params.entrySet().stream()
                .map(e -> new SmsNotify.Param(e.getKey(), e.getValue().toString())).toList(), phoneNumbers);
    }

    @Override
    public List<Integer> sysNotify(Integer companyID, Supplier<Collection<SysNotify.Notify>> notifySupplier,
                                   @Nonnull Integer... userIDs) {

        SysNotify param = SysNotify.builder().companyID(companyID)
                .notifyUsers(Arrays.stream(userIDs).distinct().toList())
                .notifyList(notifySupplier.get()).build();

        ResultWrapper<List<Integer>> result = userService.addSysNotify(param,
                config.getAuthAppKey(), config.getAuthAppSecret());

        Optional.of(result).filter(ResultWrapper::apiSuccess)
                .orElseThrow(() -> new CustomBaseException(result.getCode(), result.getMsg()));

        return Optional.ofNullable(result.getData()).orElse(List.of());
    }

    /**
     * 格式化短信参数内容，防止超过20个字符
     */
    private String formatSmsParam(String value) {
        if (value != null) {
            return value.substring(0, Math.min(value.length(), 20));
        }
        return StrUtil.EMPTY;
    }
}