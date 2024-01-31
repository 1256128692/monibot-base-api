package cn.shmedo.monitor.monibotbaseapi.service.notify;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.SmsNotify;
import jakarta.annotation.Nonnull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface NotifyService {

    /**
     * 发送短信通知
     *
     * @param signName     短信签名
     * @param templateCode 模板编号
     * @param params       短信参数
     * @param phoneNumbers 目标手机号
     */
    boolean smsNotify(String signName, String templateCode, Map<String, Object> params,
                      @Nonnull String... phoneNumbers);

    /**
     * 发送短信通知
     *
     * @param signName      短信签名
     * @param templateCode  模板编号
     * @param paramSupplier 参数提供器
     * @param phoneNumbers  目标手机号
     */
    boolean smsNotify(String signName, String templateCode, Supplier<Collection<SmsNotify.Param>> paramSupplier,
                      @Nonnull String... phoneNumbers);

    /**
     * 发送平台通知
     *
     * @param companyID      企业id
     * @param notifySupplier 内容提供器
     * @param userIDs        目标用户id
     * @return 通知id集合，null安全
     */
    List<Integer> sysNotify(Integer companyID, Supplier<Collection<SysNotify.Notify>> notifySupplier,
                            @Nonnull Integer... userIDs);

    /**
     * 发送邮件通知
     *
     * @param mailTag         邮件标签
     * @param isHtml          内容是否为html
     * @param contentSupplier 内容提供器
     * @param mailAddresses   目标邮箱
     * @return 是否成功
     */
    boolean mailNotify(String mailTag, boolean isHtml, Supplier<String> contentSupplier,
                       @Nonnull String... mailAddresses);
}
