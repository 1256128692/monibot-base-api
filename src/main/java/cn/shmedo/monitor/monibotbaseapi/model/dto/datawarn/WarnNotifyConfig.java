package cn.shmedo.monitor.monibotbaseapi.model.dto.datawarn;

import cn.shmedo.monitor.monibotbaseapi.model.dto.UserContact;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Chengfs on 2024/1/15
 */
@Data
public class WarnNotifyConfig {

    /**
     * 报警等级，仅数据报警此字段不为空<br/>
     * 值范围 1-4
     */
    private List<Integer> levels;

    /**
     * 通知用户 联系方式<br/>
     * key: 用户id  value: {@link UserContact}
     */
    private Map<Integer, UserContact> contacts;

    /**
     * 通知方式 <br/>
     * 1.平台消息 2.短信 3.邮件
     */
    private List<Integer> methods;

}