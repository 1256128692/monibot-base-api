package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 公告发布状态
 *
 * @author youxian.kong@shmedo.cn
 * @date 2024-02-29 17:21
 */
@Getter
@RequiredArgsConstructor
public enum BulletinPublishStatus {
    UNPUBLISHED(0, "未发布"), PUBLISHED(1, "已发布");
    private final Integer code;
    private final String desc;
}
