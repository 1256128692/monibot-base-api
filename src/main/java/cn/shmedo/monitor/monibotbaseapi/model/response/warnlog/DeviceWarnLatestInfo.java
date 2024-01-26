package cn.shmedo.monitor.monibotbaseapi.model.response.warnlog;

import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-26 14:28
 */
@Data
public class DeviceWarnLatestInfo {
    private Integer warnLogID;
    private Integer notifyID;
    private String warnName;
    private Date warnTime;
    private Integer deviceType;
    private String deviceModel;
    private String deviceToken;
}
