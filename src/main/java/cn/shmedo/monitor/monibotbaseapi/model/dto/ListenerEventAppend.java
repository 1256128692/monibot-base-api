package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.shmedo.iot.entity.api.CurrentSubject;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-06 13:29
 */
public record ListenerEventAppend(Integer companyID, String accessToken) {
    public static ListenerEventAppend of(CurrentSubject currentSubject, String accessToken) {
        return new ListenerEventAppend(currentSubject.getCompanyID(), accessToken);
    }

    public static ListenerEventAppend of(Integer companyID, String accessToken) {
        return new ListenerEventAppend(companyID, accessToken);
    }
}
