package cn.shmedo.monitor.monibotbaseapi.model.response.third.auth;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static cn.shmedo.monitor.monibotbaseapi.model.param.third.auth.SysNotify.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-02 17:15
 */
@Data
@Builder
@NoArgsConstructor
public class NotifyDetailInfo {
    private Integer notifyID;
    private String name;
    private String content;
    private Date time;
    private Integer userID;
    private Type type;
    private Status status;

    public NotifyDetailInfo(Integer notifyID, String name, String content, Date time, Integer userID, Type type, Status status) {
        this.notifyID = notifyID;
        this.name = name;
        this.content = content;
        this.time = time;
        this.userID = userID;
        this.type = type;
        this.status = status;
    }
}
