package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

@Data
public class HistoryLiveInfo {

    /**
     * 历史回放地址
     */
    private String historyLiveAddress;

    private String ysToken;

}
