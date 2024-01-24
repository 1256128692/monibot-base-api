package cn.shmedo.monitor.monibotbaseapi.service.third.notify;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.MailNotify;
import cn.shmedo.monitor.monibotbaseapi.model.param.third.notify.SmsNotify;
import feign.Headers;
import feign.RequestLine;

public interface MdNotifyService {

    /**
     * 发送阿里云短信
     *
     * @param pa        请求参数 {@link SmsNotify}
     * @return 结果 {@link ResultWrapper}
     */
    @RequestLine("POST /SendAliSms")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Void> sendSms(SmsNotify pa);

    /**
     * 发送阿里云邮件
     *
     * @param pa        请求参数 {@link MailNotify}
     * @return 结果 {@link ResultWrapper}
     */
    @RequestLine("POST /SendAliEms")
    @Headers({"appKey: {appKey}", "appSecret: {appSecret}"})
    ResultWrapper<Void> sendMail(MailNotify pa);
}
