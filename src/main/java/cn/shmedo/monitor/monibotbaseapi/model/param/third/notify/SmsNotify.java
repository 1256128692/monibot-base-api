package cn.shmedo.monitor.monibotbaseapi.model.param.third.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @author Chengfs on 2024/1/12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsNotify {

    /**
     * 接收短信的 手机号
     */
    private Collection<String> userPhones;

    /**
     * 短信签名
     */
    private String signName;

    /**
     * 短信模板代码
     */
    private String templateCode;

    /**
     * 模板参数集
     */
    private Collection<Param> params;

    public record Param(String keyName, String value) {
    }
}