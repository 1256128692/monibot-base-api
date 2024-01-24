package cn.shmedo.monitor.monibotbaseapi.model.param.third.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author Chengfs on 2024/1/24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailNotify {

    /**
     * 接收短信的 手机号
     */
    private Collection<String> userMailAddressList;

    /**
     * 邮件签名
     */
    private String mailTag;

    /**
     * 内容是否为html
     */
    private Boolean isHtml;

    /**
     * 邮件内容
     */
    private String mailContent;
}