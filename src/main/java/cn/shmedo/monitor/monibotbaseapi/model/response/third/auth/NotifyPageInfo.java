package cn.shmedo.monitor.monibotbaseapi.model.response.third.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-26 10:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyPageInfo {
    private Integer notifyID;
    private Integer type;
    private Integer serviceID;
    private String name;
    private String content;
    private Integer status;
    private Date time;
    private Integer userID;
}
